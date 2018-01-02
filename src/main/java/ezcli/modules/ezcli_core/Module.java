package ezcli.modules.ezcli_core;

import ezcli.modules.ezcli_core.terminal.Terminal;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Abstract class specifying methods all modules should contain, and some initialization ones.
 * Each module must extend this class and implement the methods specified here.
 *
 * <p>Two types of module exist:
 *
 * <p>One type is like the Terminal module, which is an independent entity
 * that handles its own input and does not rely on other modules to function or for data of any type.
 * That type of module should be initialized with the single parameter super class constructor, and the
 * single parameter super class init method (in that order).
 *
 * <p>The other type of module that exists works on top of independent modules. It provides some extra
 * optional functionality, which is non essential and can be removed if desired. These modules are run
 * when certain events are detected. This type of module should be initialized by using the double parameter
 * super class constructor and the triple parameter super class init method (in that order).
 */
public abstract class Module {

    // Hashmap that maps modules to their class name (the class that extends Module)
    public static HashMap<String, Module> modules = new HashMap<>();

    // Hashmap that maps a string to a module, which when typed in interactive module, will run the mapped module
    public static HashMap<String, Module> moduleMap = new HashMap<>();
    public static HashMap<Module, String> moduleNameMap = new HashMap<>();

    // Contains methods to be run when events from TermKeyProcessor and TermArrowKeyProcessor are processed
    public static HashMap<String, LinkedList<Method>> eventMethods = new HashMap<>();

    // Module specific variables...
    public String moduleName;
    private EventState whenToRun;
    public boolean currentlyActive = false;

    public Module(String moduleName) {
        this.moduleName = moduleName;
    }

    public Module(String moduleName, EventState whenToRun) {
        this.moduleName = moduleName;
        this.whenToRun = whenToRun;
    }

    public EventState getWhenToRun() {
        return whenToRun;
    }

    /**
     * Code to run for module. Should contain a while loop similar to that in the Interactive class,
     * in order to process input. Always ensure that currentlyActive is set to true when this method
     * is initially called, and to false when it exits.
     */
    public abstract void run();

    /**
     * Code to run when touring program.
     */
    public abstract void tour();

    /**
     * Initializes terminal module and all modules listed in moduleDeclaration file sequentially.
     * For comments or temporarily disabling a module, put a '#' at the beginning of the module declaration line.
     *
     * @param moduleDeclarations File containing list of modules to be loaded
     */
    public static void initModules(String moduleDeclarations) {

        new Terminal();

        List<String> modules;

        try {
            modules = Files.readAllLines(Paths.get(moduleDeclarations));
        } catch (IOException e) {
            System.err.println("Modules could not be loaded, file \"" + moduleDeclarations + "\" not found");
            return;
        }

        for (String s : modules) {
            if ("".equals(s.trim()) || s.contains("#"))
                continue;

            try {
                Class<?> module = Class.forName("ezcli.modules." + s.trim());
                module.getConstructor().newInstance();
            } catch (Exception e) {
                System.err.println("Error loading module: ezcli.modules." + s.trim());
                e.printStackTrace();
            }
        }
    }

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
    protected void init(Module module, String[] methodNames, String[] binds) {

        if (methodNames.length != binds.length) {
            System.err.println("Methods and Binds arrays have unequal size in module \""
                    + module.getClass().getSimpleName() + "\" (each method must be bound to something) ");
            return;
        }

        Method[] methods = new Method[methodNames.length];
        for (int i = 0; i < methodNames.length; i++) {
            try {
                methods[i] = module.getClass().getDeclaredMethod(methodNames[i]);
            } catch (NoSuchMethodException e) {
                System.err.println("Error loading method \"" + methodNames[i] + "\" from module "
                        + module.getClass().getSimpleName());
            }
        }

        modules.put(module.moduleName, module);

        // Match key binds with methods passed in arrays
        for (int i = 0; i < methods.length; i++) {
            if (methods[i] == null)
                continue;

            String[] bind = binds[i].split(" ");
            for (String b : bind) {
                b = b.trim();
                if (!bindToKeys(b, binds[i], methods[i]))
                    bindToArrowKeys(b, methods[i]);

            }
        }
    }

