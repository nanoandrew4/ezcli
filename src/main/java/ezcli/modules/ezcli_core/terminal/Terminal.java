package ezcli.modules.ezcli_core.terminal;

import ezcli.modules.ezcli_core.Ezcli;
import ezcli.modules.ezcli_core.global_io.Command;
import ezcli.modules.ezcli_core.global_io.handlers.InputHandler;
import ezcli.modules.ezcli_core.modularity.Module;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

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

    private void importBashHistory() {
        System.out.println("You currently have an empty history file. Do you wish to import you bash history?");
        Scanner in = new Scanner(System.in);
        String ans = in.nextLine();
        if ("y".equalsIgnoreCase(ans) || "yes".equalsIgnoreCase(ans)) {
            try {
                List<String> bashHistory = Files.readAllLines(Paths.get(Ezcli.USER_HOME_DIR + ".bash_history"));
                inputInSession.addAll(bashHistory);
                writeCommandsToFile();
            } catch (IOException e) {
                System.err.println("Error reading bash history file. Aborting import...");
            }
        }
    }

    private void writeCommandsToFile() {
        File historyFile = new File(Ezcli.USER_HOME_DIR + ".ezcli_history");

        try {
            if (historyFile.createNewFile())
                importBashHistory();
        } catch (IOException e) {
            System.err.println("Error creating new history file in directory: " + Ezcli.USER_HOME_DIR);
            return;
        }

        List<String> original;
        try {
            original = Files.readAllLines(Paths.get(historyFile.getAbsolutePath()));
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
                System.err.println("Error creating print writer");
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
        this.currentlyActive = true;
        Ezcli.prompt = Ezcli.currDir + " >> ";

        Ezcli.ezcliOutput.println("Entered terminal mode", "info");
        Ezcli.ezcliOutput.print(Ezcli.prompt, "prompt");
        while (!exit) {
            inputProcessor.process(InputHandler.getKey());
        }

        writeCommandsToFile();
        this.currentlyActive = false;
        Ezcli.prompt = ">> ";
    }

    public void parse(String rawCommand) {

        inputInSession.add(rawCommand);

        String[] split = rawCommand.split("&&");
        Ezcli.ezcliOutput.println();

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
                Ezcli.ezcliOutput.print(Ezcli.prompt, "prompt");
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
                System.err.println("Parsing command \"" + command + "\" failed, enter \"help\" for help using module.");
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
        Ezcli.ezcliOutput.print(Ezcli.prompt, "prompt");
    }

    public void help() {
        Ezcli.ezcliOutput.println("This module interacts directly with the system.", "info");
        Ezcli.ezcliOutput.println("All input will be passed to the system when a \n" +
                "newline character is detected (enter key pressed)", "info");
        Ezcli.ezcliOutput.println("To change directory, enter: \"cd [somedir]\".", "info");
        Ezcli.ezcliOutput.println("To return to Interactive module, enter \"exit\".", "info");
    }

    @Override
    public void tour() {
        Ezcli.ezcliOutput.println("This is the " + moduleName + " module.", "info");
        Ezcli.ezcliOutput.println("This module deals directly with your system terminal.", "info");
        Ezcli.ezcliOutput.println("Anything input here, with the exception of the commands", "info");
        Ezcli.ezcliOutput.println("\"exit\" and \"t-help\" will be passed directly to the system.", "info");
        if (Ezcli.sleep(3) != Command.NONE) return;

        Ezcli.ezcliOutput.println("For example, lets pass \"dir\"", "info");
        if (Ezcli.sleep(1.2) != Command.NONE) return;
        inputProcessor.process('d');
        if (Ezcli.sleep(0.7) != Command.NONE) return;
        inputProcessor.process('i');
        if (Ezcli.sleep(0.7) != Command.NONE) return;
        inputProcessor.process('r');
        if (Ezcli.sleep(1.2) != Command.NONE) return;
        inputProcessor.getKeyProcessor().newLineEvent();

        Ezcli.ezcliOutput.println("\n\n", "info");

        Ezcli.ezcliOutput.println("That should have printed your working directory!", "info");
        Ezcli.ezcliOutput.println("We will now be exiting this module, please wait.\n\n", "info");
        Ezcli.sleep(5);
    }

    /**
     * Changes the terminals directory, since the system does not interpret chdir commands.
     * Attempts to emulate the "cd" command.
     *
     * @param command chdir command to parse
     */
    public void changeDir(String command) {
        String[] chdirSplit = command.split(" ");

        if (chdirSplit.length != 2 || !"cd".equals(chdirSplit[0])) {
            Ezcli.ezcliOutput.println("Invalid chdir command passed.", "info");
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
//                Ezcli.prompt = Ezcli.promptColor + Ezcli.currDir + " >> " + ANSIColorOutput.DEFAULT_COLOR;
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
                Ezcli.prompt = Ezcli.currDir + " >> ";
            } else {
                Ezcli.ezcliOutput.println("Please enter a valid directory to change to.", "info");
            }
        }
    }
}