package ezcli.modules.ezcli_core.interactive;

import ezcli.modules.ezcli_core.global_io.InputHandler;
import ezcli.modules.ezcli_core.global_io.KeyHandler;
import ezcli.modules.ezcli_core.global_io.input.Input;

import java.io.IOException;

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

    MainInputProcessor(Interactive interactive) {
        this.interactive = interactive;
        keyHandler = new MainKeyProcessor(this);
        KeyHandler.initKeysMap();
    }

    public MainKeyProcessor getKeyProcessor() {
        return (MainKeyProcessor) keyHandler;
    }

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

    @Override
    public void process(int input) {
        try {
            Input.read(false);
            Input.read(false);
        } catch (IOException e) {
            System.out.println("Error in Interactive arrow catching");
        }
        keyHandler.process(input);
    }

    protected void parse() {
        interactive.parse(command);
    }
}
