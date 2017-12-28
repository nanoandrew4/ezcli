package ezcli.modules.ezcli_core.terminal;

import ezcli.modules.ezcli_core.EventState;
import ezcli.modules.ezcli_core.Ezcli;
import ezcli.modules.ezcli_core.Module;
import ezcli.modules.ezcli_core.global_io.KeyHandler;
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

    private String suggestion = "";

    TermKeyProcessor(TermInputProcessor inputProcessor) {
        this.inputProcessor = inputProcessor;
    }

    protected String getSuggestion() {
        return suggestion;
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

        Util.clearLine(inputProcessor.getCommand() + suggestion, true);
        suggestion = "";
        //inputProcessor.fileAutocomplete();
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

        Util.clearLine(command + suggestion, true);

        if (inputProcessor.getCursorPos() == command.length()) {
            System.out.print(Ezcli.prompt);
            System.out.print(command + input);
            inputProcessor.setCommand(command + input);
        } else {
            inputProcessor.setCommand(new StringBuilder(command).insert(cursorPos, input).toString());
            System.out.print(Ezcli.prompt);
            System.out.print(inputProcessor.getCommand());
        }

        //printSuggestion();

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

            Util.clearLine(command + suggestion, true);

            inputProcessor.setCommand(new StringBuilder(command).deleteCharAt(charToDelete).toString());
            System.out.print(Ezcli.prompt);
            System.out.print(inputProcessor.getCommand());

            //printSuggestion();

            if (inputProcessor.getCursorPos() == 1)
                suggestion = "";

            inputProcessor.decreaseCursorPos();
            inputProcessor.moveToCursorPos();
            inputProcessor.setResetVars(true);
        }

        Module.processEvent(String.valueOf("\b"), EventState.POST_EVENT);
    }
    /*
    private void printSuggestion() {

        //suggestion = cmdComplete.getMatchingCommand(inputProcessor.getCommand());

        if (!"".equals(suggestion) && !"".equals(inputProcessor.getCommand())) {
            System.out.print(suggestion, ColorOutput.CMD_SUGGESTION);
        }
    }
    */
}
