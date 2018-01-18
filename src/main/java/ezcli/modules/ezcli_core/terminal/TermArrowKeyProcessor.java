package ezcli.modules.ezcli_core.terminal;

import ezcli.modules.ezcli_core.modularity.EventState;
import ezcli.modules.ezcli_core.Ezcli;
import ezcli.modules.ezcli_core.modularity.Module;
import ezcli.modules.ezcli_core.global_io.handlers.ArrowKeyHandler;
import ezcli.modules.ezcli_core.global_io.ArrowKeys;
import ezcli.modules.ezcli_core.util.Util;

/**
 * Processes arrow keys for Terminal module.
 *
 * @see Terminal
 */
public class TermArrowKeyProcessor extends ArrowKeyHandler {

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
            Module.processEvent("larrow", EventState.PRE_EVENT);
            if (inputProcessor.getCursorPos() > 0) {
                Ezcli.ezcliOutput.print("\b", "command");
                inputProcessor.decreaseCursorPos();
            }
            Module.processEvent("larrow", EventState.POST_EVENT);
        };
    }

    private void setRArrowBehaviour() {
        rArrEvent = () -> {
            Module.processEvent("rarrow", EventState.PRE_EVENT);
            if (inputProcessor.getCursorPos() < inputProcessor.getCommand().length()) {
                Util.clearLine(inputProcessor.getCommand(), true);
                Ezcli.ezcliOutput.print(Ezcli.prompt, "prompt");
                Ezcli.ezcliOutput.print(inputProcessor.getCommand(), "command");
                inputProcessor.increaseCursorPos();
//            inputProcessor.moveToCursorPos();
            }
            Module.processEvent("rarrow", EventState.POST_EVENT);
        };
    }

    private void setUArrowBehaviour() {
        uArrEvent = () -> {
            Module.processEvent("uarrow", EventState.PRE_EVENT);
            prevCommandIterator(ArrowKeys.UP);
            inputProcessor.setCursorPos(inputProcessor.getCommand().length());
            Module.processEvent("uarrow", EventState.POST_EVENT);
        };
    }

    private void setDArrowBehaviour() {
        dArrEvent = () -> {
            Module.processEvent("darrow", EventState.PRE_EVENT);
            prevCommandIterator(ArrowKeys.DOWN);
            inputProcessor.setCursorPos(inputProcessor.getCommand().length());
            Module.processEvent("darrow", EventState.POST_EVENT);
        };
    }

    /**
     * Iterates through the prevCommands list. Emulates Unix terminal behaviour when using
     * vertical arrow keys.
     *
     * @param ak Arrow key to process
     */
    private void prevCommandIterator(ArrowKeys ak) {
        if (inputProcessor.commandHistory.size() == 0)
            return;

        if (commandListPosition == inputProcessor.commandHistory.size() && lastArrowPress == ArrowKeys.NONE)
            // Saves currently typed command before moving through the list of previously typed commands

            currCommand = inputProcessor.getCommand();

        if (ak == ArrowKeys.UP && commandListPosition > 0) {
            // Move through the list towards first typed command

            lastArrowPress = ak;
            Util.clearLine(inputProcessor.getCommand(), true);

            if (commandListPosition > inputProcessor.commandHistory.size())
                commandListPosition = inputProcessor.commandHistory.size();

            Ezcli.ezcliOutput.print(Ezcli.prompt, "prompt");
            Ezcli.ezcliOutput.print(inputProcessor.commandHistory.get(--commandListPosition), "command");
            inputProcessor.setCommand(inputProcessor.commandHistory.get(commandListPosition));

        } else if (ak == ArrowKeys.DOWN && commandListPosition < inputProcessor.commandHistory.size() - 1) {
            // Move through list towards last typed element

            lastArrowPress = ak;
            Util.clearLine(inputProcessor.getCommand(), true);

            Ezcli.ezcliOutput.print(Ezcli.prompt, "prompt");
            Ezcli.ezcliOutput.print(inputProcessor.commandHistory.get(++commandListPosition), "info");
            inputProcessor.setCommand(inputProcessor.commandHistory.get(commandListPosition));

        } else if (ak == ArrowKeys.DOWN && commandListPosition >= inputProcessor.commandHistory.size() - 1) {
            // Print command that was stored before iteration through list began

            lastArrowPress = ak;
            Util.clearLine(inputProcessor.getCommand(), true);
            commandListPosition++;

            Ezcli.ezcliOutput.print(Ezcli.prompt, "prompt");
            Ezcli.ezcliOutput.print(currCommand, "command");
            inputProcessor.setCommand(currCommand);
        }
    }
}
