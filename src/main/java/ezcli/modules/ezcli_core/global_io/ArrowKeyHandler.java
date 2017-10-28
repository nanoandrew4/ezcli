package ezcli.modules.ezcli_core.global_io;

public abstract class ArrowKeyHandler {

    public static int[] vals = new int[3]; // for use in detecting arrow presses on Unix
    public static int pos = 0; // for use in detecting arrow presses on Unix
    public static boolean resetArrowCheck = false; // for use in resetting detection of arrow presses on Unix
    // last arrow key that was pressed (if any other key is pressed sets to ArrowKeys.NONE)
    public static ArrowKeys lastArrowPress = ArrowKeys.NONE;

    /**
     * Checks if last input was arrow key (only on Windows).
     * <br></br>
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
     * <br></br>
     * For more information, please see comment block below.
     * <br></br>
     *
     * @param i integer value of last key press
     * @return arrow key pressed (or ArrowKeys.NONE if no arrow key was pressed)
     */
    /*
        When Unix processes arrow keys, they are read as a sequence of 3 numbers, for example 27 91 65
        which means Process is called once for each of the three numbers. The first number will be processed normally,
        which can not be prevented, but the other two run 1ms after the previous one, which means those can be filtered out.
        Even when holding down a key, the interval between each detection is 30ms +-1ms, which means this approach
        causes no problems at all. If char 27 is ignored, then the program will continue to run normally, at the cost of
        the escape character in Unix systems.
	*/
    public static ArrowKeys arrowKeyCheckUnix(int i) {

        if (resetArrowCheck) {
            vals = new int[3];
            resetArrowCheck = false;
            pos = 0;
        }

        vals[pos++ % 3] = i;

        if (vals[0] == 27 && vals[1] == 91 && !resetArrowCheck) {
            switch (vals[2]) {
                case 65:
                    resetArrowCheck = true;
                    return ArrowKeys.UP;
                case 66:
                    resetArrowCheck = true;
                    return ArrowKeys.DOWN;
                case 67:
                    resetArrowCheck = true;
                    return ArrowKeys.RIGHT;
                case 68:
                    resetArrowCheck = true;
                    return ArrowKeys.LEFT;
                default:
                    return ArrowKeys.NONE;
            }
        }

        return ArrowKeys.NONE;
    }

    public abstract ArrowKeys process(ArrowKeys ak);

}
