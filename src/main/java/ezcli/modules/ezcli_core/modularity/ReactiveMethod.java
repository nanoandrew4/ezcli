package ezcli.modules.ezcli_core.modularity;

import java.lang.reflect.Method;

public class ReactiveMethod {

    public final Method method;
    public final EventState whenToRun;

    ReactiveMethod(Method method, EventState whenToRun) {
        this.method = method;
        this.whenToRun = whenToRun;
    }
}
