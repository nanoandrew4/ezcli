package ezcli.modules.ezcli_core.terminal;

import ezcli.modules.ezcli_core.modularity.EventState;
import ezcli.modules.ezcli_core.Ezcli;
import ezcli.modules.ezcli_core.modularity.Module;
import ezcli.modules.ezcli_core.global_io.handlers.KeyHandler;
import ezcli.modules.ezcli_core.global_io.Keys;
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
            //FileAutocomplete.resetVars();
        }

        super.process(input);
    }

    @Override
    public void tabEvent() {
        Module.processEvent("\t", EventState.PRE_EVENT);

        Util.clearLine(inputProcessor.getCommand(), true);
        inputProcessor.fileAutocomplete();
        inputProcessor.setResetVars(false);

        Module.processEvent("\t", EventState.POST_EVENT);
    }

    @Override
    public void newLineEvent() {
        Module.processEvent("\n", EventState.PRE_EVENT);

        boolean empty = "".equals(inputProcessor.getCommand().trim());

        String command = inputProcessor.getCommand();
        ArrayList<String> prevCommands = inputProcessor.getPrevCommands();

        if (!empty)
            prevCommands.add(command);

        inputProcessor.getArrowKeyProcessor().setCommandListPosition(prevCommands.size());
        inputProcessor.getArrowKeyProcessor().setCurrCommand("");
        inputProcessor.setCursorPos(0);
        inputProcessor.setResetVars(true);
        inputProcessor.parse();

        Module.processEvent("\n", EventState.POST_EVENT);
    }

    @Override
    public void charEvent(char input) {
        Module.processEvent(String.valueOf(input), EventState.PRE_EVENT);

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

        Module.processEvent(String.valueOf(input), EventState.POST_EVENT);
    }

    @Override
    public void backspaceEvent() {
        Module.processEvent(String.valueOf("\b"), EventState.PRE_EVENT);

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

        Module.processEvent(String.valueOf("\b"), EventState.POST_EVENT);
    }
}
