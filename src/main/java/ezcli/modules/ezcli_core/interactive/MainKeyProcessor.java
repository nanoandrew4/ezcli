package ezcli.modules.ezcli_core.interactive;

import ezcli.modules.ezcli_core.global_io.KeyHandler;

/**
 * Processes key presses (except arrow keys) for Interactive module.
 *
 * @see Interactive
 */
public class MainKeyProcessor extends KeyHandler {

    private MainInputProcessor inputProcessor;

    MainKeyProcessor(MainInputProcessor inputProcessor) {
        this.inputProcessor = inputProcessor;
    }

    @Override
    public void process(int input) {
        super.process(input);
    }

    @Override
    public void tabEvent() {
        // Do not process tab key
    }

    @Override
    public void newLineEvent() {
        // Do not process enter key
    }

    @Override
    public void charEvent(char input) {
        inputProcessor.setCommand(inputProcessor.getCommand() + input);
        System.out.print(input);
        inputProcessor.parse();
    }

    @Override
    public void backspaceEvent() {
        if (inputProcessor.getCommand().length() > 0) {
            System.out.println("\b \b");

            String command = inputProcessor.getCommand();
            int commandLength = command.length();

            inputProcessor.setCommand(new StringBuilder(command).deleteCharAt(commandLength).toString());
        }
    }
}
