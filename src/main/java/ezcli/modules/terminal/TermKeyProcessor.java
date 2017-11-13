package ezcli.modules.terminal;

import ezcli.modules.ezcli_core.Ezcli;
import ezcli.modules.ezcli_core.global_io.KeyHandler;
import ezcli.modules.ezcli_core.global_io.Keys;
import ezcli.modules.ezcli_core.util.FileAutocomplete;
import ezcli.modules.ezcli_core.util.Util;

import java.util.ArrayList;

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
     *
     * @param input Last character input by user
     */
    @Override
    public void process(int input) {

        Keys key = getKey(input);

        if (key != Keys.TAB) {
            inputProcessor.setLockTab(false);
            inputProcessor.setBlockClear(false);
            FileAutocomplete.resetVars();
        }

        super.process(input);
    }

    @Override
    public void tabEvent() {
        inputProcessor.fileAutocomplete();
        inputProcessor.setResetVars(false);
    }

    @Override
    public void newLineEvent() {
        boolean empty = Terminal.containsOnlySpaces(inputProcessor.getCommand());

        String command = inputProcessor.getCommand();
        ArrayList<String> prevCommands = inputProcessor.getPrevCommands();

        if (!empty)
            prevCommands.add(command);

        inputProcessor.getArrowKeyProcessor().setCommandListPosition(prevCommands.size());
        inputProcessor.getArrowKeyProcessor().setCurrCommand("");
        inputProcessor.setCursorPos(0);
        inputProcessor.setResetVars(true);
        inputProcessor.parse();
    }

    @Override
    public void charEvent(char input) {
        String command = inputProcessor.getCommand();
        int cursorPos = inputProcessor.getCursorPos();

        if (inputProcessor.getCursorPos() == inputProcessor.getCommand().length()) {
            System.out.print(input);
            inputProcessor.setCommand(inputProcessor.getCommand() + input);
        } else {
            Util.clearLine(inputProcessor.getCommand(), true);
            inputProcessor.setCommand(new StringBuilder(command).insert(cursorPos, input).toString());
            System.out.print(Ezcli.prompt + inputProcessor.getCommand());
        }

        inputProcessor.increaseCursorPos();
        inputProcessor.moveToCursorPos();
        inputProcessor.setResetVars(true);
    }

    @Override
    public void backspaceEvent() {
        if (inputProcessor.getCommand().length() > 0 && inputProcessor.getCursorPos() > 0) {
            int charToDelete = inputProcessor.getCursorPos() - 1;
            String command = inputProcessor.getCommand();

            Util.clearLine(inputProcessor.getCommand(), true);

            inputProcessor.setCommand(new StringBuilder(command).deleteCharAt(charToDelete).toString());
            System.out.print(Ezcli.prompt + inputProcessor.getCommand());

            inputProcessor.decreaseCursorPos();
            inputProcessor.moveToCursorPos();
            inputProcessor.setResetVars(true);
        }
    }
}
