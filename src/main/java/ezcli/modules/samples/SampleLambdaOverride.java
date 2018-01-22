package ezcli.modules.samples;

import ezcli.modules.ezcli_core.modularity.Module;
import ezcli.modules.ezcli_core.terminal.Terminal;

public class SampleLambdaOverride extends Module {

    /**
     * This class also extends Module, but is neither reactive nor interactive since it does not implement either
     * of the init methods. For this demonstration this will not be necessary.
     *
     * If this class were to be initialized at some point, pressing tab while inside the Terminal module would cause
     * the program to exit. This is meant to exemplify how other modules can modify an interactive modules
     * event processors.
     */
    SampleLambdaOverride() {
        super("SampleLambdaOverride");

        override();
    }

    private void override() {
        Terminal t = (Terminal) Module.modules.get("Terminal");
        t.inputProcessor.getKeyProcessor().tabEvent = () -> {
            System.exit(0);
        };
    }

    @Override
    public void run() {/* Do nothing */}

    @Override
    public void tour() {/* Do nothing */}
}
