package ezcli.modules.ezcli_core.global_io.handlers;

import ezcli.modules.ezcli_core.global_io.ArrowKeys;
import ezcli.modules.ezcli_core.global_io.handlers.events.Event;
import ezcli.modules.ezcli_core.modularity.EventState;
import ezcli.modules.ezcli_core.modularity.Module;

/**
 * Abstract class specifying how arrow keys should be handled.
 * Each module must run its own implementation of this class.
 */
public abstract class ArrowKeyHandler {

    // Last arrow key that was pressed (if any other key is pressed sets to ArrowKeys.NONE)
    protected static ArrowKeys lastArrowPress = ArrowKeys.NONE;

    private long lastPress = System.currentTimeMillis();

    public Event lArrEvent;
    public Event rArrEvent;
    public Event uArrEvent;
    public Event dArrEvent;

    /**
     * Checks if last input was arrow key (only on Windows).
     *
     * @param i integer value of last key press
     * @return arrow key pressed (or ArrowKeys.NONE if no arrow key was pressed)
     */
    public static ArrowKeys arrowKeyCheckWindows(int i) {
        switch (i) {
            case 57416:
                return ArrowKeys.UP;
            case 57424:
                return ArrowKeys.DOWN;
            case 57421:
                return ArrowKeys.RIGHT;
            case 57419:
                return ArrowKeys.LEFT;
            default:
                return ArrowKeys.NONE;
        }
    }

    /**
     * Checks if input was arrow key (only on Unix).
     * <br></br><br></br>
     * <p>
     * When Unix processes arrow keys, they are read as a sequence of 3 numbers, for example 27 91 65
     * which means Process is called once for each of the three numbers. The first number will be processed normally,
     * which can not be prevented, but the other two run 1ms after the previous one, which means those can be filtered out.
     * Even when holding down a key, the interval between each detection is 30ms +-1ms, which means this approach
     * causes no problems at all. If char 27 is ignored, then the program will continue to run normally, at the cost of
     * the escape character in Unix systems.
     *
     * @param i integer value of last key press
     * @return arrow key pressed (or ArrowKeys.NONE if no arrow key was pressed)
     */
    public static ArrowKeys arrowKeyCheckUnix(int... i) {

        if (i[0] == 27 && i[1] == 91) {
            switch (i[2]) {
                case 65:
                    return ArrowKeys.UP;
                case 66:
                    return ArrowKeys.DOWN;
                case 67:
                    return ArrowKeys.RIGHT;
                case 68:
                    return ArrowKeys.LEFT;
                default:
                    return ArrowKeys.NONE;
            }
        }

        return ArrowKeys.NONE;
    }

    /**
     * Process an arrow key press. If arrow key handling is simple enough,
     * can be used by itself to contain all the handling code.
     *
     * @param ak Arrow key to process
     * @return Arrow key that was processed
     */
    public ArrowKeys process(ArrowKeys ak) {

        if (ak != ArrowKeys.NONE && System.currentTimeMillis() - lastPress > InputHandler.minWaitTime) {
            lastPress = System.currentTimeMillis();
            switch (ak) {
                case UP:
                    Module.processEvent("uarrow", EventState.PRE_EVENT);
                    uArrEvent.process();
                    Module.processEvent("uarrow", EventState.POST_EVENT);
                    break;
                case DOWN:
                    Module.processEvent("darrow", EventState.PRE_EVENT);
                    dArrEvent.process();
                    Module.processEvent("darrow", EventState.POST_EVENT);
                    break;
                case LEFT:
                    Module.processEvent("larrow", EventState.PRE_EVENT);
                    lArrEvent.process();
                    Module.processEvent("larrow", EventState.POST_EVENT);
                    break;
                case RIGHT:
                    Module.processEvent("rarrow", EventState.PRE_EVENT);
                    rArrEvent.process();
                    Module.processEvent("rarrow", EventState.POST_EVENT);
                    break;
                default:
                    return ak; // Should never run
            }
        }

        return ak;
    }
}

