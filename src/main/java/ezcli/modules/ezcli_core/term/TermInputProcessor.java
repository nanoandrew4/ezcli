package ezcli.modules.ezcli_core.term;

import ezcli.modules.ezcli_core.Ezcli;
import ezcli.modules.ezcli_core.global_io.*;
import ezcli.modules.ezcli_core.util.Util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Input processor for terminal module.
 *
 * @see Terminal
 * @see TermKeyProcessor
 * @see TermArrowKeyProcessor
 */
public class TermInputProcessor extends InputHandler {

    // List of files for tab rotation and printing options
    private static LinkedList<String> fileNames = new LinkedList<>();

    private static ArrayList<String> prevCommands = new ArrayList<>(); // stores all entered commands

    private static String command = "";

    // Stores command while rotating through fileNames list
    private static String originalCommand = "";

    // Length of original input to be completed
    private static int startComplete = 0;

    // Stops autocomplete from reprinting the text it completed if tab is pressed consecutively at the end of a complete file name
    static boolean lockTab = false;

    // Stops autocomplete from constantly erasing fileNames list when searching sub-directories
    static boolean blockClear = false;

    // for use in detecting arrow presses on Unix, see comment block near usage in Process()
    private static long lastPress = System.currentTimeMillis();

    TermInputProcessor() {
        super(new TermKeyProcessor(), new TermArrowKeyProcessor());
        KeyHandler.init();
    }

    protected static LinkedList<String> getFileNames() {
        return fileNames;
    }

    protected static void setFileNames(LinkedList<String> fileNames) {
        TermInputProcessor.fileNames = fileNames;
    }

    protected static ArrayList<String> getPrevCommands() {
        return prevCommands;
    }

    protected static void setPrevCommands(ArrayList<String> prevCommands) {
        TermInputProcessor.prevCommands = prevCommands;
    }

    protected static String getCommand() {
        return command;
    }

    protected static void setCommand(String command) {
        TermInputProcessor.command = command;
    }

    protected static String getOriginalCommand() {
        return originalCommand;
    }

    protected static void setOriginalCommand(String originalCommand) {
        TermInputProcessor.originalCommand = originalCommand;
    }

    protected static int getStartComplete() {
        return startComplete;
    }

    protected static void setStartComplete(int startComplete) {
        TermInputProcessor.startComplete = startComplete;
    }

    public KeyHandler getKeyHandler() {
        return keyHandler;
    }

    public ArrowKeyHandler getArrowKeyHandler() {
        return arrowKeyHandler;
    }

    /**
     * Calls appropriate method for handling
     * input read from the Input class, using
     * booleans in Ezcli class to determine
     * what OS the program is running on.
     * <br></br>
     */
    @Override
    public void process(int input) {

        // process input for Windows systems
        if (Ezcli.isWin) {
            // if returns anything but ArrowKeys.NONE or ArrowKeys.MOD check keys
            ArrowKeys ak = arrowKeyHandler.process(ArrowKeyHandler.arrowKeyCheckWindows(input));
            if (ak != ArrowKeys.NONE && ak != ArrowKeys.MOD)
                keyHandler.process(input);
        } else if (Ezcli.isUnix) {
            ArrowKeys ak = ArrowKeyHandler.arrowKeyCheckUnix(input);
            if (ak != ArrowKeys.NONE && ak != ArrowKeys.MOD)
                arrowKeyHandler.process(ak);
            if (System.currentTimeMillis() - lastPress > 10 && input != 27)
                keyHandler.process(input);
        }

        lastPress = System.currentTimeMillis();
    }

