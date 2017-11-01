package ezcli.modules.ezcli_core.interactive;

import ezcli.modules.ezcli_core.global_io.InputHandler;
import ezcli.modules.ezcli_core.global_io.KeyHandler;

/**
 * input processor for interactive module.
 *
 * @see Interactive
 * @see MainKeyProcessor
 */
public class MainInputProcessor extends InputHandler {

    private static String command = ""; // command string for use in interactive mode

    protected static void setCommand(String command) {
        MainInputProcessor.command = command;
    }

    protected static String getCommand() {
        return command;
    }

    MainInputProcessor() {
        super(new MainKeyProcessor(), null);
        KeyHandler.initKeysMap();
    }

    @Override
    public void process(int input) {
        keyHandler.process(input);
    }
}
