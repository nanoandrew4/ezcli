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
    protected static boolean lockTab = false;

    // Stops autocomplete from constantly erasing fileNames list when searching sub-directories
    protected static boolean blockClear = false;

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

        // Process input for Windows systems
        if (Ezcli.isWin) {
            // If returns anything but ArrowKeys.NONE or ArrowKeys.MOD check keys
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
    protected static void fileAutocomplete(String currText) {
        
        String modText = currText; // Modifiable version of currText

        // Whether a new list of files should be created or not
        boolean newList = false;

        // Whether command ends with slash or not
        boolean endsWithSlash = originalCommand.endsWith("/") ? originalCommand.endsWith("/") : command.endsWith("/");

        String path = getPath(currText); // path to directory in which to find files to complete with

        // Get directory at path, and files inside the directory
        File currFolder = new File(Ezcli.currDir + path);
        File[] files = currFolder.listFiles();

        // If not empty parameter or not directory
        if (!endsWithSlash && !command.endsWith(" "))
            modText = currText.split("/")[currText.split("/").length - 1];

        // If ends with slash directory and list not yet cleared from previous tab, clear, block clear so tab rotation works and set
        // modText to empty string, so that all files in the directory are output
        else if (endsWithSlash && !blockClear) {
            fileNames.clear();
            blockClear = true;
            modText = " ";
        }

        // If command ends with empty space, output all files in path
        else if (command.endsWith(" "))
            printAllContents(fileNames.size() == 0, files);

        if (fileNames.size() == 0) {
            newList = true; // For tab rotation, true means no tab rotation, false means rotate through list
            populateFileNames(files, endsWithSlash, modText);
        }

        if (fileNames.size() != 1) {

            /* Method that first prints all matching files and directories, and then iterates through them
             * with each tab key press
             */
            fileNamesIterator(newList, endsWithSlash, modText);

            // If no input, just output all files and folders
            if (" ".equals(modText))
                printAllContents(newList, files);

        } else if (!lockTab) {
            autocomplete(path, modText);
        }
    }

    /**
     * Populates the fileNames list with all files and folders matching currText.
     *
     * @param files List of all files in path
     * @param endsWithSlash Whether the current input ends with a slash or not (file vs directory)
     * @param currText String to match with file names to populate list
     */
    private static void populateFileNames(File[] files, boolean endsWithSlash, String currText) {

        // Stores original command so that command does not keep adding to itself
        originalCommand = command;

        // For autocomplete in tab rotation
        startComplete = (endsWithSlash || currText.endsWith(" ")) ? 0 : currText.length();

        // Add all files with matching names to list
        assert files != null;
        for (File f : files)
            if (f.getName().startsWith(currText))
                fileNames.add(f.getName());
    }

    /**
     * Returns the path to the specified directory given a string.
     *
     * @param str String to try to extract path from
     * @return Path found
     */
    private static String getPath(String str) {
        boolean endsWithSlash = originalCommand.endsWith("/") ? originalCommand.endsWith("/") : command.endsWith("/");

        // Split text at slashes to get path, so that relevant files can be autocompleted or displayed
        String[] splitPath = str.split("/");
        String path = "";
        if (splitPath.length > 0) {
            // Re-create path to look in
            for (int i = 0; (i < (splitPath.length - 1) && !endsWithSlash) || (i < splitPath.length && endsWithSlash); i++)
                path += "/" + splitPath[i];
            path += "/";
        }

        return path;
    }

    /**
     * Autocompletes a string currText using files and folders contained in path.
     * Not for rotating through a list of possibilities, this method should only run
     * when there is only one option to autocomplete.
     *
     * @param path Path in which to perform the autocompleting.
     * @param currText Text to be autocompleted
     */
    private static void autocomplete(String path, String currText) {
        String fileName = fileNames.getFirst();
        String end = "";

        // If auto-completing directory, add slash at end
        if (Files.isDirectory(Paths.get(Ezcli.currDir + path + fileName)))
            end = "/";

            // If auto-completing a file, add space at end
        else if (Files.isRegularFile(Paths.get(Ezcli.currDir + path + fileName)))
            end = " ";

        // Append autocompleted text to command variable and print autocompleted string
        command += fileName.substring(currText.length(), fileName.length()) + end;
        System.out.print(fileName.substring(currText.length(), fileName.length()) + end);

        // Lock tab
        lockTab = true;
    }

    /**
     * Prints all contents of a directory, should only happen in Terminal module
     * when no input to autocomplete has been given.
     * @param newList Whether the list of files is new or old
     * @param files List of files in path
     */
    private static void printAllContents(boolean newList, File[] files) {
        if (newList) {
            for (File f : files) {
                System.out.print(f.getName() + " \t");
                fileNames.add(f.getName());
            }
            // Improve readability
            System.out.println("\n");

            // Re-output command after clearing lines and printing all contents
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

    /**
     * First will display all files and directories matching 'currText' when tab key is pressed.
     * <br></br><br></br>
     * After this has happened, provided no other keys have been pressed (excluding tab),
     * it will iterate through all the file and directory names that match 'currText', printing the
     * missing characters to the terminal so the user can see, and appending those autocompleted
     * characters to the end of the command variable.
     *
     * @param newList whether the list of files to iterate through is new or not
     * @param endsWithSlash endsWithSlash from fileAutocomplete()
     * @param currText text to be autocompleted
     */
    private static void fileNamesIterator(boolean newList, boolean endsWithSlash, String currText) {
        // Clear line
        if (fileNames.size() > 0 || " ".equals(currText))
            Util.clearLine(command, true);

        // Print matching file names
        if (newList)
            for (String s : fileNames)
                System.out.print(s + "\t");

            // rotate
        else if (!lockTab || endsWithSlash) {
            Util.clearLine(command, true); // Clear current line in terminal

            String currFile = fileNames.pollFirst(); // Get and remove first file or directory in list

            // Autocomplete with first file or dir name
            command = originalCommand + currFile.substring(startComplete, currFile.length());

            System.out.print(Ezcli.prompt + command); // Print autocompleted command to screen

            fileNames.add(currFile); // Add previously removed file or directory to end of list
        }

        if (fileNames.size() > 0 && newList)
            // Re-output command after clearing lines
            System.out.print("\n" + Ezcli.prompt + command);
    }
}

