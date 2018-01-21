package ezcli.modules.color_output;

import ezcli.modules.ezcli_core.Ezcli;
import ezcli.modules.ezcli_core.modularity.Module;
import ezcli.modules.ezcli_core.global_io.output.Output;

import java.util.HashMap;

public class ANSIColorOutput extends Module implements Output {

    // Hardcoded values used by default
    public final static String DEFAULT_COLOR = (char)27 + "[0m";
    public final static String PROMPT_COLOR = (char)27 + "[38;5;95m";
    public final static String CMD_COLOR = (char)27 + "[38;5;117m";
    public final static String CMD_SUGGESTION_COLOR = (char) 27 + "[38;5;100m";

    // Pairs key words to a color, for output in the methods below
    public static HashMap<String, String> textTypes = new HashMap<>();

    // For adding values to the hashmap, ideally do it inside the static body below to prevent coupling issues
    static {
        textTypes.put("prompt", PROMPT_COLOR);
        textTypes.put("command", CMD_COLOR);
        textTypes.put("file", (char)27 + "[38;5;160m");
        textTypes.put("suggestion", CMD_SUGGESTION_COLOR);
        textTypes.put("info", DEFAULT_COLOR);
    }

    public ANSIColorOutput() {
        super("ANSIColorOutput");
        if (Ezcli.IS_UNIX)
            Ezcli.ezcliOutput = this;
    }

    /**
     * Used to pair a key word with an ANSI color code, for use in outputting using this class.
     * Ideally any color code should be added directly in the static initializer of the class,
     * and not through use of this method.
     */
    @Deprecated
    public static void addTextType(String type, String color) {
        textTypes.put(type, color);
    }

    /**
     * Returns the ANSI code paired to the key word 'type'
     *
     * @param type String previously paired to a ANSI color code
     * @return ANSI color code paired to key word 'type'
     */
    public static String getANSI(String type) {
        return textTypes.get(type);
    }

    @Override
    public void print(char c, String type) {
        System.out.print(textTypes.get(type) + c + DEFAULT_COLOR);
    }

    @Override
    public void print(int i, String type) {
        System.out.print(textTypes.get(type) + i + DEFAULT_COLOR);
    }

    @Override
    public void print(boolean b, String type) {
        System.out.print(textTypes.get(type) + b + DEFAULT_COLOR);
    }

    @Override
    public void print(long l, String type) {
        System.out.print(textTypes.get(type) + l + DEFAULT_COLOR);
    }

    @Override
    public void print(float f, String type) {
        System.out.print(textTypes.get(type) + f + DEFAULT_COLOR);
    }

    @Override
    public void print(double d, String type) {
        System.out.print(textTypes.get(type) + d + DEFAULT_COLOR);
    }

    @Override
    public void print(char[] c, String type) {
        System.out.print(textTypes.get(type) + c + DEFAULT_COLOR);
    }

    @Override
    public void print(String s, String type) {
        System.out.print(textTypes.get(type) + s + DEFAULT_COLOR);
    }

    @Override
    public void println(char c, String type) {
        System.out.println(textTypes.get(type) + c + DEFAULT_COLOR);
    }

    @Override
    public void println(int i, String type) {
        System.out.println(textTypes.get(type) + i + DEFAULT_COLOR);
    }

    @Override
    public void println(boolean b, String type) {
        System.out.println(textTypes.get(type) + b + DEFAULT_COLOR);
    }

    @Override
    public void println(long l, String type) {
        System.out.println(textTypes.get(type) + l + DEFAULT_COLOR);
    }

    @Override
    public void println(float f, String type) {
        System.out.println(textTypes.get(type) + f + DEFAULT_COLOR);
    }

    @Override
    public void println(double d, String type) {
        System.out.println(textTypes.get(type) + d + DEFAULT_COLOR);
    }

    @Override
    public void println(char[] c, String type) {
        System.out.println(textTypes.get(type) + c + DEFAULT_COLOR);
    }

    @Override
    public void println(String s, String type) {
        System.out.println(textTypes.get(type) + s + DEFAULT_COLOR);
    }

    @Override
    public void println() {
        System.out.println();
    }

    @Override
    public void run() {/* Nothing to do */}

    @Override
    public void tour() {/* Nothing to do */}
}
