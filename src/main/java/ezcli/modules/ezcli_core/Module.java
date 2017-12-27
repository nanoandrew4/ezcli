package ezcli.modules.ezcli_core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Abstract class specifying methods all modules should contain.
 * Each module must extend this class and implement the methods specified here.
 */
public abstract class Module {

    // List of modules that the program will use
    public static ArrayList<Module> modules = new ArrayList<>();

    // Hashmap that maps a string to a module, which when typed in interactive module, will run the mapped module
    protected static HashMap<String, Module> moduleMap = new HashMap<>();
    protected static HashMap<Module, String> moduleNameMap = new HashMap<>();

    protected static HashMap<String, LinkedList<Method>> eventMethods = new HashMap<>();

    // Module specific variables...

    public String moduleName;

    private String[] dependencies;

    protected EventState whenToRun;

    public Module(String moduleName, String... dependencies) {
        this.moduleName = moduleName;
        this.dependencies = dependencies;
    }

    public Module(String moduleName, EventState whenToRun, String... dependencies) {
        this.moduleName = moduleName;
        this.whenToRun = whenToRun;
        this.dependencies = dependencies;
    }

    /**
     * Return the preferred time to run for this module, so that the given
     * method uses the correct and up to date variables.
     *
     * @return Preferred time to run module.
     */
    public abstract EventState getWhenToRun();

    /**
     * Code to run for module. Should contain a while loop similar to that in the Interactive class,
     * in order to process input.
     */
    public abstract void run();

    /**
     * Code to run when touring program.
     */
    public abstract void tour();

    /**
     * Quasi-constructor. Initializes the class and adds the module to a list of modules.
     * Every module must call this method only once, and pass itself as a module if it wants to be accessible through
     * the Interactive module.
     *
     * @param module  Module to be added
     */
    protected void init(Module module, String keyToBind) {
        modules.add(module);
        moduleMap.put(keyToBind, module);
        moduleNameMap.put(module, keyToBind);
    }

    /**
     * Quasi-constructor. Adds methods to eventMethods, which are called when certain events occur in Terminal module.
     */
    protected void init(Method[] methods, String[] binds) {
        if (methods.length != binds.length) {
            System.err.println("Methods and Binds arrays are not equal in length " +
                    "(each method must be bound to something)");
            return;
        }

        for (int i = 0; i < methods.length; i++) {
            if ("all".equals(binds[i])) {
                for (int j = 30; j < 126; j++) {
                    LinkedList<Method> list = eventMethods.computeIfAbsent(String.valueOf(j), k -> new LinkedList<>());
                    list.add(methods[i]);
                }
                String[] keys = {"\n", "\b", "\t"};
                for (String s : keys) {
                    LinkedList<Method> list = eventMethods.computeIfAbsent(s, k -> new LinkedList<>());
                    list.add(methods[i]);
                }
            } else {
                for (int j = 0; j < binds[i].length(); j++) {
                    LinkedList<Method> list = eventMethods.computeIfAbsent(String.valueOf(binds[i].charAt(j)), k -> new LinkedList<>());
                    list.add(methods[i]);
                }
            }
        }
    }

    public static void processEvent(String val, EventState es) {
        LinkedList<Method> list = eventMethods.get(val);

        if (list == null)
            return;

        for (Method m : list) {
            try {
                EventState requestedES =
                        (EventState) m.getDeclaringClass().getDeclaredMethod("getWhenToRun").invoke(null);
                if (es == requestedES)
                    m.invoke(null);
            } catch (Exception e) {
                System.err.println("Error processing event");
                e.printStackTrace();
            }
        }
    }
}
