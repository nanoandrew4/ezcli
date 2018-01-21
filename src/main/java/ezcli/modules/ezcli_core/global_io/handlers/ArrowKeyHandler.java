package ezcli.modules.ezcli_core.global_io.handlers;

import ezcli.modules.ezcli_core.global_io.ArrowKeys;
import ezcli.modules.ezcli_core.global_io.handlers.events.Event;
import ezcli.modules.ezcli_core.modularity.EventState;
import ezcli.modules.ezcli_core.modularity.Module;

/**
 * Abstract class specifying how arrow keys should be handled.
 * Each module must implement its own set of Events.
 *
 * @see Event
 */
public abstract class ArrowKeyHandler {

    // Last arrow key that was pressed (if any other key is pressed sets to ArrowKeys.NONE)
    protected static ArrowKeys lastArrowPress = ArrowKeys.NONE;

    private long lastPress = System.currentTimeMillis();

    // Events to be implemented by any class that inherits ArrowKeyHandler
    public Event lArrEvent;
    public Event rArrEvent;
    public Event uArrEvent;
    public Event dArrEvent;

    /**
     * Checks if last input was arrow key (only on Windows).
     *
     * @param i Integer value of last key press
     * @return Arrow key pressed (or ArrowKeys.NONE if no arrow key was pressed)
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
     * <p>
     * When Unix processes arrow keys, they are read as a sequence of 3 numbers, for example 27 91 65
     * which means that the implementation of InputHandler owning the implementation of ArrowKeyHandler
     * must read 3 values, only blocking for the first. That way, if an arrow key is pressed, all three values are
     * caught, and if not, no input is lost, since the time for catching in non-blocking mode is ~1ms, and keyboard
     * presses are only detected every ~30ms interval.
     *
     * @param i Integer value of last key press
     * @return Arrow key pressed (or ArrowKeys.NONE if no arrow key was pressed)
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
     * Process an arrow key press.
     * <p>
     * Handles sending event status to all Modules, so they can react appropriately, as well
     * relegating to the appropriate lambda expression.
     *
     * @param ak Arrow key to process
     */
    public void process(ArrowKeys ak) {
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
            }
        }
    }
}

