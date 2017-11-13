package ezcli.modules.ezcli_core;

import ezcli.modules.ezcli_core.interactive.Interactive;
import ezcli.modules.terminal.Terminal;

/**
 * Entry point for program. Takes care of all loading and initializing, so modules can get to work.
 */
public class Ezcli {

    public static final String VERSION = "0.1.0";
    public static String prompt = "   \b\b\b>> ";

    public static String currDir = System.getProperty("user.dir") + "/";
    public final static String USER_HOME_DIR = System.getProperty("user.home") + "/";

    public static boolean IS_WIN;
    public static boolean IS_UNIX;

    public static boolean testOutput = false;

    public static void main(String[] args) {
        setOS();

        initModules();

        new Interactive().run();
    }

    /**
     * Sets OS global variables. Must be run before attempting to use any input, so input handlers know what to do.
     */
    public static void setOS() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("windows"))
            IS_WIN = true;
        else if ("linux".equals(os) || os.contains("mac") || "sunos".equals(os) || "freebsd".equals(os))
            IS_UNIX = true;
    }

    /**
     * Create modules to be used in program. Pass the string you want them to be mapped to in the Module hashmap.
     * <p>
     * When the string (for example "t" for terminal module) is detected in the Interactive module, the associated
     * module will run (using the previous example, that would be the terminal module).
     * <p>
     * For this implementation to work, the init() method in the Module class must be called in the constructor
     * of each module.
     */
    private static void initModules() {
        new Terminal("t");
    }
}
