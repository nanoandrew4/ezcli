package ezcli.modules.ezcli_core.global_io;

import ezcli.modules.ezcli_core.global_io.Input.Input;

import java.io.IOException;

/**
 * Abstract class specifying how input should be handled.
 * Each module must run its own implementation of this class.
 */
public abstract class InputHandler {

    protected ArrowKeyHandler arrowKeyHandler;
    protected KeyHandler keyHandler;

    /**
     * Set KeyHandler and ArrowKeyHandler for module.
     * <br></br><br></br>
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

    public static char getKey() {
        try {
            return (char) Input.read(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
