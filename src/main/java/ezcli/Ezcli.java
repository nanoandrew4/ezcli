package ezcli;

import ezcli.modules.ezcli_core.term.Terminal;
import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;
import java.util.Scanner;

public class Ezcli {

    public static final String VERSION = "0.1.0";
    public static String prompt = "   \b\b\b>> ";

    public static String currDir = "./";

    public static boolean isWin = SystemUtils.IS_OS_WINDOWS;
    public static boolean isUnix = SystemUtils.IS_OS_UNIX || SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_FREE_BSD;

    public static void main(String[] args) {
        System.out.print(prompt);
        Terminal t = new Terminal();
        t.run();
    }
}
