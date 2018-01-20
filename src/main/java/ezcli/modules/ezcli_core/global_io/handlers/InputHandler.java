package ezcli.modules.ezcli_core.global_io.handlers;

import java.io.IOException;
import ezcli.modules.ezcli_core.global_io.input.Input;

/**
 * Abstract class specifying how input should be handled.
 * Each module must run its own implementation of this class.
 */
public abstract class InputHandler {

    protected ArrowKeyHandler arrowKeyHandler;
    protected KeyHandler keyHandler;

    /*
        Determines how long the program will ignore user input (in ms).
        Prevents program from going visually insane, by not overloading the system with too much output.
    */
    public static int minWaitTime = 40;

    /**
     * Create key handler object with null arrow key handler and key handler.
     * Only call this constructor if you plan to manually assign the handlers
     * straight after this constructor returns.
     */
    public InputHandler() {
        arrowKeyHandler = null;
        keyHandler = null;
    }

    /**
     * Set KeyHandler and ArrowKeyHandler for module.
     * <br></br>
     * <p>
     * Classes extending KeyHandler and ArrowKeyHandler should be written, and passed through this function,
     * so they can be later used to process input for this module.
     *
     * @param kh key handler to use
     * @param akh arrow key handler to use
     */
    public InputHandler(KeyHandler kh, ArrowKeyHandler akh) {
        this.arrowKeyHandler = akh;
        this.keyHandler = kh;
    }

    /**
     * Code to run when processing input for module.
     * Can (and should) make use of keyHandler and/or arrowKeyHandler for input processing.
     */
    public abstract void process(int input);

    /**
     * Returns key char value of last key pressed.
     * @return Char value of key pressed
     */
    public static char getKey() {
        try {
            return (char) Input.read(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
