package ezcli.modules.samples;

import ezcli.modules.ezcli_core.modularity.EventState;
import ezcli.modules.ezcli_core.modularity.Module;

public class Module1 extends Module {

    public static char someChar = '\0';

    /**
     * Constructor calls superclass constructor, and followed by the reactive-module init method, specifying
     * what set of keys each method should react to (which triggers an event with the key name),
     * a set of method names and at what stage they should be run (currently before or after the event).
     *
     * For more info on how events are triggered, see the KeyHandler and ArrowKeyHandler classes.
     * For more info on how events are handled, and by extension, how each reactive module is called,
     * see the Module class.
     *
     */
    public Module1() {
        super("Module1");
        init(this,
                new String[]{"uarrow darrow"}, new String[]{"doSomething"}, new EventState[]{EventState.PRE_EVENT});
    }

    /**
     * This method will be called if the up or down arrow key is pressed, and this method will run before
     * any processing happens in the interactive module from which the event was triggered.
     */
    public void doSomething() {
        Module2.someInt++;
    }

    @Override
    public void run() {/* Nothing to do */}

    @Override
    public void tour() {/* Nothing to do */}
}
