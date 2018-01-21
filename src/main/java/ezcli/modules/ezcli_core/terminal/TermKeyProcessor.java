package ezcli.modules.ezcli_core.terminal;

import ezcli.modules.ezcli_core.Ezcli;
import ezcli.modules.ezcli_core.global_io.handlers.InputHandler;
import ezcli.modules.ezcli_core.global_io.handlers.KeyHandler;

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
        tabEvent = () -> {};
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
            inputProcessor.parse();

            inputProcessor.setCommand("");
        };
    }

    private void setUpCharEvents() {
        charEvent = (char input) -> {
            String command = inputProcessor.getCommand();
            int cursorPos = inputProcessor.getCursorPos();

            InputHandler.clearLine(command, true);

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
        };
    }

    private void setUpBackspaceEvent() {
        backspaceEvent = () -> {
            if (inputProcessor.getCommand().length() > 0 && inputProcessor.getCursorPos() > 0) {
                int charToDelete = inputProcessor.getCursorPos() - 1;
                String command = inputProcessor.getCommand();

                InputHandler.clearLine(command, true);

                inputProcessor.setCommand(new StringBuilder(command).deleteCharAt(charToDelete).toString());
                Ezcli.ezcliOutput.print(Ezcli.prompt, "prompt");
                Ezcli.ezcliOutput.print(inputProcessor.getCommand(), "command");

                inputProcessor.decreaseCursorPos();
                inputProcessor.moveToCursorPos();
            }
        };
    }
}
