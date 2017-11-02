package ezcli.modules.ezcli_core.util;

import ezcli.modules.ezcli_core.Ezcli;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;

public class FileAutocomplete {

    private static File[] files;
    private static LinkedList<String> fileNames;
    private static String command, originalCommand, currText, path;
    private static boolean blockClear, lockTab, endsWithSlash, newList;
    private static int startComplete;

    private static boolean available;

    public static boolean isAvailable() {
        return available;
    }

    public static String getCommand() {
        return command;
    }

    public static boolean isBlockClear() {
        return blockClear;
    }

    public static boolean isLockTab() {
        return lockTab;
    }

    /**
     * Sets all variables needed for attempting to autocomplete a file or directory name, and then runs the algorithm.
     *
     * @param command Command with complete path to autocomplete (not necessarily the same as currText)
     * @param currText File or directory name to autocomplete (determined by splitting command by "/")
     * @param blockClear True if method should not delete the file names currently stored, which enables tab rotation
     * @param lockTab Whether the tab key should be processed or not
     */
    public static void init(String command, String currText, boolean blockClear, boolean lockTab) {
        // set to unavailable until this operation is complete
        available = false;

        // set variables needed to do autocomplete
        FileAutocomplete.command = command;
        FileAutocomplete.currText = currText;
        FileAutocomplete.blockClear = blockClear;
        FileAutocomplete.lockTab = lockTab;

        // autocomplete
        fileAutocomplete();

        // set to available
        available = true;
    }

    public static void resetVars() {
        files = null;
        fileNames = null;
        command = "";
        originalCommand = "";
        currText = "";
        path = "";
        blockClear = false;
        lockTab = false;
        endsWithSlash = false;
        newList = false;
        startComplete = 0;
    }

    /**
     * Using a string of text representing what has been typed presently,
     * displays all files that match the current input.
     *
     */
    public static void fileAutocomplete() {

        // Whether a new list of files should be created or not
        newList = false;

        // Whether command ends with slash or not
        endsWithSlash = originalCommand.endsWith("/") ? originalCommand.endsWith("/") : command.endsWith("/");

        startComplete = 0; // where to start autocompleting in string command

        path = getPath(); // path to directory in which to find files to complete with

        // Get directory at path, and files inside the directory
        File currFolder = new File(Ezcli.currDir + path);
        files = currFolder.listFiles();

        // If not empty parameter or not directory
        if (!endsWithSlash && !command.endsWith(" "))
            currText = currText.split("/")[currText.split("/").length - 1];

        // If ends with slash directory and list not yet cleared from previous tab, clear, block clear so tab rotation works and set
        // modText to empty string, so that all files in the directory are output
        else if (endsWithSlash && !blockClear) {
            fileNames.clear();
            blockClear = true;
            currText = " ";
        }

        // If command ends with empty space, output all files in path
        else if (command.endsWith(" "))
            printAllContents();

        if (fileNames.size() == 0) {
            newList = true; // For tab rotation, true means no tab rotation, false means rotate through list
            populateFileNames();
        }

        if (fileNames.size() != 1) {

            /* Method that first prints all matching files and directories, and then iterates through them
             * with each tab key press
             */
            fileNamesIterator();

            // If no input, just output all files and folders
            if (" ".equals(currText))
                printAllContents();

        } else if (!lockTab) {
            autocomplete();
        }
    }

    /**
     * Populates the fileNames list with all files and folders matching currText.
     */
    private static void populateFileNames() {

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
     * @return Path found
     */
    private static String getPath() {
        endsWithSlash = originalCommand.endsWith("/") ? originalCommand.endsWith("/") : command.endsWith("/");

        // Split text at slashes to get path, so that relevant files can be autocompleted or displayed
        String[] splitPath = currText.split("/");
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
     */
    private static void autocomplete() {
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
     */
    private static void printAllContents() {
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
     */
    private static void fileNamesIterator() {
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
