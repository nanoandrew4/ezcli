package ezcli.modules.ezcli_core;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Abstract class specifying methods all modules should contain.
 * Each module must extend this class and implement the methods specified here.
 */
public abstract class Module {

    // List of modules that the program will use
    public static HashMap<String, Module> modules = new HashMap<>();

    // Hashmap that maps a string to a module, which when typed in interactive module, will run the mapped module
    protected static HashMap<String, Module> moduleMap = new HashMap<>();
    protected static HashMap<Module, String> moduleNameMap = new HashMap<>();

    protected static HashMap<String, LinkedList<Method>> eventMethods = new HashMap<>();

    // Module specific variables...

    public String moduleName;

    protected EventState whenToRun;

    public Module(String moduleName) {
        this.moduleName = moduleName;
    }

    public Module(String moduleName, EventState whenToRun) {
        this.moduleName = moduleName;
        this.whenToRun = whenToRun;
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
     * @param module Module to be added
     */
    protected void init(Module module, String keyToBind) {
        modules.put(module.moduleName, module);
        moduleMap.put(keyToBind, module);
        moduleNameMap.put(module, keyToBind);
    }

    /**
     * Quasi-constructor. Adds methods to eventMethods, which are called when certain events occur in Terminal module,
     * such as key press events.
     */
    protected void init(Module module, Method[] methods, String[] binds) {

        if (methods.length != binds.length) {
            System.err.println("Methods and Binds arrays are not equal in length " +
                    "(each method must be bound to something)");
            return;
        }

        modules.put(module.moduleName, module);

        for (int i = 0; i < methods.length; i++) {
            String[] bind = binds[i].split(" ");
            for (String b : bind) {
                if ("allkeys".equals(b)) {
                    for (int j = 30; j < 126; j++)
                        addCharsToHashmap(String.valueOf((char) j), methods[i]);

                    String[] keys = {"\n", "\b", "\t"};
                    for (String s : keys)
                        addCharsToHashmap(s, methods[i]);

                } else if (!b.contains("arrow")) {
                    addCharsToHashmap(binds[i], methods[i]);
                    continue;
                }

                if ("allarrows".equals(b)) {
                    String[] arrows = {"uarrow", "darrow", "larrow", "rarrow"};
                    for (String s : arrows)
                        addStringToHashmap(s, methods[i]);
                } else {
                    if ("uarrow".equals(b))
                        addStringToHashmap(b, methods[i]);
                    if ("darrow".equals(b))
                        addStringToHashmap(b, methods[i]);
                    if ("larrow".equals(b))
                        addStringToHashmap(b, methods[i]);
                    if ("rarrow".equals(b))
                        addStringToHashmap(b, methods[i]);
                }
            }
        }
    }

    private void addCharsToHashmap(String s, Method m) {
        for (int i = 0; i < s.length(); i++) {
            LinkedList<Method> list = eventMethods.get(String.valueOf(s.charAt(i)));

            if (list == null) {
                list = new LinkedList<>();
                eventMethods.put(String.valueOf(s.charAt(i)), list);
            }

            list.add(m);
        }
    }

    private void addStringToHashmap(String s, Method m) {
        LinkedList<Method> list = eventMethods.get(s);

        if (list == null) {
            list = new LinkedList<>();
            eventMethods.put(s, list);
        }

        list.add(m);
    }

    public static void processEvent(String val, EventState es) {
        LinkedList<Method> list = eventMethods.get(val);

        if (list == null)
            return;

        for (Method m : list) {
            try {
                System.out.println(m.getDeclaringClass().getSimpleName());
                EventState requestedES = (EventState) m.getDeclaringClass().getDeclaredMethod("getWhenToRun")
                        .invoke(modules.get(m.getDeclaringClass().getSimpleName()));
                if (es == requestedES)
                    m.invoke(modules.get(m.getDeclaringClass().getSimpleName()));
            } catch (Exception e) {
                System.err.println("Error processing event from class: " + m.getDeclaringClass().getSimpleName());
                System.out.println("Error processing \"" + val + "\"");
                e.printStackTrace();
            }
        }
    }
}
