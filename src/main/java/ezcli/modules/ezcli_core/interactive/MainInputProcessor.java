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

    private String command = ""; // command string for use in interactive mode

    protected void setCommand(String command) {
        this.command = command;
    }

    protected String getCommand() {
        return command;
    }

    private Interactive interactive;

    MainInputProcessor(Interactive interactive) {
        super();
        this.interactive = interactive;
        keyHandler = new MainKeyProcessor(this);
        KeyHandler.initKeysMap();
    }

    @Override
    public void process(int input) {
        keyHandler.process(input);
    }

    protected void parse() {
        interactive.parse = true;
    }
}
