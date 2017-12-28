package ezcli.modules.ezcli_core.terminal;

import ezcli.modules.ezcli_core.EventState;
import ezcli.modules.ezcli_core.Module;
import ezcli.modules.ezcli_core.Ezcli;
import ezcli.modules.ezcli_core.global_io.Command;
import ezcli.modules.ezcli_core.global_io.InputHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

/**
 * Terminal module. Used to interact with system, as if you were running commands on your system terminal.
 */
public class Terminal extends Module {

    // Input handler for this module
    public TermInputProcessor inputProcessor;

    public LinkedList<String> inputInSession;

    private final static int maxLinesInHistory = 10000;

    private boolean exit;

    public Terminal() {
        super("Terminal");
        init(this, "t");

        inputProcessor = new TermInputProcessor(this);
        inputInSession = new LinkedList<>();
    }

    @Override
    public EventState getWhenToRun() {
        return null;
    }

    private void writeCommandsToFile() {
        File historyFile = new File(Ezcli.USER_HOME_DIR + ".ezcli_history");

        try {
            historyFile.createNewFile();
        } catch (IOException e) {
            System.err.println("Error creating new history file in directory: " + Ezcli.USER_HOME_DIR);
            return;
        }

        List<String> original;
        try {
            original = Files.readAllLines(Paths.get(historyFile.getAbsolutePath()));
            if (original.size() == 0) {
                // populate
            }
        } catch (IOException e) {
            System.err.println("Error reading lines from original history file");
            return;
        }

        if (historyFile.delete()) {
            historyFile = new File(Ezcli.USER_HOME_DIR + ".ezcli_history");

            PrintWriter pw;
            try {
                pw = new PrintWriter(historyFile);
            } catch (FileNotFoundException e) {
                System.out.println("Error creating print writer");
                return;
            }

            int startPos = original.size() + inputInSession.size() > maxLinesInHistory ? inputInSession.size() : 0;
            for (int i = startPos; i < original.size(); i++)
                pw.println(original.get(i));

            for (String s : inputInSession)
                pw.println(s);

            pw.close();
        }
    }

    @Override
    public void run() {
        exit = false;
//        Ezcli.prompt = Ezcli.promptColor + Ezcli.currDir + " >> " + ColorOutput.DEFAULT_COLOR;

        System.out.println("Entered terminal mode");
        System.out.print(Ezcli.prompt);
        while (!exit) {
            inputProcessor.process(InputHandler.getKey());
        }

        writeCommandsToFile();
//        Ezcli.prompt = Ezcli.promptColor + ">> " + ColorOutput.DEFAULT_COLOR;
    }

    public void parse(String rawCommand) {

        inputInSession.add(rawCommand);

        String[] split = rawCommand.split("&&");
        System.out.println();

        for (String command : split) {
            command = command.trim();

            if (command.startsWith("cd")) {
                changeDir(command);
                continue;
            }

            if ("exit".equals(command)) {
                inputProcessor.setCursorPos(0);
                inputProcessor.setCommand("");
                exit = true;
                return;
            } else if ("".equals(command) || "".equals(command.trim()) || "help".equals(command)) {
                if ("help".equals(command))
                    help();
                inputProcessor.setCommand("");
                System.out.print(Ezcli.prompt);
                return;
            }

            ProcessBuilder pb;
            Process p = null;
            try {
                String[] commandArr = command.split(" ");
                pb = new ProcessBuilder(commandArr);
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
        if (Ezcli.sleep(3) != Command.NONE) return;

        System.out.println("For example, lets pass \"dir\"");
        if (Ezcli.sleep(1.2) != Command.NONE) return;
        inputProcessor.process('d');
        if (Ezcli.sleep(0.7) != Command.NONE) return;
        inputProcessor.process('i');
        if (Ezcli.sleep(0.7) != Command.NONE) return;
        inputProcessor.process('r');
        if (Ezcli.sleep(1.2) != Command.NONE) return;
        inputProcessor.getKeyProcessor().newLineEvent();

        System.out.println("\n\n");

        System.out.println("That should have printed your working directory!");
        System.out.println("We will now be exiting this module, please wait.\n\n");
        Ezcli.sleep(5);
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
//                Ezcli.prompt = Ezcli.promptColor + Ezcli.currDir + " >> " + ColorOutput.DEFAULT_COLOR;
                Ezcli.prompt = Ezcli.currDir + " >> ";
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
//                Ezcli.prompt = Ezcli.promptColor + Ezcli.currDir + " >> " + ColorOutput.DEFAULT_COLOR;
                Ezcli.prompt = Ezcli.currDir + " >> ";
            } else {
                System.out.println("Please enter a valid directory to change to.");
            }
        }
    }
}