    /**
     * Binds a sequence of characters to a method. If any character in the sequence
     * if processed later on, the passed method will be invoked.
     *
     * @param b Current request to bind
     * @param bind Potential sequence of characters to bind
     * @param m Method to be bound to sequence of characters
     * @return True if binding was attempted, false if no binding occurred
     */
    private boolean bindToKeys(String b, String bind, Method m) {
        if ("allkeys".equals(b)) {
            for (int j = 30; j < 126; j++) // ascii
                addCharsToHashmap(String.valueOf((char) j), m);
            return true;

        } else if (!b.contains("arrow")) {
            addCharsToHashmap(bind, m);
            return true;
        }
        return false;
    }

    /**
     * Binds a string to a method. If the string is passed as an event later on,
     * the passed method will be invoked. For use primarily with keys that have no ASCII values.
     *
     * @param b String to bind to method
     * @param m Method to be bound to string
     */
    private void bindToArrowKeys(String b, Method m) {
        if ("allarrows".equals(b)) {
            String[] arrows = {"uarrow", "darrow", "larrow", "rarrow"};
            for (String s : arrows)
                addStringToHashmap(s, m);
        } else {
            if ("uarrow".equals(b))
                addStringToHashmap(b, m);
            if ("darrow".equals(b))
                addStringToHashmap(b, m);
            if ("larrow".equals(b))
                addStringToHashmap(b, m);
            if ("rarrow".equals(b))
                addStringToHashmap(b, m);
        }
    }

    /**
     * Breaks string into individual characters, and assigns each one the passed method, for later invocation when
     * the relevant characters are handled.
     * <p>
     * Only for use with ASCII characters, non ASCII characters should be added with addStringToHashmap()
     *
     * @param s String of characters to assign to method
     * @param m Method to be assigned to sequence of characters
     */
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

    /**
     * Assigns string to method, for later invocation when the string is handled in processEvent()
     * <p>
     * Only for use of keys which do not have ASCII codes, such as arrow keys.
     * <p>
     * Currently only supports uarrow, darrow, larrow and rarrow for arrow keys.
     *
     * @see ezcli.modules.ezcli_core.terminal.TermArrowKeyProcessor
     *
     * @param s String to map to method.
     * @param m Method to be mapped to string
     */
    private void addStringToHashmap(String s, Method m) {
        LinkedList<Method> list = eventMethods.get(s);

        if (list == null) {
            list = new LinkedList<>();
            eventMethods.put(s, list);
        }

        list.add(m);
    }

    /**
     * Searches eventMethods hashmap for list of modules to run based on event, and runs them.
     * For more info regarding the calling of these methods and the hardcoded events that are accepted,
     * see the mentioned classes.
     *
     * @see ezcli.modules.ezcli_core.terminal.TermKeyProcessor
     * @see ezcli.modules.ezcli_core.terminal.TermArrowKeyProcessor
     *
     * @param event Event to process
     * @param es Required stage to process at (currently either before or after the input processors have updated)
     */
    public static void processEvent(String event, EventState es) {
        LinkedList<Method> list = eventMethods.get(event);

        if (list == null)
            return;

        for (Method m : list) {
            try {
                EventState requestedES =
                        (EventState) m.getDeclaringClass().getSuperclass().getDeclaredMethod("getWhenToRun")
                                .invoke(modules.get(m.getDeclaringClass().getSimpleName()));
                if (es.equals(requestedES))
                    m.invoke(modules.get(m.getDeclaringClass().getSimpleName()));

            } catch (Exception e) {
                System.err.println("Error processing event from class: " + m.getDeclaringClass().getSimpleName());
                System.out.println("Error processing \"" + event + "\"");
                e.printStackTrace();
            }
        }
    }
}
