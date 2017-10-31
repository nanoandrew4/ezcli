package ezcli.modules.ezcli_core;

/**
 * Abstract class specifying methods all modules should contain.
 * Each module must extend this class and implement the methods specified here.
 */
public abstract class Module {

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
