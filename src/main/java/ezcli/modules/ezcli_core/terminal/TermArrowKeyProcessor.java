package ezcli.modules.ezcli_core.terminal;

import ezcli.modules.ezcli_core.EventState;
import ezcli.modules.ezcli_core.Ezcli;
import ezcli.modules.ezcli_core.Module;
import ezcli.modules.ezcli_core.global_io.ArrowKeyHandler;
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
    }

    protected void setCurrCommand(String currCommand) {
        this.currCommand = currCommand;
    }

    protected void setCommandListPosition(int commandListPosition) {
        this.commandListPosition = commandListPosition;
    }

    /**
     * Processes arrow keys presses and delegates them to abstract implementations.
     *
     * @param ak arrow key pressed (if any)
     * @return arrow key pressed
     */
    @Override
    public ArrowKeys process(ArrowKeys ak) {

        if (ak != ArrowKeys.NONE) {
            switch (ak) {
                case UP:
                    processUp(); break;
                case DOWN:
                    processDown(); break;
                case LEFT:
                    processLeft(); break;
                case RIGHT:
                    processRight(); break;
                default:
                    return ak; // Should never run
            }
        }
        return ak;
    }

    @Override
    protected void processUp() {
        Module.processEvent("uarrow", EventState.PRE_EVENT);
        prevCommandIterator(ArrowKeys.UP);
        inputProcessor.setCursorPos(inputProcessor.getCommand().length());
        Module.processEvent("uarrow", EventState.POST_EVENT);
    }

    @Override
    protected void processDown() {
        Module.processEvent("darrow", EventState.PRE_EVENT);
        prevCommandIterator(ArrowKeys.DOWN);
        inputProcessor.setCursorPos(inputProcessor.getCommand().length());
        Module.processEvent("darrow", EventState.POST_EVENT);
    }

    @Override
    protected void processLeft() {
        Module.processEvent("larrow", EventState.PRE_EVENT);
        if (inputProcessor.getCursorPos() > 0) {
            System.out.print("\b");
            inputProcessor.decreaseCursorPos();
        }
        Module.processEvent("larrow", EventState.POST_EVENT);
    }

    @Override
    protected void processRight() {
        Module.processEvent("rarrow", EventState.PRE_EVENT);
        if (inputProcessor.getCursorPos() < inputProcessor.getCommand().length()) {
            Util.clearLine(inputProcessor.getCommand(), true);
            System.out.print(Ezcli.prompt);
            System.out.print(inputProcessor.getCommand());
            inputProcessor.increaseCursorPos();
            inputProcessor.moveToCursorPos();
        }
        Module.processEvent("rarrow", EventState.POST_EVENT);
    }

    /**
     * Iterates through the prevCommands list. Emulates Unix terminal behaviour when using
     * vertical arrow keys.
     *
     * @param ak Arrow key to process
     */
    private void prevCommandIterator(ArrowKeys ak) {
        if (inputProcessor.getPrevCommands().size() == 0)
            return;

        if (commandListPosition == inputProcessor.getPrevCommands().size() && lastArrowPress == ArrowKeys.NONE)
            // Saves currently typed command before moving through the list of previously typed commands

            currCommand = inputProcessor.getCommand();

        if (ak == ArrowKeys.UP && commandListPosition > 0) {
            // Move through the list towards first typed command

            lastArrowPress = ak;
            Util.clearLine(inputProcessor.getCommand(), true);

            if (commandListPosition > inputProcessor.getPrevCommands().size())
                commandListPosition = inputProcessor.getPrevCommands().size();

            System.out.print(Ezcli.prompt);
            System.out.print(inputProcessor.getPrevCommands().get(--commandListPosition));
            inputProcessor.setCommand(inputProcessor.getPrevCommands().get(commandListPosition));

        } else if (ak == ArrowKeys.DOWN && commandListPosition < inputProcessor.getPrevCommands().size() - 1) {
            // Move through list towards last typed element

            lastArrowPress = ak;
            Util.clearLine(inputProcessor.getCommand(), true);

            System.out.print(Ezcli.prompt);
            System.out.print(inputProcessor.getPrevCommands().get(++commandListPosition));
            inputProcessor.setCommand(inputProcessor.getPrevCommands().get(commandListPosition));

        } else if (ak == ArrowKeys.DOWN && commandListPosition >= inputProcessor.getPrevCommands().size() - 1) {
            // Print command that was stored before iteration through list began

            lastArrowPress = ak;
            Util.clearLine(inputProcessor.getCommand(), true);
            commandListPosition++;

            System.out.print(Ezcli.prompt);
            System.out.print(currCommand);
            inputProcessor.setCommand(currCommand);
        }
    }
}
