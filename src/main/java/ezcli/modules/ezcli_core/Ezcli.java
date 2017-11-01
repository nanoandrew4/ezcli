package ezcli.modules.ezcli_core;

import ezcli.modules.ezcli_core.interactive.Interactive;
import ezcli.modules.ezcli_core.terminal.Terminal;

/**
 * Entry point for program.
 */
public class Ezcli {

    public static final String VERSION = "0.1.0";
    public static String prompt = "   \b\b\b>> ";

    public static String currDir = System.getProperty("user.dir");

    public static boolean isWin;
    public static boolean isUnix;

    public static void main(String[] args) {
        setOS();

        initModules();

        new Interactive().run(); // start program in interactive mode
    }

    /**
     * Sets OS global variables. Must be run before attempting to use any input, so input handlers know what to do.
     */
    public static void setOS() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("windows"))
            isWin = true;
        else if (os.equals("linux") || os.equals("mac") || os.equals("sunos") || os.equals("freebsd"))
            isUnix = true;
    }

    /**
     * Create modules to be used in program. Pass the string you want them to be mapped to in the Module hashmap.
     * When the string (for example "t" for terminal module) is detected in the Interactive module, the associated
     * module will run (using the previous example, that would be the terminal module).
     * <br></br><br></br>
     * For this implementation to work, the init() method in the Module class must be called in the constructor
     * of each module.
     */
    private static void initModules() {
        new Terminal("t");
    }
}
