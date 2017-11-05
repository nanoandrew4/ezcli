package ezcli.modules.ezcli_core.terminal;

import ezcli.modules.ezcli_core.Module;
import ezcli.modules.ezcli_core.Ezcli;
import ezcli.modules.ezcli_core.global_io.Command;
import ezcli.modules.ezcli_core.global_io.input.Input;
import ezcli.modules.ezcli_core.global_io.InputHandler;
import ezcli.modules.ezcli_core.global_io.KeyHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Terminal module. Used to interact with system, as if you were running commands on your system terminal.
 */
public class Terminal extends Module {

    private boolean exit;

    private TermInputProcessor inputProcessor;

    public Terminal(String mapWith) {
        super("Terminal");
        init(this, mapWith);
        inputProcessor = new TermInputProcessor(this);
    }

    protected TermInputProcessor getInputProcessor() {
        return inputProcessor;
    }

    @Override
    public void run() {
        exit = false;

        System.out.println("Entered terminal mode");
        System.out.print(Ezcli.prompt);
        while (!exit) {
            inputProcessor.process(InputHandler.getKey());
        }
    }

    @Override
    public void parse(String rawCommand) {
        System.out.println();

        String command = removeSpaces(rawCommand); // removes blank space before and after command if any exists

        if ("exit".equals(command)) {
            inputProcessor.setCursorPos(0);
            inputProcessor.setCommand("");
            exit = true;
            return;
        } else if ("".equals(command) || containsOnlySpaces(command) || "t-help".equals(command)) {
            if ("t-help".equals(command))
                help();
            inputProcessor.setCommand("");
            System.out.print(Ezcli.prompt);
            return;
        }

        ProcessBuilder pb;
        Process p = null; // system command to run
        try {
            pb = new ProcessBuilder(command);
            pb.inheritIO(); // make program and process share IO
            p = pb.start(); // start process
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Parsing command \"" + command + "\" failed, enter \"t-help\" for help using module.");
        }

        /*
         * While system program is running, sleep.
         */
        if (p != null) {
            while (p.isAlive()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            p.destroy(); // destroy once done
        }

        inputProcessor.setCommand("");
        System.out.print(Ezcli.prompt);
    }

    /**
     * Removes blank space before and after command if any exists.
     *
     * @param command Command to parse
     * @return Command without white space
     */
    protected static String removeSpaces(String command) {

        int fpos = 0;
        for (int i = 0; i < command.length(); i++) {
            if (command.charAt(i) == ' ')
                fpos++;
            else
                break;
        }
        int bpos = command.length() > 0 ? command.length() : 0;
        for (int i = command.length() - 1; i > 0; i--) {
            if (command.charAt(i) == ' ')
                bpos--;
            else
                break;
        }

        return command.substring(fpos, bpos);
    }

    /**
     * Determines if a string is composed only of spaces.
     *
     * @param s string to check
     * @return true if s is composed of only spaces, false if there is a character in it
     */
    protected static boolean containsOnlySpaces(String s) {
        for (int i = 0; i < s.length(); i++)
            if (s.charAt(i) != ' ')
                return false;
        return true;
    }

    @Override
    public void help() {
        System.out.println("This module interacts directly with the system.");
        System.out.println("All input will be passed to the system when a \nnewline character is detected (enter key pressed)");
        System.out.println("To return to Interactive module, enter \"exit\".");
    }

    @Override
    public void tour() {
        System.out.println("This is the " + moduleName + " module.");
        System.out.println("This module deals directly with your system terminal.");
        System.out.println("Anything input here, with the exception of the commands");
        System.out.println("\"exit\" and \"t-help\" will be passed directly to the system.");
        if (sleep(3) != Command.NONE) return;

        System.out.println("For example, lets pass \"dir\"");
        if (sleep(1.2) != Command.NONE) return;
        inputProcessor.process('d');
        if (sleep(0.7) != Command.NONE) return;
        inputProcessor.process('i');
        if (sleep(0.7) != Command.NONE) return;
        inputProcessor.process('r');
        if (sleep(1.2) != Command.NONE) return;
        inputProcessor.getKeyHandler().newLineEvent();

        System.out.println("\n\n");

        System.out.println("That should have printed your working directory!");
        System.out.println("We will now be exiting this module, please wait.\n\n");
        sleep(5);
    }
}
