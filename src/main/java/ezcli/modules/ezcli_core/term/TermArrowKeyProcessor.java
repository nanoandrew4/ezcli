package ezcli.modules.ezcli_core.term;

import ezcli.modules.ezcli_core.Ezcli;
import ezcli.modules.ezcli_core.global_io.ArrowKeyHandler;
import ezcli.modules.ezcli_core.global_io.ArrowKeys;
import ezcli.modules.ezcli_core.util.Util;

/**
 * Processes arrow keys for Terminal module.
 *
 * @see Terminal
 */
public class TermArrowKeyProcessor extends ArrowKeyHandler {

    private String originalCommand = ""; // stores previous getCommand() before it is modified
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

        if (ak != ArrowKeys.NONE && ak != ArrowKeys.MOD) { // process up, down, left and right arrow keys
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
            return ak;
        } else if (ak != ArrowKeys.MOD) {
            /*
             * Only executes when an element in the TermInputProcessor.getPrevCommands() list is not being modified.
			 * ArrowKeys.MOD used to indicate to the InputHandler that it should not go back in to this block,
			 * and continue on with execution of ProcessUnix as per usual, which will modify the string currently
			 * displayed.
			 */
            vals = new int[3];
            pos = 0;
            lastArrowPress = ArrowKeys.NONE;
            if (commandListPosition < inputProcessor.getPrevCommands().size() && inputProcessor.getPrevCommands().size() > 0) {
                String tmp = inputProcessor.getCommand(); // save current getCommand()
                int prevCMDPos = commandListPosition;
                inputProcessor.setCommand(inputProcessor.getPrevCommands().get(prevCMDPos)); // set global getCommand() to one currently displayed
                if ("".equals(originalCommand))
                    originalCommand = inputProcessor.getCommand();
                //TermInputProcessor.process(key, ArrowKeys.MOD, input); // process current input and apply on currently displayed getCommand()
                if (inputProcessor.getCommand().equals("")) { // if Keys.NWLN was input, set getCommand() to tmp to avoid blank lines in list
                    inputProcessor.getPrevCommands().set(prevCMDPos, originalCommand);
                    originalCommand = "";
                } else { // if no newline, follow as per usual (see below)
                    inputProcessor.getPrevCommands().set(prevCMDPos, inputProcessor.getCommand()); // set getCommand() to modified version
                    inputProcessor.setCommand(tmp); // set global getCommand() back to previous state
                }
                return ak;
            }
        }
        return ak;
    }
}
