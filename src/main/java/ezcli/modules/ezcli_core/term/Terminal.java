package ezcli.modules.ezcli_core.term;

import ezcli.modules.ezcli_core.Module;
import ezcli.modules.ezcli_core.Ezcli;
import ezcli.modules.ezcli_core.global_io.Input;
import ezcli.modules.ezcli_core.global_io.KeyHandler;

import java.io.IOException;
import java.util.Scanner;

/**
 * Terminal module. Used to interact with system, as if you were running commands on your system terminal.
 */
public class Terminal extends Module {

    static boolean parse;
    private static boolean exit;

    private TermInputProcessor inputProcessor;

    public Terminal() {
        super();
        inputProcessor = new TermInputProcessor();
    }

    @Override
    public void run() {
        exit = false;

        System.out.println("\nEntered terminal mode");
        System.out.print(Ezcli.prompt);
        while (!exit) {
            inputProcessor.process();
            if (parse)
                parse(TermInputProcessor.getCommand());
        }
    }

    @Override
    public void parse(String command) {
        parse = false;
        System.out.println();

        if ("exit".equals(command)) {
            exit = true;
            return;
        } else if (command.equals("") || containsOnlySpaces(command) || "t-help".equals(command)) {
            if ("t-help".equals(command))
                help();
            TermInputProcessor.setCommand("");
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

        TermInputProcessor.setCommand("");
        System.out.print(Ezcli.prompt);
    }

    /**
     * Determines if a string is composed only of spaces.
     *
     * @param s string to check
     * @return true if s is composed of only spaces, false if there is a character in it
     */
    static boolean containsOnlySpaces(String s) {
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
