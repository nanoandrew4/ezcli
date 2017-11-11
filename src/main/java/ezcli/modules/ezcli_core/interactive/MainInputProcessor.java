package ezcli.modules.ezcli_core.interactive;

import ezcli.modules.ezcli_core.global_io.InputHandler;
import ezcli.modules.ezcli_core.global_io.KeyHandler;

/**
 * Input processor for interactive module. Only processes keys, not arrowKeys
 *
 * @see Interactive
 * @see MainKeyProcessor
 */
public class MainInputProcessor extends InputHandler {

    private Interactive interactive;

    private String command = "";

    // Stores previous command, for testing purposes only
    private String wasCommand = "";

    protected void setCommand(String command) {
        wasCommand = this.command;
        this.command = command;
    }

    protected String getCommand() {
        return command;
    }

    protected String getWasCommand() {
        return wasCommand;
    }

    MainInputProcessor(Interactive interactive) {
        this.interactive = interactive;
        keyHandler = new MainKeyProcessor(this);
        KeyHandler.initKeysMap();
    }

    @Override
    public void process(int input) {
        keyHandler.process(input);
    }

    protected void parse() {
        interactive.parse(command);
    }
}
