package ezcli.modules.ezcli_core.util;

import ezcli.modules.ezcli_core.Ezcli;
import ezcli.modules.smart_autocomplete.CommandFreq;

import java.util.ArrayList;

public class Util {

    /**
     * Takes an interval of time in milliseconds, and returns amount of time it represents.
     * Useful for determining runtime of a program in a more readable format.
     * <br></br>
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

        for (int i = 0; i < line.length() + (clearPrompt ? Ezcli.prompt.length() : 0); i++)
            System.out.print("\b");

        for (int i = 0; i < line.length() + (clearPrompt ? Ezcli.prompt.length() : 0); i++)
            System.out.print(" ");

        for (int i = 0; i < line.length() + (clearPrompt ? Ezcli.prompt.length() : 0); i++)
            System.out.print("\b");

    }

    /**
     * Sorts a CommandFreq list using quicksort.
     *
     * @param lPiv Leftmost chunk of list to sort
     * @param rPiv Rightmost chunk of list to sort
     */
    public static void sort(int lPiv, int rPiv, ArrayList<CommandFreq> list) {
        int cPiv = list.get((rPiv + lPiv) / 2).getFreq();
        int a = lPiv, b = rPiv;

        while (a <= b) {
            while (list.get(a).getFreq() > cPiv)
                a++;
            while (list.get(b).getFreq() < cPiv)
                b--;
            if (a <= b) {
                CommandFreq cfTmp = list.get(a);
                list.set(a, list.get(b));
                list.set(b, cfTmp);
                a++;
                b--;
            }
        }

        if (b < rPiv)
            sort(lPiv, b, list);
        if (a < rPiv)
            sort(a, rPiv, list);
    }
}
