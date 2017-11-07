package ezcli.modules.ezcli_core.util;

import ezcli.modules.ezcli_core.Ezcli;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

/**
 * Class that autocompletes filenames.
 */
public class FileAutocomplete {

    private static File[] files;
    private static LinkedList<String> fileNames = new LinkedList<>();
    private static String command = "", originalCommand = "", currText = "", path = "";
    private static boolean blockClear, lockTab, resetVars, endsWithSlash, newList;
    private static int startComplete;

    private static boolean available = true;

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

    public static boolean isResetVars() {
        return resetVars;
    }

    /**
     * Sets all variables needed for attempting to autocomplete a file or directory name, and then runs the algorithm.
     *
     * @param command Command with complete path to autocomplete (not necessarily the same as currText)
     * @param blockClear True if method should not delete the file names currently stored, which enables tab rotation
     * @param lockTab Whether the tab key should be processed or not
     */
    public static void init(String command, boolean blockClear, boolean lockTab) {
        // Set to unavailable until this operation is complete
        available = false;
        resetVars = false;

        // Set variables needed to do autocomplete
        FileAutocomplete.command = command;
        FileAutocomplete.blockClear = blockClear;
        FileAutocomplete.lockTab = lockTab;

        if ("".equals(command))
            FileAutocomplete.command = " ";

        // Stores original command so that command does not keep adding to itself
        originalCommand = command;

        // Autocomplete
        fileAutocomplete();

        // Set to available
        available = true;
    }

    public static void setCommand(String command) {
        FileAutocomplete.command = command;

        String[] commandArr = command.split(" "); // Split command
        FileAutocomplete.currText = command.endsWith(" ") ? "" : commandArr[commandArr.length - 1]; // Get portion of command to autocomplete
    }

    protected static void setCurrText(String currText) {
        FileAutocomplete.currText = currText;
    }

    public static void resetVars() {
        files = null;
        fileNames = new LinkedList<>();
        command = "";
        originalCommand = "";
        currText = "";
        path = "";
        blockClear = false;
        lockTab = false;
        resetVars = false;
        endsWithSlash = false;
        newList = false;
        startComplete = 0;
    }

    /**
     * Using a string of text representing what has been typed presently,
     * displays all files that match the current input.
     */
    public static void fileAutocomplete() {

        // Split command
        String[] commandArr = originalCommand.split(" ");
        // Get portion of command to autocomplete
        FileAutocomplete.currText = originalCommand.endsWith(" ") ? " " : commandArr[commandArr.length - 1];

        // Whether a new list of files should be created or not
        newList = false;

        // Whether command ends with slash or not
        endsWithSlash = originalCommand.endsWith("/") ? originalCommand.endsWith("/") : command.endsWith("/");

        startComplete = 0; // Where to start autocompleting in string command

        path = getPath(); // Path to directory in which to find files to complete with

        // Get directory at path, and files inside the directory
        Path p = path.startsWith("/") ? Paths.get(path) : Paths.get(Ezcli.currDir + path);
        File currFolder = p.toFile();
        files = currFolder.listFiles();

        // Do not continue if the path passed was invalid, and nothing exists there
        if (files == null)
            return;

        // If not empty parameter or not directory
        if ((!endsWithSlash && !currText.startsWith("~")) && !command.endsWith(" "))
            currText = currText.split("/")[currText.split("/").length - 1];

        /*
         * From this point onwards currText becomes the text to complete, without any path.
         * Example: currText before would be "/home/username/someDir/te", now it is "te".
         */

        // If ends with slash directory and list not yet cleared from previous tab, clear, block clear so tab rotation works and set
        // modText to empty string, so that all files in the directory are output
        else if ((endsWithSlash || currText.startsWith("~")) && !blockClear) {
            fileNames.clear();
            blockClear = true;
            currText = " ";
        }

        if (fileNames.size() == 0) {
            newList = true; // For tab rotation, true means no tab rotation, false means rotate through list
            populateFileNames();
        }

        if (fileNames.size() != 1) {

            /*
             * Method that first prints all matching files and directories, and then iterates through them
             * with each tab key press
             */

            fileNamesIterator();

        } else if (!lockTab) {
            autocomplete();
        }
    }

    /**
     * Populates the fileNames list with all files and folders matching currText.
     */
    private static void populateFileNames() {

        // For autocomplete in tab rotation
        startComplete = (endsWithSlash || currText.endsWith(" ")) ? 0 : currText.endsWith(" ") ? 0 : currText.length();

        // Add all files with matching names to list
        assert files != null;
        for (File f : files)
            if (f.getName().startsWith(currText) || " ".equals(currText))
                fileNames.add(f.getName());
    }

    /**
     * Returns the path to the specified directory given a string.
     *
     * @return Path found
     */
    protected static String getPath() {
        boolean startsWithSlash = originalCommand.startsWith("/") || currText.startsWith("/");
        endsWithSlash = originalCommand.endsWith("/") || currText.endsWith("/");

        String path = currText.startsWith("~") ? Ezcli.userHomeDir : "";

        if (!"".equals(path)) {
            currText = currText.substring(1);
            if (currText.startsWith("/"))
                currText = currText.substring(1);
        }

        // Split text at slashes to get path, so that relevant files can be autocompleted or displayed
        String[] splitPath = currText.split("/");

        if (splitPath.length > 0) {
            // Re-create path to look in, starting at 0 if text does not start with "/", and at 1 if it does (prevents duplicate "/")
            for (int i = startsWithSlash ? 1 : 0; (i < (splitPath.length - 1) && !endsWithSlash) || (i < splitPath.length && endsWithSlash); i++)
                path += (i > 0 ? "/" : "") + splitPath[i]; // Don't make "username/" in to "/username/"

            if (!"".equals(path) && !"~".equals(currText)) // If text passed is just "" do not add a "/"
                path += "/";
        }

        if (startsWithSlash && "".equals(path)) // If path is just "/" return just that (which is top level dir)
            path = "/";

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

        Path p = path.startsWith("/") ? Paths.get(path + fileName) : Paths.get(Ezcli.currDir + path + fileName);

        // If auto-completing directory, add slash at end
        if (Files.isDirectory(p))
            end = "/";

        // If auto-completing a file, add space at end
        else if (Files.isRegularFile(p))
            end = " ";

        // Append autocompleted text to command variable and print autocompleted string
        command += fileName.substring(startComplete, fileName.length()) + end;
        System.out.print(fileName.substring(startComplete, fileName.length()) + end);

        // Lock tab
        lockTab = true;

        // Notify caller class that variables can be reset now, since autocomplete is done
        resetVars = true;
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

        // Rotate
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
