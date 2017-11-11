package ezcli.modules.ezcli_core;

import ezcli.modules.ezcli_core.global_io.Command;
import ezcli.modules.ezcli_core.global_io.KeyHandler;
import ezcli.modules.ezcli_core.global_io.input.Input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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

    protected String moduleName;

    public Module(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * Quasi-constructor. Initializes the class and adds the module to a list of modules.
     * Every module must call this class only once, and pass itself as a module.
     *
     * @param module  Module to be added
     * @param mapWith String to map to module, which when typed in the interactive module will run this module
     */
    protected void init(Module module, String mapWith) {
        modules.add(module);
        moduleMap.put(mapWith, module);
        moduleNameMap.put(module, mapWith);
    }

    /**
     * Code to run for module. Should contain a while loop similar to that in the Interactive class,
     * in order to process input.
     */
    public abstract void run();

    /**
     * Code to run when parsing input.
     *
     * @param command Command to parse
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

    /**
     * Make program sleep but listen for signals such as SIGTERM and SIGKILL.
     *
     * @param s Number of seconds to sleep for
     * @return Signal to be handled
     */
    protected Command sleep(double s) {
        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() - start < s * 1000) {
            try {
                Command c = KeyHandler.signalCatch(Input.read(false));
                if (c != Command.NONE)
                    return c;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return Command.NONE;
    }

}
