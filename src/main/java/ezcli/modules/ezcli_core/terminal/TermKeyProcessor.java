package ezcli.modules.ezcli_core.terminal;

import ezcli.modules.ezcli_core.Ezcli;
import ezcli.modules.ezcli_core.global_io.KeyHandler;
import ezcli.modules.ezcli_core.global_io.Keys;
import ezcli.modules.ezcli_core.util.FileAutocomplete;
import ezcli.modules.ezcli_core.util.Util;

/**
 * Processes key presses (except arrow keys) for Terminal module.
 *
 * @see Terminal
 */
public class TermKeyProcessor extends KeyHandler {

    private TermInputProcessor inputProcessor;

    TermKeyProcessor(TermInputProcessor inputProcessor) {
        this.inputProcessor = inputProcessor;
    }

    /**
     * Processes input provided by input class,
     * and operates based on the input it receives,
     * using the character value passed by the process() method.
     * <br></br>
     *
     * @param input last character input by user
     */
    @Override
    public void process(int input) {

        Keys key = getKey(input);

        // reset if input is not tab
        if (key != Keys.TAB) {
            inputProcessor.setLockTab(false);
            inputProcessor.setBlockClear(false);
            FileAutocomplete.resetVars();
        }

        super.process(input);
    }

    @Override
    public void tabEvent() {

        //TODO: ADJUST CURSORPOS TO POSITION AFTER TAB

        // Split into sections
        String[] commandArr = inputProcessor.getCommand().split(" ");

        // Get last element
        String currText = commandArr[commandArr.length - 1] + (inputProcessor.getCommand().endsWith(" ") ? " " : "");

        // If more than one element, autocomplete file
        if (commandArr.length > 1 || inputProcessor.getCommand().endsWith(" "))
            inputProcessor.fileAutocomplete(currText);
    }

    @Override
    public void newLineEvent() {
        boolean empty = Terminal.containsOnlySpaces(inputProcessor.getCommand());

        if (!empty)
            inputProcessor.getPrevCommands().add(inputProcessor.getCommand());
        inputProcessor.getArrowKeyProcessor().setCommandListPosition(inputProcessor.getPrevCommands().size());
        inputProcessor.getArrowKeyProcessor().setCurrCommand("");
        inputProcessor.setCursorPos(0);
        inputProcessor.parse();
    }

    @Override
    public void charEvent(char input) {
        if (inputProcessor.getCursorPos() == inputProcessor.getCommand().length()) {
            System.out.print(input);
            inputProcessor.setCommand(inputProcessor.getCommand() + input);
        } else {
            Util.clearLine(inputProcessor.getCommand(), true);
            inputProcessor.setCommand(new StringBuilder(inputProcessor.getCommand()).insert(inputProcessor.getCursorPos(), input).toString());
            System.out.print(Ezcli.prompt + inputProcessor.getCommand());
        }
        inputProcessor.increaseCursorPos();
        inputProcessor.moveToCursorPos();
    }

    @Override
    public void backspaceEvent() {
        if (inputProcessor.getCommand().length() > 0 && inputProcessor.getCursorPos() > 0) {
            int charToDelete = inputProcessor.getCursorPos();
            Util.clearLine(inputProcessor.getCommand(), true);
            inputProcessor.setCommand(
                    inputProcessor.getCommand().substring(0, charToDelete - 1) +
                            (charToDelete < inputProcessor.getCommand().length() ? inputProcessor.getCommand().substring(charToDelete) : ""));

            // Delete char, add white space and move back again
            System.out.print(Ezcli.prompt + inputProcessor.getCommand());
            inputProcessor.decreaseCursorPos();
            inputProcessor.moveToCursorPos();
        }
    }
}
