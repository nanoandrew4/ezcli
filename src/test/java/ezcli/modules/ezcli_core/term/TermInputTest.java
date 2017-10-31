package ezcli.modules.ezcli_core.term;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;
import ezcli.modules.ezcli_core.global_io.ArrowKeys;

import static org.junit.Assert.*;
import static ezcli.modules.ezcli_core.term.TermInputProcessor.getCommand;

public class TermInputTest {

    /*
     * No testing can be done on file autocomplete since it will fail on different systems.
     * Changing this test will require modification of all other tests in this class.
     * Tests that key processing in the Terminal module is functional and robust.
     */
    @Test
    public void keyTest() {
        TermKeyProcessor inputProcessor = new TermKeyProcessor();
        Terminal t = new Terminal();

        inputProcessor.process('a');
        assertEquals("a", getCommand());

        inputProcessor.process('b');
        assertEquals("ab", getCommand());

        inputProcessor.process((char)3);
        assertEquals("ab", getCommand());

        inputProcessor.process((char)1);
        assertEquals("ab", getCommand());

        inputProcessor.newLineEvent(); // emulate newline
        t.parse(getCommand());
        assertEquals("", getCommand());

        inputProcessor.process('c');
        inputProcessor.process('d');
        inputProcessor.backspaceEvent(); // emulate backspace
        inputProcessor.process('e');

        assertEquals("ce", getCommand());

        inputProcessor.newLineEvent(); // emulate newline
        t.parse(getCommand());

        assertEquals(2, TermInputProcessor.getPrevCommands().size());
    }

    /*
     * Tests that arrow key processing in the Terminal module is functional and robust.
     * Current command should be "" in TermInputProcessor.
     * prevCommands list should hold 2 strings -> "ab" and "ce"
     */
    @Test
    public void arrowKeyTest() {
        TermArrowKeyProcessor akProcessor = new TermArrowKeyProcessor();

        TermInputProcessor.getPrevCommands().add("ab");
        TermInputProcessor.getPrevCommands().add("ce");
        TermArrowKeyProcessor.setCommandListPosition(2);

        akProcessor.process(ArrowKeys.NONE);
        assertEquals("", getCommand());
        sleep();

        assertEquals(2, TermInputProcessor.getPrevCommands().size());

        akProcessor.process(ArrowKeys.UP);
        assertEquals("ce", getCommand()); sleep();

        akProcessor.process(ArrowKeys.UP);
        assertEquals("ab", getCommand()); sleep();

        akProcessor.process(ArrowKeys.DOWN);
        assertEquals("ce", getCommand()); sleep();

        akProcessor.process(ArrowKeys.UP);
        assertEquals("ab", getCommand()); sleep();

        akProcessor.process(ArrowKeys.NONE);
        assertEquals("ab", getCommand()); sleep();

        akProcessor.process(ArrowKeys.LEFT);
        assertEquals("ab", getCommand()); sleep();

        akProcessor.process(ArrowKeys.LEFT);
        assertEquals("ab", getCommand()); sleep();

        akProcessor.process(ArrowKeys.RIGHT);
        assertEquals("ab", getCommand()); sleep();

        akProcessor.process(ArrowKeys.DOWN);
        assertEquals("ce", getCommand()); sleep();

        akProcessor.process(ArrowKeys.DOWN);
        assertEquals("", getCommand()); sleep();

        TermInputProcessor.getPrevCommands().clear();
    }

