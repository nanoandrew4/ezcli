package ezcli.modules.ezcli_core.global_io;

/**
 * Abstract class specifying how arrow keys should be handled.
 * Each module must run its own implementation of this class.
 */
public abstract class ArrowKeyHandler {

    // Stores the three values of the codes that Unix systems pass when an arrow key is pressed
    protected static int[] vals = new int[3];

    // Specifies which of the three codes used in processing arrow keys in Unix the program should be expecting
    protected static int pos = 0;

    // Last arrow key that was pressed (if any other key is pressed sets to ArrowKeys.NONE)
    protected static ArrowKeys lastArrowPress = ArrowKeys.NONE;

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
    /*

	*/
    public static ArrowKeys arrowKeyCheckUnix(int i) {

        if (vals[2] > 64 && vals[2] < 69) {
            // Reset array and position tracker if key was previously returned
            vals = new int[3];
            pos = 0;
        }

        vals[pos++ % 3] = i;

        if (vals[0] == 27 && vals[1] == 91) {
            switch (vals[2]) {
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
        } else if ((vals[0] != 27 && vals[0] != 0) || (i != vals[0] && i == 91 && vals[0] != 0)) {
            // Resets vals array if either of first two positions don't align with expected pattern
            vals = new int[3];
            pos = 0;
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
    public abstract ArrowKeys process(ArrowKeys ak);

    protected abstract void processUp();

    protected abstract void processDown();

    protected abstract void processLeft();

    protected abstract void processRight();
}
