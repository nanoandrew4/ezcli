package ezcli.modules.ezcli_core.interactive;

import ezcli.modules.ezcli_core.global_io.Input;
import ezcli.modules.ezcli_core.global_io.InputHandler;

import java.io.IOException;

/**
 * Input processor for interactive module.
 *
 * @see Interactive
 * @see MainKeyProcessor
 */
public class MainInputProcessor extends InputHandler {

    static String command = ""; // command string for use in interactive mode

    MainInputProcessor() {
        super(new MainKeyProcessor(), null);
    }

    @Override
    public void process() {
        char input = 0;

        try {
            input = (char) Input.read(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        keyHandler.process(input);
    }
}
