package ezcli.modules.terminal;

import ezcli.modules.ezcli_core.Module;
import ezcli.modules.ezcli_core.Ezcli;
import ezcli.modules.ezcli_core.global_io.Command;
import ezcli.modules.ezcli_core.global_io.InputHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Terminal module. Used to interact with system, as if you were running commands on your system terminal.
 */
public class Terminal extends Module {

    // Input handler for this module
    private TermInputProcessor inputProcessor;

    private boolean exit;

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

        String[] split = rawCommand.split("&&");
        System.out.println();

        for (String command : split) {
            command = removeSpaces(command);

            if (command.startsWith("cd")) {
                changeDir(command);
                continue;
            }

            if ("exit".equals(command)) {
                inputProcessor.setCursorPos(0);
                inputProcessor.setCommand("");
                exit = true;
                return;
            } else if ("".equals(command) || containsOnlySpaces(command) || "help".equals(command)) {
                if ("help".equals(command))
                    help();
                inputProcessor.setCommand("");
                System.out.print(Ezcli.prompt);
                return;
            }

            ProcessBuilder pb;
            Process p = null;
            try {
                pb = new ProcessBuilder(command);
                pb.inheritIO(); // Make program and process share IO to allow user to interact with program
                pb.directory(new File(Ezcli.currDir)); // Set working directory for command
                p = pb.start();
            } catch (IOException | IllegalArgumentException e) {
                System.out.println("Parsing command \"" + command + "\" failed, enter \"help\" for help using module.");
            }

            if (p != null) {
                while (p.isAlive()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                p.destroy();
            }
        }

        inputProcessor.setCommand("");
        System.out.print(Ezcli.prompt);
    }

    /**
     * Changes the terminals directory, since the system does not interpret chdir commands.
     * Attempts to emulate the "cd" command.
     *
     * @param command chdir command to parse
     */
    private void changeDir(String command) {
        String[] chdirSplit = command.split(" ");

        if (chdirSplit.length != 2 || !"cd".equals(chdirSplit[0])) {
            System.out.println("Invalid chdir command passed.");
        } else {
            String dirChange = chdirSplit[1];
            String currDir = Ezcli.currDir;
            File f;

            // "cd .."
            if ("..".equals(dirChange) && !"/".equals(Ezcli.currDir)) {
                String[] dirSplit = Ezcli.currDir.split("/");
                StringBuilder newPath = new StringBuilder();

                for (int i = 0; i < dirSplit.length - 1; i++)
                    newPath.append(dirSplit[i]).append("/");

                System.setProperty("user.dir", newPath.toString());
                Ezcli.currDir = newPath.toString();
                return;
            }

            // "cd /home/username/example/"
            if (dirChange.startsWith("/"))
                f = Paths.get(dirChange).toFile();

            // "cd ~/example/"
            else if (dirChange.startsWith("~") && dirChange.length() > 1)
                f = Paths.get(Ezcli.USER_HOME_DIR + dirChange.substring(1)).toFile();

            // "cd ~"
            else if ("~".equals(dirChange))
                f = Paths.get(Ezcli.USER_HOME_DIR).toFile();

            // "cd example/src/morexamples/"
            else
                f = Paths.get(currDir + dirChange).toFile();

            if (f.exists() && f.isDirectory()) {
                System.setProperty("user.dir", f.getAbsolutePath());
                Ezcli.currDir = f.getAbsolutePath() + "/";
            } else {
                System.out.println("Please enter a valid directory to change to.");
            }
        }
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
        System.out.println("All input will be passed to the system when a \n" +
                "newline character is detected (enter key pressed)");
        System.out.println("To change directory, enter: \"cd [somedir]\".");
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
        inputProcessor.getKeyProcessor().newLineEvent();

        System.out.println("\n\n");

        System.out.println("That should have printed your working directory!");
        System.out.println("We will now be exiting this module, please wait.\n\n");
        sleep(5);
    }
}