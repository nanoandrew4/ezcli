package ezcli.modules.ezcli_core.terminal;

import ezcli.modules.ezcli_core.Ezcli;
import ezcli.modules.ezcli_core.global_io.Keys;
import ezcli.modules.ezcli_core.global_io.handlers.KeyHandler;
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
        setUpTabEvents();
        setUpNWLNEvent();
        setUpCharEvents();
        setUpBackspaceEvent();
    }

    private void setUpTabEvents() {
        tabEvent = () -> {
            Util.clearLine(inputProcessor.getCommand(), true);
//            inputProcessor.fileAutocomplete();
            inputProcessor.setResetVars(false);
        };
    }

    private void setUpNWLNEvent() {
        newLineEvent = () -> {
            String command = inputProcessor.getCommand();

            boolean empty = "".equals(command.trim());

            if (!empty)
                inputProcessor.commandHistory.add(command);

            inputProcessor.getArrowKeyProcessor().setCommandListPosition(inputProcessor.commandHistory.size());
            inputProcessor.getArrowKeyProcessor().setCurrCommand("");
            inputProcessor.setCursorPos(0);
            inputProcessor.setResetVars(true);
            inputProcessor.parse();

            inputProcessor.setCommand("");
            Ezcli.ezcliOutput.print(Ezcli.prompt, "prompt");
        };
    }

    private void setUpCharEvents() {
        charEvent = (char input) -> {
            String command = inputProcessor.getCommand();
            int cursorPos = inputProcessor.getCursorPos();

            Util.clearLine(command, true);

            if (inputProcessor.getCursorPos() == command.length()) {
                Ezcli.ezcliOutput.print(Ezcli.prompt, "prompt");
                Ezcli.ezcliOutput.print(command + input, "command");
                inputProcessor.setCommand(command + input);
            } else {
                inputProcessor.setCommand(new StringBuilder(command).insert(cursorPos, input).toString());
                Ezcli.ezcliOutput.print(Ezcli.prompt, "prompt");
                Ezcli.ezcliOutput.print(inputProcessor.getCommand(), "command");
            }

            inputProcessor.increaseCursorPos();
            inputProcessor.moveToCursorPos();
            inputProcessor.setResetVars(true);
        };
    }

    private void setUpBackspaceEvent() {
        backspaceEvent = () -> {
            if (inputProcessor.getCommand().length() > 0 && inputProcessor.getCursorPos() > 0) {
                int charToDelete = inputProcessor.getCursorPos() - 1;
                String command = inputProcessor.getCommand();

                Util.clearLine(command, true);

                inputProcessor.setCommand(new StringBuilder(command).deleteCharAt(charToDelete).toString());
                Ezcli.ezcliOutput.print(Ezcli.prompt, "prompt");
                Ezcli.ezcliOutput.print(inputProcessor.getCommand(), "command");

                inputProcessor.decreaseCursorPos();
                inputProcessor.moveToCursorPos();
                inputProcessor.setResetVars(true);
            }
        };
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
            //FileAutocomplete.resetVars();
        }

        super.process(input);
    }
}
