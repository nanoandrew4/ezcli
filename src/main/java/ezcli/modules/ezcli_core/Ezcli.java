package ezcli.modules.ezcli_core;

import ezcli.modules.ezcli_core.global_io.Command;
import ezcli.modules.ezcli_core.global_io.handlers.KeyHandler;
import ezcli.modules.ezcli_core.global_io.input.Input;
import ezcli.modules.ezcli_core.global_io.output.ConsoleOutput;
import ezcli.modules.ezcli_core.global_io.output.Output;
import ezcli.modules.ezcli_core.interactive.Interactive;
import ezcli.modules.ezcli_core.modularity.Module;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Entry point for program. Takes care of all loading and initializing, so modules can get to work.
 */
public class Ezcli {

    public static final String VERSION = "0.4.0-WIP";

    public static String currDir = System.getProperty("user.dir") + "/";
    public final static String USER_HOME_DIR = System.getProperty("user.home") + "/";

    public static String prompt = ">> ";

    public static boolean IS_WIN;
    public static boolean IS_UNIX;

    // Toggle displaying output from various test packages
    public static boolean displayTestOutput = false;
    public static boolean displayTermTestOutput = false;
    public static boolean displayCoreTestOutput = false;
    public static boolean displaySmartAutocompleteOutput = false;
    public static boolean displayModularityTestOutput = false;

    // Copy of standard output stream so test classes can print if they need to
    public static PrintStream stdOutput = System.out;

    public static Output ezcliOutput;

    public static void main(String[] args) {
        setOS();

        if (IS_WIN) {
            stdOutput.println("Windows version is currently not available.");
            return;
        }

        Module.initModules("modules.txt");

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

        ezcliOutput = new ConsoleOutput();
    }

    /**
     * Make program sleep but listen for signals such as SIGTERM and SIGKILL.
     *
     * @param s Number of seconds to sleep for
     * @return Signal to be handled
     */
    public static Command sleep(double s) {
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