    /*
     * Tests both key handling and arrow key handling for terminal module.
     * TODO: TEST TABS WITH MOCK FILE STRUCTURE
     */
    @Test
    public void combinedTest() {
        Terminal terminal = new Terminal();
        TermInputProcessor inputProcessor = new TermInputProcessor();

        // reset variables that might have been modified elsewhere needed for clean test
        TermInputProcessor.setCommand("");
        TermInputProcessor.getPrevCommands().clear();
        TermArrowKeyProcessor.setCommandListPosition(0);

        boolean isWin = SystemUtils.IS_OS_WINDOWS;
        sleep();

        inputProcessor.process('h'); sleep();
        inputProcessor.process('e'); sleep();
        inputProcessor.process('l'); sleep();
        inputProcessor.process('p'); sleep();
        inputProcessor.getKeyHandler().newLineEvent(); // simulate newline
        terminal.parse(getCommand()); // parse command (so command will be cleared)

        assertEquals(1, TermInputProcessor.getPrevCommands().size());
        assertEquals("help", TermInputProcessor.getPrevCommands().get(0));
        assertEquals("", getCommand());

        inputProcessor.getKeyHandler().backspaceEvent(); sleep(); // simulate backspace
        inputProcessor.process('t'); sleep();
        inputProcessor.process('e'); sleep();
        inputProcessor.process('s'); sleep();
        inputProcessor.process('t'); sleep();
        inputProcessor.getKeyHandler().backspaceEvent(); sleep(); // simulate backspace
        inputProcessor.getKeyHandler().backspaceEvent(); sleep(); // simulate backspace
        inputProcessor.process('s'); sleep();
        inputProcessor.process('s'); sleep();
        inputProcessor.getKeyHandler().newLineEvent(); sleep(); // simulate newline
        terminal.parse(getCommand()); // parse command (so command will be cleared)

        assertEquals(2, TermInputProcessor.getPrevCommands().size());
        assertEquals("tess", TermInputProcessor.getPrevCommands().get(1));
        assertEquals("", getCommand());

        if (isWin) {
            inputProcessor.process(57424); // down
            inputProcessor.process(57420); // nothing
            inputProcessor.process(57416); // up
            assertEquals("tess", getCommand()); // moved up one, so last command input
            inputProcessor.process(57419); // left
            inputProcessor.process(574200); // nothing
            assertEquals("tess", getCommand()); // no movement, so same as last time
            inputProcessor.process(57416); // up
            assertEquals("help", getCommand()); // moved up one, so second last input
            inputProcessor.process(57416); // up
            assertEquals("help", getCommand()); // moved up but at top of list, so same as before
            inputProcessor.process(57424); // down
            assertEquals("tess", getCommand()); // moved down to last input
            inputProcessor.process(57424); // down
            inputProcessor.process(57424); // down
            inputProcessor.process(57424); // down
            assertEquals("", getCommand()); // moved down past end of list, so print current input ("")
            inputProcessor.process(57416); // leaves command equaling "tess"
        } else { // assumes UNIX
            simUnixLeftArrow(inputProcessor); // left
            inputProcessor.process(2); // nothing
            simUnixUpArrow(inputProcessor); // up
            assertEquals("tess", getCommand()); // moved up one, so last command input
            simUnixLeftArrow(inputProcessor); // left
            inputProcessor.process(2000); // nothing
            assertEquals("tess", getCommand()); // no movement, so same as last time
            simUnixUpArrow(inputProcessor); // up
            assertEquals("help", getCommand()); // moved up but at top of list, so same as before
            simUnixDownArrow(inputProcessor); // down
            assertEquals("tess", getCommand()); // moved down to last input
            simUnixDownArrow(inputProcessor); // down
            simUnixDownArrow(inputProcessor); // down
            simUnixDownArrow(inputProcessor); // down
            assertEquals("", getCommand()); // moved down past end of list, so print current input ("")
            simUnixUpArrow(inputProcessor); // leaves command equaling "tess"
        }

        inputProcessor.getKeyHandler().backspaceEvent(); // simulate backspace
        inputProcessor.getKeyHandler().backspaceEvent(); // now command should equal "te"
        assertEquals("te", getCommand());
        inputProcessor.getKeyHandler().newLineEvent(); // simulate newline
        terminal.parse(getCommand()); // parse command (so command will be cleared)

        assertEquals(3, TermInputProcessor.getPrevCommands().size());
        assertEquals("te", TermInputProcessor.getPrevCommands().get(2));
        assertEquals("tess", TermInputProcessor.getPrevCommands().get(1));
        assertEquals("help", TermInputProcessor.getPrevCommands().get(0));
    }

    /*
     * This test tries to intentionally break all possible elements of the input system for the terminal module.
     */
    @Test
    public void attemptToBreak() {
        // TODO
    }

    // simulates an up arrow key press for Unix systems
    private void simUnixUpArrow(TermInputProcessor t) {
        t.process(27);
        t.process(91);
        t.process(65);
    }

    // simulates a down arrow key press for Unix systems
    private void simUnixDownArrow(TermInputProcessor t) {
        t.process(27);
        t.process(91);
        t.process(66);
    }

    // simulates a left arrow key press for Unix systems
    private void simUnixLeftArrow(TermInputProcessor t) {
        t.process(27);
        t.process(91);
        t.process(68);
    }

    // simulates a right arrow key press for Unix systems
    private void simUnixRightArrow(TermInputProcessor t) {
        t.process(27);
        t.process(91);
        t.process(67);
    }

    /*
     * Causes program to sleep for 30ms in order to prevent bad tests on Unix (see ArrowKeyHandler + KeyHandler)
     */
    private void sleep() {
        try {
            Thread.sleep(11);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
