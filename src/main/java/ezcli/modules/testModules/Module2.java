package ezcli.modules.testModules;

import ezcli.modules.ezcli_core.EventState;
import ezcli.modules.ezcli_core.Module;

public class Module2 extends Module {

    public static int someInt = 48;

    public Module2() {
        super("Module2", EventState.PRE_EVENT);
        init(this, new String[]{"doSomethingElse"}, new String[] {"allkeys uarrow rarrow"});
    }

    public void doSomethingElse() {
        Module3.someStr += (char) someInt;
    }

    @Override
    public void run() {}

    @Override
    public void tour() {}
}
