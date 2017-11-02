package ezcli.modules.ezcli_core.terminal;

import ezcli.modules.ezcli_core.Ezcli;
import ezcli.modules.ezcli_core.global_io.ArrowKeyHandler;
import ezcli.modules.ezcli_core.global_io.ArrowKeys;
import ezcli.modules.ezcli_core.global_io.InputHandler;
import ezcli.modules.ezcli_core.util.Util;

/**
 * Processes arrow keys for Terminal module.
 *
 * @see Terminal
 */
public class TermArrowKeyProcessor extends ArrowKeyHandler {

    private int commandListPosition = 0; // position on prevCommands list (used to iterate through it)

    private String currCommand = ""; // stores current TermInputProcessor.command when iterating through prevCommands

    private TermInputProcessor inputProcessor;

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
     * Processes arrow keys presses.
     *
     * @param ak arrow key pressed (if any)
     * @return arrow key pressed
     */
    @Override
    public ArrowKeys process(ArrowKeys ak) {

        if (ak != ArrowKeys.NONE) { // process up, down, left and right arrow keys
            switch (ak) {
                case UP:
                    processUp(); break;
                case DOWN:
                    processDown(); break;
                case LEFT:
                    processLeft(); break;
                case RIGHT:
                    processRight(); break;
            }
        }
        return ak;
    }

    @Override
    protected void processUp() {
        prevCommandIterator(ArrowKeys.UP);
        inputProcessor.setCursorPos(inputProcessor.getCommand().length());
    }

    @Override
    protected void processDown() {
        prevCommandIterator(ArrowKeys.DOWN);
        inputProcessor.setCursorPos(inputProcessor.getCommand().length());
    }

    @Override
    protected void processLeft() {
        if (inputProcessor.getCursorPos() > 0) {
            System.out.print("\b");
            inputProcessor.decreaseCursorPos();
        }
    }

    @Override
    protected void processRight() {
        if (inputProcessor.getCursorPos() < inputProcessor.getCommand().length()) {
            Util.clearLine(inputProcessor.getCommand(), true);
            System.out.print(Ezcli.prompt + inputProcessor.getCommand());
            inputProcessor.increaseCursorPos();
            inputProcessor.moveToCursorPos();
        }
    }

    private void prevCommandIterator(ArrowKeys ak) {
        if (commandListPosition == inputProcessor.getPrevCommands().size() && lastArrowPress == ArrowKeys.NONE)
            // saves currently typed command before moving through the list of previously typed commands
            currCommand = inputProcessor.getCommand();

        if (ak == ArrowKeys.UP && commandListPosition > 0) { // move through the list towards first typed command
            lastArrowPress = ak;
            Util.clearLine(inputProcessor.getCommand(), true);

            if (commandListPosition > inputProcessor.getPrevCommands().size()) // prevent array out of bounds
                commandListPosition = inputProcessor.getPrevCommands().size();

            // print previous command and set the command variable to it
            System.out.print(Ezcli.prompt + inputProcessor.getPrevCommands().get(--commandListPosition));
            inputProcessor.setCommand(inputProcessor.getPrevCommands().get(commandListPosition));

        } else if (ak == ArrowKeys.DOWN && commandListPosition < inputProcessor.getPrevCommands().size() - 1) {
            // move through list towards last typed element

            lastArrowPress = ak;
            Util.clearLine(inputProcessor.getCommand(), true);

            // print next command and set command variable to it
            System.out.print(Ezcli.prompt + inputProcessor.getPrevCommands().get(++commandListPosition));
            inputProcessor.setCommand(inputProcessor.getPrevCommands().get(commandListPosition));

        } else if (ak == ArrowKeys.DOWN && commandListPosition >= inputProcessor.getPrevCommands().size() - 1) {
            // print command that was stored before iteration through list began

            lastArrowPress = ak;
            Util.clearLine(inputProcessor.getCommand(), true);
            commandListPosition++;

            System.out.print(Ezcli.prompt + currCommand);
            inputProcessor.setCommand(currCommand);
        }
    }
}
