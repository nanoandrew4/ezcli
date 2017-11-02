package ezcli.modules.ezcli_core;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Abstract class specifying methods all modules should contain.
 * Each module must extend this class and implement the methods specified here.
 */
public abstract class Module {

    // list of modules that the program wil use
    public static ArrayList<Module> modules = new ArrayList<>();

    // hashmap that maps a string to a module, which when typed in the interactive module, will run the module the string is mapped to
    public static HashMap<String, Module> moduleMap = new HashMap<>();

    /**
     * Quasi-constructor. Initializes the class and adds the module to a list of modules.
     * Every module must call this class only once, and pass itself as a module.
     * @param module module to be added
     * @param mapWith string to map to module, which when typed in the interactive module will run this module
     */
    protected void init(Module module, String mapWith) {
        modules.add(module);
        moduleMap.put(mapWith, module);
    }

    /**
     * Code to run for module. Should contain a while loop similar to that in the Interactive class,
     * in order to process input.
     */
    public abstract void run();

    /**
     * Code to run when parsing input.
     *
     * @param command command to parse
     */
    public abstract void parse(String command);

    /**
     * Code to run when displaying help for this module.
     */
    public abstract void help();

    /**
     * Code to run when touring program
     */
    public abstract void tour();

}
