package ezcli.modules.testModules;

import ezcli.modules.ezcli_core.modularity.EventState;
import ezcli.modules.ezcli_core.modularity.Module;

public class Module3 extends Module {

    public static String someStr = "cba";

    public Module3() {
        super("Module3");
        init(this, new String[] {"abc"}, new String[] {"doSomeMore"}, new EventState[] {EventState.PRE_EVENT});
    }

    public void doSomeMore() {
        Module1.someChar = someStr.charAt(someStr.length() - 1);
    }

    @Override
    public void run() {}

    @Override
    public void tour() {}
}