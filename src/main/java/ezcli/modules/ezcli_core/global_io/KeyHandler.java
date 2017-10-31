package ezcli.modules.ezcli_core.global_io;

import ezcli.modules.ezcli_core.Ezcli;

import java.util.HashMap;

/**
 * Abstract class specifying methods needed to process key events properly.
 * Each module must run its own implementation of this class.
 */
public abstract class KeyHandler {

    // stores all key integer values and maps them to the values in Keys enum
    private static HashMap<Integer, Keys> keymap = new HashMap<>();

    public static HashMap<Integer, Keys> getKeymap() {
        return keymap;
    }

    /**
     * Processes all input by relegating it to abstract methods.
     *
     * @param input ASCII key code representing key pressed
     */
    public void process(int input) {
        Keys key = getKey(input);
        // Back Space
        if (key == Keys.BCKSP)
            backspaceEvent();

        // Tab
        else if (key == Keys.TAB)
            tabEvent();

        // Enter, or new line
        else if (key == Keys.NWLN)
            newLineEvent();

        // Character input
        else if (input > 31 && input < 127)
            charEvent((char)input);

        signalCatch(input);
    }

    /**
     * Catches system signals, such as Ctrl+C.
     * Useful for running while waiting for some process to finish, so user can cancel if they wish.
     * For use in above use case, loop while process is not done and pass Input.read(false) to this method.
     *
     * @param input ASCII key code value of key pressed
     * @return true if process should be cancelled, false if no signals were caught
     */
    public static boolean signalCatch(int input) {
        if (input == 3) // Ctrl+C
            return true;
        if (input == 26) { // Ctrl+Z -> force quit program
            System.out.println();
            System.exit(2);
        }
        return false;
    }

    /**
     * Loads all integer values of keys to HashMap.
     */
    public static void init() {
        if (Ezcli.isWin) {
            keymap.put(8, Keys.BCKSP); // win backspace
            keymap.put(9, Keys.TAB); // win tab
            keymap.put(13, Keys.NWLN); // win newline
        } else if (Ezcli.isUnix) {
            keymap.put(127, Keys.BCKSP); // unix backspace
            keymap.put((int) '\t', Keys.TAB); // unix tab
            keymap.put((int) '\n', Keys.NWLN); // unix newline
        }
    }

    /**
     * Returns associated key value from HashMap.
     *
     * @param i integer value of key pressed
     */
    protected static Keys getKey(int i) {
        return keymap.get(i);
    }

    /**
     * Code to run when tab key is pressed.
     */
    public abstract void tabEvent();

    /**
     * Code to run when enter key is pressed.
     */
    public abstract void newLineEvent();

    /**
     * Code to run when an ASCII value between 32 and 126 (all characters).
     *
     * @param input ASCII key code of key pressed
     */
    public abstract void charEvent(char input);

    /**
     * Code to run when backspace key is pressed.
     */
    public abstract void backspaceEvent();
}
