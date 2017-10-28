package ezcli.modules.ezcli_core.term;

import ezcli.Ezcli;
import ezcli.modules.ezcli_core.global_io.KeyHandler;
import ezcli.modules.ezcli_core.global_io.Keys;

public class TermKeyProcessor extends KeyHandler {

    private boolean clearFilesList = true;

    /**
     * Processes input provided by Input class,
     * and operates based on the input it receives,
     * using the character value passed by the process() method.
     * <br></br>
     *
     * @param input last character input by user
     */
    @Override
    public void process(char input) {

        Keys key = getKey(input);

        clearFilesList = true;

        // reset if input is not tab
        if (key != Keys.TAB) {
            TermInputProcessor.lockTab = false;
            TermInputProcessor.blockClear = false;
        }

        super.process(input);

        // clear TermInputProcessor.fileNames list and reset TermInputProcessor.originalCommand string
        if (TermInputProcessor.fileNames.size() > 0 && clearFilesList) {
            TermInputProcessor.fileNames.clear();
            TermInputProcessor.originalCommand = "";
        }
    }

    /**
     * Determines if a string is composed only of spaces.
     * <br></br>
     *
     * @param s string to check
     * @return true if s is composed of only spaces, false if there is a character in it
     */
    private static boolean containsOnlySpaces(String s) {
        for (int i = 0; i < s.length(); i++)
            if (s.charAt(i) != ' ')
                return false;
        return true;
    }

    @Override
    public void tabEvent() {
        clearFilesList = false;

        // Split into sections
        String[] commandArr = TermInputProcessor.command.split(" ");

        // Get last element
        String currText = commandArr[commandArr.length - 1] + (TermInputProcessor.command.endsWith(" ") ? " " : "");

        // If more than one element, autocomplete file
        if (commandArr.length > 1 || TermInputProcessor.command.endsWith(" "))
            TermInputProcessor.fileAutocomplete(currText);
    }

    @Override
    public void newLineEvent() {
        boolean empty = containsOnlySpaces(TermInputProcessor.command);
        if (TermInputProcessor.command.length() > 0 && !empty)
            Terminal.parse = true;

        if (!empty)
            TermInputProcessor.prevCommands.add(TermInputProcessor.command);
        TermInputProcessor.commandListPosition = TermInputProcessor.prevCommands.size();
        TermInputProcessor.currCommand = "";
        System.out.print("\n" + Ezcli.prompt);
    }

    @Override
    public void charEvent(char c, Keys key) {
        if (key == Keys.BCKSP || key == Keys.TAB)
            return;

        System.out.print(c);
        TermInputProcessor.command += c;
    }

    @Override
    public void backspaceEvent() {
        if (TermInputProcessor.command.length() > 0) {
            TermInputProcessor.command = TermInputProcessor.command.substring(0, TermInputProcessor.command.length() - 1);

            // Delete char, add white space and move back again
            System.out.print("\b \b");
        }
    }
}
