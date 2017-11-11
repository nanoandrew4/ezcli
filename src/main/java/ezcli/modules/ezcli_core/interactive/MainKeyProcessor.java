package ezcli.modules.ezcli_core.interactive;

import ezcli.modules.ezcli_core.global_io.KeyHandler;

/**
 * Processes key presses (except arrow keys) for Interactive module.
 *
 * @see Interactive
 */
public class MainKeyProcessor extends KeyHandler {

    private MainInputProcessor inputProcessor;

    private long lastPress = System.currentTimeMillis();
    private char lastInput = 0;

    MainKeyProcessor(MainInputProcessor inputProcessor) {
        this.inputProcessor = inputProcessor;
    }

    @Override
    public void process(int input) {

        /*
         * Prevents arrow key char values from being processed or printed in Unix systems.
         * This can occur due to the way arrow keys are processed by the application on Unix systems.
         * See ArrowKeyHandler.arrowKeyCheckUnix() for more info
         */
        if (System.currentTimeMillis() - lastPress < 10) {
            if (lastInput == 'A') {
                System.out.println("\b \b");

                String command = inputProcessor.getCommand();
                int commandLength = command.length();

                inputProcessor.setCommand(new StringBuilder(command).deleteCharAt(commandLength).toString());
            }
            return;
        }

        lastPress = System.currentTimeMillis();
        lastInput = (char)input;

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
        // Do not process backspace key
    }
}
