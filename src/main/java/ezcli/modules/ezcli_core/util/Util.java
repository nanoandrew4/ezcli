package ezcli.modules.ezcli_core.util;

import ezcli.modules.ezcli_core.modularity.EventState;
import ezcli.modules.ezcli_core.Ezcli;
import ezcli.modules.ezcli_core.modularity.Module;
import ezcli.modules.ezcli_core.global_io.Command;
import ezcli.modules.ezcli_core.global_io.handlers.KeyHandler;
import ezcli.modules.ezcli_core.global_io.input.Input;

import java.io.IOException;

public class Util {

    /**
     * Takes an interval of time in milliseconds, and returns amount of time it represents.
     * Useful for determining runtime of a program in a more readable format.
     *
     * @param interval milliseconds to convert to dd, hh, mm, ss, SSS format
     * @return time converted to readable format
     */
    public static String getRunTime(long interval) {
        long seconds = interval / 1000;
        String time = "";

        if (seconds / 86400 >= 1)
            time += String.valueOf(seconds / 86400) + " days, ";
        if ((seconds / 3600) >= 1)
            time += String.valueOf((seconds / 3600) % 24) + " hours, ";
        if ((seconds / 60) >= 1)
            time += String.valueOf((seconds / 60) % 60) + " minutes, ";

        time += String.valueOf(seconds % 60) + " seconds, ";
        time += String.valueOf(interval % 1000) + " millis";

        return time;
    }

    /**
     * Clears a line in the console of size line.length().
     *
     * @param line        line to be cleared
     * @param clearPrompt choose to clear prompt along with line (only use true if prompt exists)
     */
    public static void clearLine(String line, boolean clearPrompt) {
        Module.processEvent("clearln", EventState.PRE_EVENT);

        for (int i = 0; i < line.length() + (clearPrompt ? Ezcli.prompt.length() : 0); i++)
            System.out.print("\b");

        for (int i = 0; i < line.length() + (clearPrompt ? Ezcli.prompt.length() : 0); i++)
            System.out.print(" ");

        for (int i = 0; i < line.length() + (clearPrompt ? Ezcli.prompt.length() : 0); i++)
            System.out.print("\b");

        Module.processEvent("clearln", EventState.POST_EVENT);
    }

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
