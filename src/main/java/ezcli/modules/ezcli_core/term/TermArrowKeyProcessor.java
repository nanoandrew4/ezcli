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

    private static String originalCommand = ""; // stores previous getCommand() before it is modified

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
            if (TermInputProcessor.commandListPosition == TermInputProcessor.getPrevCommands().size() && lastArrowPress == ArrowKeys.NONE)
                TermInputProcessor.currCommand = TermInputProcessor.getCommand();

            if (ak == ArrowKeys.UP && TermInputProcessor.commandListPosition > 0) {
                lastArrowPress = ak;
                Util.clearLine(TermInputProcessor.getCommand(), true);

                if (TermInputProcessor.commandListPosition > TermInputProcessor.getPrevCommands().size())
                    TermInputProcessor.commandListPosition = TermInputProcessor.getPrevCommands().size();

                System.out.print(Ezcli.prompt + TermInputProcessor.getPrevCommands().get(--TermInputProcessor.commandListPosition));
                TermInputProcessor.setCommand(TermInputProcessor.getPrevCommands().get(TermInputProcessor.commandListPosition));

            } else if (ak == ArrowKeys.DOWN && TermInputProcessor.commandListPosition < TermInputProcessor.getPrevCommands().size() - 1) {
                lastArrowPress = ak;
                Util.clearLine(TermInputProcessor.getCommand(), true);

                System.out.print(Ezcli.prompt + TermInputProcessor.getPrevCommands().get(++TermInputProcessor.commandListPosition));
                TermInputProcessor.setCommand(TermInputProcessor.getPrevCommands().get(TermInputProcessor.commandListPosition));

            } else if (ak == ArrowKeys.DOWN && TermInputProcessor.commandListPosition >= TermInputProcessor.getPrevCommands().size() - 1) {
                lastArrowPress = ak;
                Util.clearLine(TermInputProcessor.getCommand(), true);
                TermInputProcessor.commandListPosition++;

                System.out.print(Ezcli.prompt + TermInputProcessor.currCommand);
                TermInputProcessor.setCommand(TermInputProcessor.currCommand);
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
            if (TermInputProcessor.commandListPosition < TermInputProcessor.getPrevCommands().size() && TermInputProcessor.getPrevCommands().size() > 0) {
                String tmp = TermInputProcessor.getCommand(); // save current getCommand()
                int prevCMDPos = TermInputProcessor.commandListPosition;
                TermInputProcessor.setCommand(TermInputProcessor.getPrevCommands().get(prevCMDPos)); // set global getCommand() to one currently displayed
                if (originalCommand.equals(""))
                    originalCommand = TermInputProcessor.getCommand();
                //TermInputProcessor.process(key, ArrowKeys.MOD, input); // process current input and apply on currently displayed getCommand()
                if (TermInputProcessor.getCommand().equals("")) { // if Keys.NWLN was input, set getCommand() to tmp to avoid blank lines in list
                    TermInputProcessor.getPrevCommands().set(prevCMDPos, originalCommand);
                    originalCommand = "";
                } else { // if no newline, follow as per usual (see below)
                    TermInputProcessor.getPrevCommands().set(prevCMDPos, TermInputProcessor.getCommand()); // set getCommand() to modified version
                    TermInputProcessor.setCommand(tmp); // set global getCommand() back to previous state
                }
                return ak;
            }
        }
        return ak;
    }
}
