package ezcli.modules.ezcli_core.interactive;

import ezcli.modules.ezcli_core.global_io.KeyHandler;

/**
 * Processes key presses for Interactive module.
 *
 * @see Interactive
 */
public class MainKeyProcessor extends KeyHandler {

    private long lastPress = System.currentTimeMillis();
    private char lastInput = 0;

    @Override
    public void process(char input) {

        /*
         * Prevents arrow key char values from being processed or printed in Unix systems.
         * This can occur due to the way arrow keys are processed by the application on Unix systems.
         * See ArrowKeyHandler.arrowKeyCheckUnix() for more info
         */
        if (System.currentTimeMillis() - lastPress < 20) {
            if (lastInput == 'A') {
                System.out.println("\b \b");
                MainInputProcessor.command = MainInputProcessor.command.substring(0, MainInputProcessor.command.length() - 1);
            }
            return;
        }

        lastPress = System.currentTimeMillis();
        lastInput = input;

        super.process(input);
    }

    @Override
    public void tabEvent() {
        // do not process tab key
    }

    @Override
    public void newLineEvent() {
        // do not process enter key
    }

    @Override
    public void charEvent(char input) {
        MainInputProcessor.command += input;
        Interactive.parse = true;
        System.out.print(input);
    }

    @Override
    public void backspaceEvent() {
        // do not process backspace key
    }
}
