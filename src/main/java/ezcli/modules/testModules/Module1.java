package ezcli.modules.testModules;

import ezcli.modules.ezcli_core.EventState;
import ezcli.modules.ezcli_core.Module;

public class Module1 extends Module {

    public static char someChar = '\0';

    public Module1() {
        super("Module1", EventState.PRE_EVENT);
        init(this, new String[] {"doSomething"}, new String[]{"uarrow darrow"});
    }

    public void doSomething() {
        Module2.someInt++;
    }

    @Override
    public void run() {}

    @Override
    public void tour() {}
}
