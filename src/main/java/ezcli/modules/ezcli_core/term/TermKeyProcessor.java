package ezcli.modules.ezcli_core.term;

import ezcli.modules.ezcli_core.global_io.KeyHandler;
import ezcli.modules.ezcli_core.global_io.Keys;
import ezcli.modules.ezcli_core.util.FileAutocomplete;

/**
 * Processes key presses (except arrow keys) for Terminal module.
 *
 * @see Terminal
 */
public class TermKeyProcessor extends KeyHandler {

    private Terminal terminal;
    private TermInputProcessor inputProcessor;

    TermKeyProcessor(Terminal terminal, TermInputProcessor inputProcessor) {
        this.terminal = terminal;
        this.inputProcessor = inputProcessor;
    }

    /**
     * Processes input provided by input class,
     * and operates based on the input it receives,
     * using the character value passed by the process() method.
     * <br></br>
     *
     * @param input last character input by user
     */
    @Override
    public void process(int input) {

        Keys key = getKey(input);

        // reset if input is not tab
        if (key != Keys.TAB) {
            inputProcessor.setLockTab(false);
            inputProcessor.setBlockClear(false);
            FileAutocomplete.resetVars();
        }

        super.process(input);
    }

    @Override
    public void tabEvent() {

        // Split into sections
        String[] commandArr = inputProcessor.getCommand().split(" ");

        // Get last element
        String currText = commandArr[commandArr.length - 1] + (inputProcessor.getCommand().endsWith(" ") ? " " : "");

        // If more than one element, autocomplete file
        if (commandArr.length > 1 || inputProcessor.getCommand().endsWith(" "))
            inputProcessor.fileAutocomplete(currText);
    }

    @Override
    public void newLineEvent() {
        boolean empty = Terminal.containsOnlySpaces(inputProcessor.getCommand());
        terminal.parse = true;

        if (!empty)
            inputProcessor.getPrevCommands().add(inputProcessor.getCommand());
        inputProcessor.getArrowKeyProcessor().setCommandListPosition(inputProcessor.getPrevCommands().size());
        inputProcessor.getArrowKeyProcessor().setCurrCommand("");
        System.out.println(); // new line
    }

    @Override
    public void charEvent(char input) {
        System.out.print(input);
        inputProcessor.setCommand(inputProcessor.getCommand() + input);
    }

    @Override
    public void backspaceEvent() {
        if (inputProcessor.getCommand().length() > 0) {
            inputProcessor.setCommand(inputProcessor.getCommand().substring(0, inputProcessor.getCommand().length() - 1));

            // Delete char, add white space and move back again
            System.out.print("\b \b");
        }
    }
}
