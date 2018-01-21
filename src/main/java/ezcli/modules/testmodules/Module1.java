package ezcli.modules.testmodules;

import ezcli.modules.ezcli_core.modularity.EventState;
import ezcli.modules.ezcli_core.modularity.Module;

public class Module1 extends Module {

    public static char someChar = '\0';

    public Module1() {
        super("Module1");
        init(this,
                new String[]{"uarrow darrow"}, new String[]{"doSomething"}, new EventState[]{EventState.PRE_EVENT});
    }

    public void doSomething() {
        Module2.someInt++;
    }

    @Override
    public void run() {/* Nothing to do */}

    @Override
    public void tour() {/* Nothing to do */}
}
