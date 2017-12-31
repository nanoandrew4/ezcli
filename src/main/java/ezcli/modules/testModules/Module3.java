package ezcli.modules.testModules;

import ezcli.modules.ezcli_core.EventState;
import ezcli.modules.ezcli_core.Module;

public class Module3 extends Module {

    public static String someStr = "cba";

    public Module3() {
        super("Module3", EventState.PRE_EVENT);
        init(this, new String[] {"doSomeMore"}, new String[] {"abc"});
    }

    public void doSomeMore() {
        Module1.someChar = someStr.charAt(someStr.length() - 1);
    }

    @Override
    public void run() {}

    @Override
    public void tour() {}
}