    /**
     * Using a string of text representing what has been typed presently,
     * displays all files that match the current input.
     * <br></br>
     *
     * @param currText file that is to be completed
     */
    static void fileAutocomplete(String currText) {
        
        String modText = currText;

        boolean newList = false;
        // whether command ends with slash or not
        boolean endsWithSlash = originalCommand.endsWith("/") ? originalCommand.endsWith("/") : command.endsWith("/");

        // split text at slashes to get path, so that relevant files can be autocompleted or displayed
        String[] splitPath = modText.split("/");
        String path = "";
        if (splitPath.length > 0) {
            // re-create path to look in
            for (int i = 0; (i < (splitPath.length - 1) && !endsWithSlash) || (i < splitPath.length && endsWithSlash); i++)
                path += "/" + splitPath[i];
            path += "/";
        }

        // get folder at path
        File currFolder = new File(Ezcli.currDir + path);
        File[] files = currFolder.listFiles();

        // if not empty parameter or not directory
        if (!endsWithSlash && !command.endsWith(" "))
            modText = splitPath[splitPath.length - 1];

            // if ends with slash directory and list not yet cleared from previous tab, clear, block clear so tab rotation works and set
            // modText to empty string, so that all files in the directory are output
        else if (endsWithSlash && !blockClear) {
            fileNames.clear();
            blockClear = true;
            modText = " ";
        }

        // if command ends with empty space, output all files in path
        else if (command.endsWith(" "))
            modText = " ";

        // get all file names for comparison
        if (fileNames.size() == 0) {
            // For tab rotation, true means no tab rotation, false means rotate through list
            newList = true;

            // Stores original command so that command does not keep adding to itself
            originalCommand = command;

            // For autocomplete in tab rotation
            startComplete = (endsWithSlash || modText.endsWith(" ")) ? 0 : modText.length();

            // add all files with matching names to list
            assert files != null;
            for (File f : files)
                if (f.getName().startsWith(modText))
                    fileNames.add(f.getName());

        }

        if (fileNames.size() != 1) {
            // Clear line
            if (fileNames.size() > 0 || " ".equals(modText))
                Util.clearLine(command, true);

            // Print matching file names
            if (newList)
                for (String s : fileNames)
                    System.out.print(s + "\t");

            else if (!lockTab || endsWithSlash) {
                Util.clearLine(command, true);

                // Get first file or dir name
                String currFile = fileNames.pollFirst();

                // Autocomplete with first file or dir name
                command = originalCommand + currFile.substring(startComplete, currFile.length());

                // Print to screen
                System.out.print(Ezcli.prompt + command);

                // Add file or dir name at end of list
                fileNames.add(currFile);

            }

            if (fileNames.size() > 0 && newList) {
                System.out.println();

                // Re-output command after clearing lines
                System.out.print(Ezcli.prompt + command);

            }

            // If no input, just output all files and folders
            if (" ".equals(modText)) {
                if (newList) {
                    for (File f : files) {
                        System.out.print(f.getName() + " \t");
                        fileNames.add(f.getName());
                    }

                    // Improve readability
                    System.out.println("\n");

                    // Re-output command after clearing lines
                    System.out.print(Ezcli.prompt + command);

                } else if (!lockTab) {
                    Util.clearLine(command, true);

                    // Get first file or dir name
                    String currFile = fileNames.pollFirst();

                    // Autocomplete with first file or dir name
                    command = originalCommand + currFile.substring(startComplete, currFile.length());

                    // Print to screen
                    System.out.print(Ezcli.prompt + command);

                    // Add file or dir name at end of list
                    fileNames.add(currFile);
                }
            }

        } else if (!lockTab) {

            String fileName = fileNames.getFirst();
            String end = "";

            // if auto-completing directory, add slash at end
            if (Files.isDirectory(Paths.get(Ezcli.currDir + path + fileName)))
                end = "/";

                // if auto-completing a file, add space at end
            else if (Files.isRegularFile(Paths.get(Ezcli.currDir + path + fileName)))
                end = " ";

            command += fileName.substring(modText.length(), fileName.length()) + end;
            System.out.print(fileName.substring(modText.length(), fileName.length()) + end);

            // Lock tab
            lockTab = true;
        }
    }
}

