package ezcli.modules.ezcli_core.term;

import ezcli.Ezcli;
import ezcli.modules.ezcli_core.global_io.ArrowKeyHandler;
import ezcli.modules.ezcli_core.global_io.ArrowKeys;
import ezcli.modules.ezcli_core.util.Util;

public class TermArrowKeyProcessor extends ArrowKeyHandler {

    private static String originalCommand = ""; // stores previous command before it is modified

    /**
     * Processes arrow keys presses.
     * <br></br>
     *
     * @param ak arrow key pressed (if any)
     * @return arrow key pressed
     */
    @Override
    public ArrowKeys process(ArrowKeys ak) {
        if (ak != ArrowKeys.NONE && ak != ArrowKeys.MOD) {
            if (TermInputProcessor.commandListPosition == TermInputProcessor.prevCommands.size() && lastArrowPress == ArrowKeys.NONE)
                TermInputProcessor.currCommand = TermInputProcessor.command;

            if (ak == ArrowKeys.UP && TermInputProcessor.commandListPosition > 0) {
                lastArrowPress = ak;
                Util.clearLine(TermInputProcessor.command, true);

                if (TermInputProcessor.commandListPosition > TermInputProcessor.prevCommands.size())
                    TermInputProcessor.commandListPosition = TermInputProcessor.prevCommands.size();

                System.out.print(Ezcli.prompt + TermInputProcessor.prevCommands.get(--TermInputProcessor.commandListPosition));
                TermInputProcessor.command = TermInputProcessor.prevCommands.get(TermInputProcessor.commandListPosition);

            } else if (ak == ArrowKeys.DOWN && TermInputProcessor.commandListPosition < TermInputProcessor.prevCommands.size() - 1) {
                lastArrowPress = ak;
                Util.clearLine(TermInputProcessor.command, true);

                System.out.print(Ezcli.prompt + TermInputProcessor.prevCommands.get(++TermInputProcessor.commandListPosition));
                TermInputProcessor.command = TermInputProcessor.prevCommands.get(TermInputProcessor.commandListPosition);

            } else if (ak == ArrowKeys.DOWN && TermInputProcessor.commandListPosition >= TermInputProcessor.prevCommands.size() - 1) {
                lastArrowPress = ak;
                Util.clearLine(TermInputProcessor.command, true);
                TermInputProcessor.commandListPosition++;

                System.out.print(Ezcli.prompt + TermInputProcessor.currCommand);
                TermInputProcessor.command = TermInputProcessor.currCommand;
            }
            return ak;
        } else if (ak != ArrowKeys.MOD) {
            /*
             * Only executes when an element in the TermInputProcessor.prevCommands list is not being modified.
			 * ArrowKeys.MOD used to indicate to the InputHandler that it should not go back in to this block,
			 * and continue on with execution of ProcessUnix as per usual, which will modify the string currently
			 * displayed.
			 */
            vals = new int[3];
            pos = 0;
            lastArrowPress = ArrowKeys.NONE;
            if (TermInputProcessor.commandListPosition < TermInputProcessor.prevCommands.size() && TermInputProcessor.prevCommands.size() > 0) {
                String tmp = TermInputProcessor.command; // save current command
                int prevCMDPos = TermInputProcessor.commandListPosition;
                TermInputProcessor.command = TermInputProcessor.prevCommands.get(prevCMDPos); // set global command to one currently displayed
                if (originalCommand.equals(""))
                    originalCommand = TermInputProcessor.command;
                //TermInputProcessor.process(key, ArrowKeys.MOD, input); // process current input and apply on currently displayed command
                if (TermInputProcessor.command.equals("")) { // if Keys.NWLN was input, set command to tmp to avoid blank lines in list
                    TermInputProcessor.prevCommands.set(prevCMDPos, originalCommand);
                    originalCommand = "";
                } else { // if no newline, follow as per usual (see below)
                    TermInputProcessor.prevCommands.set(prevCMDPos, TermInputProcessor.command); // set command to modified version
                    TermInputProcessor.command = tmp; // set global command back to previous state
                }
                return ak;
            }
        }
        return ak;
    }
}
