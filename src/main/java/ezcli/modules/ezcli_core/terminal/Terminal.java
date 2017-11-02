package ezcli.modules.ezcli_core.terminal;

import ezcli.modules.ezcli_core.Module;
import ezcli.modules.ezcli_core.Ezcli;
import ezcli.modules.ezcli_core.global_io.input.Input;
import ezcli.modules.ezcli_core.global_io.InputHandler;
import ezcli.modules.ezcli_core.global_io.KeyHandler;

import java.io.IOException;
import java.util.Scanner;

/**
 * Terminal module. Used to interact with system, as if you were running commands on your system terminal.
 */
public class Terminal extends Module {

    private boolean exit;

    private TermInputProcessor inputProcessor;

    public Terminal(String mapWith) {
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

        Runtime r = Runtime.getRuntime();
        Process p = null;
        try {
            p = r.exec(command);
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Parsing command failed, enter \"t-help\" for help using module.");
        }

        /*
         * While system program is running, print output and listen for signals to cancel or force quit
         */
        if (p != null) {
            Scanner in = new Scanner(p.getInputStream());
            String input;
            while (p.isAlive() && (input = in.nextLine()) != null) {
                System.out.println(input);
                try {
                    if (KeyHandler.signalCatch((char)Input.read(false)))
                        break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            p.destroy();
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
        // to be implemented
    }
}
