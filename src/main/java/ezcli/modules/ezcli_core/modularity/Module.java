package ezcli.modules.ezcli_core.modularity;

import ezcli.modules.ezcli_core.terminal.Terminal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Abstract class specifying methods all modules should contain, and some initialization ones.
 * Each module must extend this class and implement the methods specified here.
 *
 * <p> Two types of module exist:
 *
 * <p> One type is like the Terminal module, which is an independent entity
 * that handles its own input and does not rely on other modules to function or for data of any type.
 * That type of module should be initialized with the init(Module, String) method.
 *
 * <p> The other type of module that exists works on top of independent modules. It provides some extra
 * optional functionality, which is non essential and can be removed if desired. These modules are run
 * when certain events are detected. This type of module should be initialized by using the
 * init(Module, String[], String[], EventState[]) method.
 */
public abstract class Module {

    // Hashmap that maps modules to their class name (the class that extends Module)
    public static HashMap<String, Module> modules = new HashMap<>();

    // Hashmap that maps a string to a module, which when typed in interactive module, will run the mapped module
    public static HashMap<String, Module> moduleMap = new HashMap<>();
    public static HashMap<Module, String> moduleNameMap = new HashMap<>();

    // Contains methods to be run when events from TermKeyProcessor and TermArrowKeyProcessor are processed
    public static HashMap<String, LinkedList<ReactiveMethod>> eventMethods = new HashMap<>();

    // Module specific variables
    public String moduleName;
    public boolean currentlyActive = false;

    public Module(String moduleName) {
        this.moduleName = moduleName;
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
     * @param keyToBind Key to which the module is bound. Pressing this key in Interactive module will run the module
     */
    protected void init(Module module, String keyToBind) {
        modules.put(module.moduleName, module);
        moduleMap.put(keyToBind, module);
        moduleNameMap.put(module, keyToBind);
    }

    /**
     * Quasi-constructor. Adds methods to eventMethods, which are called when certain events occur,
     * such as key press events.
     *
     * @param module Module in which the methods are located
     * @param binds Sequences of characters or events to which the methods passed will respond
     * @param methodNames Names of methods to be bound
     * @param whenToRunEach See EventStates. Determines if method will run before or after the specified event
     */
    protected void init(Module module, String[] binds, String[] methodNames, EventState[] whenToRunEach) {
        modules.put(module.moduleName, module);

        if (methodNames.length != binds.length) {
            System.err.println("Methods and ReactiveMethod arrays have unequal size in module \""
                    + module.getClass().getSimpleName() + "\" (each method must be bound to something)");
            return;
        }

        ReactiveMethod[] reactiveMethods = new ReactiveMethod[methodNames.length];
        for (int i = 0; i < methodNames.length; i++) {
            try {
                reactiveMethods[i] =
                        new ReactiveMethod(module.getClass().getDeclaredMethod(methodNames[i]), whenToRunEach[i]);
            } catch (NoSuchMethodException e) {
                System.err.println("Error loading method \"" + methodNames[i] + "\" from module "
                        + module.getClass().getSimpleName());
            }
        }

        modules.put(module.moduleName, module);

        // Match key binds with methods passed in arrays
        for (int i = 0; i < reactiveMethods.length; i++) {
            if (reactiveMethods[i].method == null)
                continue;

            String[] bind = binds[i].split(" ");
            for (String b : bind) {
                b = b.trim();
                if (!bindToKeys(b, binds[i], reactiveMethods[i]))
                    bindToOther(b, reactiveMethods[i]);
            }
        }
    }

    /**
     * ReactiveMethod a sequence of characters to a method. If any character in the sequence
     * if processed later on, the passed method will be invoked.
     *
     * @param b Current request to bind
     * @param bind Potential sequence of characters to bind
     * @param m Method to be bound to sequence of characters
     * @return True if binding was attempted, false if no binding occurred
     */
    private boolean bindToKeys(String b, String bind, ReactiveMethod m) {
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
     * ReactiveMethod a string to a method. If the string is passed as an event later on,
     * the passed method will be invoked. For use primarily with keys that have no ASCII values, or
     * events that are non-key related.
     *
     * @param b String to bind to method
     * @param m Method to be bound to string
     */
    private void bindToOther(String b, ReactiveMethod m) {
        if ("allarrows".equals(b)) {
            String[] arrows = {"uarrow", "darrow", "larrow", "rarrow"};
            for (String s : arrows)
                addStringToHashmap(s, m);
        } else if (b.contains("arrow")){
            if ("uarrow".equals(b))
                addStringToHashmap(b, m);
            if ("darrow".equals(b))
                addStringToHashmap(b, m);
            if ("larrow".equals(b))
                addStringToHashmap(b, m);
            if ("rarrow".equals(b))
                addStringToHashmap(b, m);
        } else if ("clearln".equals(b)) {
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
    private void addCharsToHashmap(String s, ReactiveMethod m) {
        for (int i = 0; i < s.length(); i++) {
            LinkedList<ReactiveMethod> list =
                    eventMethods.computeIfAbsent(String.valueOf(s.charAt(i)), k -> new LinkedList<>());
            list.add(m);
        }
    }

    /**
     * Assigns string to method, for later invocation when the string is handled in processEvent()
     * <p>
     * Only for use of keys which do not have ASCII codes, such as arrow keys.
     * <p>
     * Currently only supports uarrow, darrow, larrow and rarrow for arrow keys, as well as clearln
     * for when the current line is deleted (see Util.clearLine()).
     *
     * @see ezcli.modules.ezcli_core.global_io.handlers.ArrowKeyHandler
     *
     * @param s String to map to method.
     * @param m Method to be mapped to string
     */
    private void addStringToHashmap(String s, ReactiveMethod m) {
        LinkedList<ReactiveMethod> list = eventMethods.computeIfAbsent(s, k -> new LinkedList<>());
        list.add(m);
    }

    /**
     * Searches eventMethods hashmap for list of modules to run based on event, and runs them.
     * For more info regarding the calling of these methods and the hardcoded events that are accepted,
     * see the mentioned classes.
     *
     * @see ezcli.modules.ezcli_core.global_io.handlers.KeyHandler
     * @see ezcli.modules.ezcli_core.global_io.handlers.ArrowKeyHandler
     *
     * @param event Event to process
     * @param es Stage at which the event is at (PRE or POST)
     */
    public static void processEvent(String event, EventState es) {
        LinkedList<ReactiveMethod> list = eventMethods.get(event);

        if (list == null)
            return;

        for (ReactiveMethod m : list) {
            try {
                EventState requestedES = m.whenToRun;
                if (es.equals(requestedES))
                    m.method.invoke(modules.get(m.method.getDeclaringClass().getSimpleName()));
            } catch (Exception e) {
                System.err.println("\nError processing event from: " + m.method.getDeclaringClass().getSimpleName());
                System.err.println("Error processing \"" + event + "\"");
            }
        }
    }

    @Override
    public String toString() {
        return moduleName;
    }
}
