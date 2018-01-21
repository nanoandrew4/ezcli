package ezcli.modules.ezcli_core.modularity;

import java.lang.reflect.Method;

/**
 * For non-independent modules, allows each method to be paired to a EventState at which it should run.
 *
 * @see Module
 */
public class ReactiveMethod {

    public final Method method;
    public final EventState whenToRun;

    ReactiveMethod(Method method, EventState whenToRun) {
        this.method = method;
        this.whenToRun = whenToRun;
    }
}
