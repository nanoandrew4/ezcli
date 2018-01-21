package ezcli.modules.ezcli_core.terminal;

import ezcli.modules.ezcli_core.Ezcli;
import ezcli.modules.ezcli_core.global_io.ArrowKeys;
import ezcli.modules.ezcli_core.global_io.handlers.ArrowKeyHandler;
import ezcli.modules.ezcli_core.global_io.handlers.InputHandler;

/**
 * Processes arrow keys for Terminal module.
 *
 * @see Terminal
 * @see TermInputProcessor
 */
public class TermArrowKeyProcessor extends ArrowKeyHandler {

    // Input processor owning this class
    private TermInputProcessor inputProcessor;

    // Position on prevCommands list (used to iterate through it)
    private int commandListPosition = 0;

    // Stores current TermInputProcessor.command when iterating through prevCommands
    private String currCommand = "";

    TermArrowKeyProcessor(TermInputProcessor inputProcessor) {
        this.inputProcessor = inputProcessor;
        setLArrowBehaviour();
        setRArrowBehaviour();
        setUArrowBehaviour();
        setDArrowBehaviour();
    }

    protected void setCurrCommand(String currCommand) {
        this.currCommand = currCommand;
    }

    protected void setCommandListPosition(int commandListPosition) {
        this.commandListPosition = commandListPosition;
    }

    private void setLArrowBehaviour() {
        lArrEvent = () -> {
            if (inputProcessor.getCursorPos() > 0) {
                Ezcli.ezcliOutput.print("\b", "command");
                inputProcessor.decreaseCursorPos();
            }
        };
    }

    private void setRArrowBehaviour() {
        rArrEvent = () -> {
            if (inputProcessor.getCursorPos() < inputProcessor.getCommand().length()) {
                InputHandler.clearLine(inputProcessor.getCommand(), true);
                Ezcli.ezcliOutput.print(Ezcli.prompt, "prompt");
                Ezcli.ezcliOutput.print(inputProcessor.getCommand(), "command");
                inputProcessor.increaseCursorPos();
                inputProcessor.moveToCursorPos();
            }
        };
    }

    private void setUArrowBehaviour() {
        uArrEvent = () -> {
            prevCommandIterator(ArrowKeys.UP);
            inputProcessor.setCursorPos(inputProcessor.getCommand().length());
        };
    }

    private void setDArrowBehaviour() {
        dArrEvent = () -> {
            prevCommandIterator(ArrowKeys.DOWN);
            inputProcessor.setCursorPos(inputProcessor.getCommand().length());
        };
    }

    /**
     * Iterates through the prevCommands list. Emulates Unix terminal behaviour when using
     * vertical arrow keys in the terminal.
     *
     * @param ak Arrow key to process
     */
    private void prevCommandIterator(ArrowKeys ak) {
        if (inputProcessor.commandHistory.size() == 0)
            return;

        int cmdHistorySize = inputProcessor.commandHistory.size() - 1;

        if (commandListPosition == inputProcessor.commandHistory.size() && lastArrowPress == ArrowKeys.NONE)
            currCommand = inputProcessor.getCommand();

        if (ak == ArrowKeys.UP && commandListPosition > 0) {
            // Move through the list towards first typed command

            lastArrowPress = ak;
            InputHandler.clearLine(inputProcessor.getCommand(), true);

            if (commandListPosition > inputProcessor.commandHistory.size())
                commandListPosition = inputProcessor.commandHistory.size();

            Ezcli.ezcliOutput.print(Ezcli.prompt, "prompt");
            Ezcli.ezcliOutput.print(inputProcessor.commandHistory.get(--commandListPosition), "command");
            inputProcessor.setCommand(inputProcessor.commandHistory.get(commandListPosition));

        } else if (ak == ArrowKeys.DOWN) {
            lastArrowPress = ak;

            if (commandListPosition < cmdHistorySize) {
                // Move through list towards last typed element
                InputHandler.clearLine(inputProcessor.getCommand(), true);

                Ezcli.ezcliOutput.print(Ezcli.prompt, "prompt");
                Ezcli.ezcliOutput.print(inputProcessor.commandHistory.get(++commandListPosition), "info");
                inputProcessor.setCommand(inputProcessor.commandHistory.get(commandListPosition));
            } else if (!inputProcessor.getCommand().equals(currCommand)) {
                // Print command that was stored before iteration through list began
                InputHandler.clearLine(inputProcessor.getCommand(), true);
                commandListPosition++;

                Ezcli.ezcliOutput.print(Ezcli.prompt, "prompt");
                Ezcli.ezcliOutput.print(currCommand, "command");
                inputProcessor.setCommand(currCommand);
            }
        }
    }
}
