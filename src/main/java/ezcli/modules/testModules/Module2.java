package ezcli.modules.testModules;

import ezcli.modules.ezcli_core.modularity.EventState;
import ezcli.modules.ezcli_core.modularity.Module;

public class Module2 extends Module {

    public static int someInt = 48;

    public Module2() {
        super("Module2");
        init(this, new String[] {"allkeys uarrow rarrow"}, new String[]{"doSomethingElse"},
                new EventState[] {EventState.PRE_EVENT});
    }

    public void doSomethingElse() {
        Module3.someStr += (char) someInt;
        System.out.println("incre");
    }

    @Override
    public void run() {}

    @Override
    public void tour() {}
}
