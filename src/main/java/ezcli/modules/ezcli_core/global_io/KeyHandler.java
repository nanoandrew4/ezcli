package ezcli.modules.ezcli_core.global_io;

import java.util.HashMap;

public abstract class KeyHandler {

    // stores all key integer values and maps them to the values in Keys enum
    private static HashMap<Integer, Keys> keymap = new HashMap<>();

    public void process(char input) {
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
        else if (Character.isDefined(input))
            charEvent(input, key);
    }

    /**
     * Loads all integer values of keys to HashMap.
     * <br></br>
     */
    public static void init() {
        keymap.put(127, Keys.BCKSP); // unix backspace
        keymap.put(8, Keys.BCKSP); // win backspace
        keymap.put((int) '\t', Keys.TAB); // unix tab
        keymap.put(9, Keys.TAB); // win tab
        keymap.put((int) '\n', Keys.NWLN); // unix newline
        keymap.put(13, Keys.NWLN); // win newline
    }

    /**
     * Returns associated key value from HashMap.
     * <br></br>
     *
     * @param i integer value of key pressed
     */
    public static Keys getKey(int i) {
        return keymap.get(i);
    }

    public abstract void tabEvent();

    public abstract void newLineEvent();

    public abstract void charEvent(char c, Keys key);

    public abstract void backspaceEvent();
}
