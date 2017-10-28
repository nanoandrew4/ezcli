package ezcli.modules.ezcli_core.global_io;

public abstract class InputHandler {

    public ArrowKeyHandler arrowKeyHandler;
    public KeyHandler keyHandler;

    public InputHandler(KeyHandler kh, ArrowKeyHandler akh) {
        this.arrowKeyHandler = akh;
        this.keyHandler = kh;
    }

    public abstract void process();
}
