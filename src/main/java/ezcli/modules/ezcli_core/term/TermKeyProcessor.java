package ezcli.modules.ezcli_core.term;

import ezcli.modules.ezcli_core.global_io.KeyHandler;
import ezcli.modules.ezcli_core.global_io.Keys;

/**
 * Processes key presses (except arrow keys) for Terminal module.
 *
 * @see Terminal
 */
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
        if (TermInputProcessor.getFileNames().size() > 0 && clearFilesList) {
            TermInputProcessor.getFileNames().clear();
            TermInputProcessor.setOriginalCommand("");
        }
    }

    @Override
    public void tabEvent() {
        clearFilesList = false;

        // Split into sections
        String[] commandArr = TermInputProcessor.getCommand().split(" ");

        // Get last element
        String currText = commandArr[commandArr.length - 1] + (TermInputProcessor.getCommand().endsWith(" ") ? " " : "");

        // If more than one element, autocomplete file
        if (commandArr.length > 1 || TermInputProcessor.getCommand().endsWith(" "))
            TermInputProcessor.fileAutocomplete(currText);
    }

    @Override
    public void newLineEvent() {
        boolean empty = Terminal.containsOnlySpaces(TermInputProcessor.getCommand());
        //if (TermInputProcessor.command.length() > 0 && !empty)
            Terminal.parse = true;

        if (!empty)
            TermInputProcessor.getPrevCommands().add(TermInputProcessor.getCommand());
        TermInputProcessor.commandListPosition = TermInputProcessor.getPrevCommands().size();
        TermInputProcessor.currCommand = "";
        System.out.println(); // new line
    }

    @Override
    public void charEvent(char input) {
        System.out.print(input);
        TermInputProcessor.setCommand(TermInputProcessor.getCommand() + input);
    }

    @Override
    public void backspaceEvent() {
        if (TermInputProcessor.getCommand().length() > 0) {
            TermInputProcessor.setCommand(TermInputProcessor.getCommand().substring(0, TermInputProcessor.getCommand().length() - 1));

            // Delete char, add white space and move back again
            System.out.print("\b \b");
        }
    }
}
