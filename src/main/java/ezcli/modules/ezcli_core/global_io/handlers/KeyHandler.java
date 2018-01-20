package ezcli.modules.ezcli_core.global_io.handlers;

import ezcli.modules.ezcli_core.Ezcli;
import ezcli.modules.ezcli_core.global_io.Command;
import ezcli.modules.ezcli_core.global_io.Keys;
import ezcli.modules.ezcli_core.global_io.handlers.events.CharEvent;
import ezcli.modules.ezcli_core.global_io.handlers.events.Event;
import ezcli.modules.ezcli_core.global_io.input.Input;
import ezcli.modules.ezcli_core.modularity.EventState;
import ezcli.modules.ezcli_core.modularity.Module;

import java.util.HashMap;

/**
 * Abstract class specifying methods needed to process key events properly.
 * Each module must run its own implementation of this class.
 */
public abstract class KeyHandler {

    // Stores all key integer values and maps them to the values in Keys enum
    private static HashMap<Integer, Keys> keymap = new HashMap<>();

    public static HashMap<Integer, Keys> getKeymap() {
        return keymap;
    }

    private long lastPress = System.currentTimeMillis();

    public Event tabEvent;
    public Event newLineEvent;
    public CharEvent charEvent;
    public Event backspaceEvent;

    /**
     * Processes all input by relegating it to abstract methods.
     *
     * @param input ASCII key code representing key pressed
     */
    public void process(int input) {
        Keys key = getKey(input);

        if (System.currentTimeMillis() - lastPress < InputHandler.minWaitTime)
            return;
        lastPress = System.currentTimeMillis();

        Module.processEvent(String.valueOf((char) input), EventState.PRE_EVENT);

        if (key == Keys.BCKSP)
            backspaceEvent.process();
        else if (key == Keys.TAB)
            tabEvent.process();
        else if (key == Keys.NWLN)
            newLineEvent.process();
        else if (input > 31 && input < 127)
            charEvent.process((char) input);

        Module.processEvent(String.valueOf((char) input), EventState.POST_EVENT);

        signalCatch(input);
    }

    /**
     * Catches system signals, such as Ctrl+C.
     * Useful for running while waiting for some process to finish, so user can cancel if they wish.
     * For use in above use case, loop while process is not done and pass input.read(false) to this method.
     *
     * @param input ASCII key code value of key pressed
     * @return true if process should be cancelled, false if no signals were caught
     */
    public static Command signalCatch(int input) {
        if (input == 3) // Ctrl+C
            return Command.CTRLC;
        if (input == 26) { // Ctrl+Z -> force quit program (not really yet...)
            System.out.println();
            return Command.CTRLZ;
        }
        return Command.NONE;
    }

    /**
     * Loads all integer values of keys to HashMap.
     */
    public static void initKeysMap() {
        if (Ezcli.IS_WIN) {
            keymap.put(8, Keys.BCKSP);
            keymap.put(9, Keys.TAB);
            keymap.put(13, Keys.NWLN);
        } else if (Ezcli.IS_UNIX) {
            keymap.put(127, Keys.BCKSP);
            keymap.put((int) '\t', Keys.TAB);
            keymap.put((int) '\n', Keys.NWLN);
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
}

