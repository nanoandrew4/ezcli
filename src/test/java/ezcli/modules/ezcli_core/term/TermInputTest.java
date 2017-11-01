package ezcli.modules.ezcli_core.term;

import org.apache.commons.lang3.SystemUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import ezcli.modules.ezcli_core.global_io.ArrowKeys;

import java.io.OutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

public class TermInputTest {

    @BeforeClass
    public static void disableOutput() {
        System.setOut(new PrintStream(new OutputStream() {
            public void write(int b) {
                // no output
            }
        }));
    }

    /*
     * No testing can be done on file autocomplete since it will fail on different systems.
     * Changing this test will require modification of all other tests in this class.
     * Tests that key processing in the Terminal module is functional and robust.
     */
    @Test
    public void keyTest() {
        Terminal terminal = new Terminal();
        TermInputProcessor inputProcessor = terminal.getInputProcessor();
        TermKeyProcessor keyProcessor = inputProcessor.getKeyProcessor();

        keyProcessor.process('a');
        assertEquals("a", inputProcessor.getCommand());

        keyProcessor.process('b');
        assertEquals("ab", inputProcessor.getCommand());

        keyProcessor.process((char)3);
        assertEquals("ab", inputProcessor.getCommand());

        keyProcessor.process((char)1);
        assertEquals("ab", inputProcessor.getCommand());

        keyProcessor.newLineEvent(); // emulate newline
        terminal.parse(inputProcessor.getCommand());
        assertEquals("", inputProcessor.getCommand());

        keyProcessor.process('c');
        keyProcessor.process('d');
        keyProcessor.backspaceEvent(); // emulate backspace
        keyProcessor.process('e');

        assertEquals("ce", inputProcessor.getCommand());

        keyProcessor.newLineEvent(); // emulate newline
        terminal.parse(inputProcessor.getCommand());

        assertEquals(2, inputProcessor.getPrevCommands().size());
    }

    /*
     * Tests that arrow key processing in the Terminal module is functional and robust.
     * Current command should be "" in TermInputProcessor.
     * prevCommands list should hold 2 strings -> "ab" and "ce"
     */
    @Test
    public void arrowKeyTest() {
        Terminal terminal = new Terminal();
        TermInputProcessor inputProcessor = terminal.getInputProcessor();
        TermArrowKeyProcessor akProcessor = inputProcessor.getArrowKeyProcessor();

        inputProcessor.getPrevCommands().add("ab");
        inputProcessor.getPrevCommands().add("ce");
        inputProcessor.getArrowKeyProcessor().setCommandListPosition(2);

        akProcessor.process(ArrowKeys.NONE);
        assertEquals("", inputProcessor.getCommand());
        sleep();

        assertEquals(2, inputProcessor.getPrevCommands().size());

        akProcessor.process(ArrowKeys.UP);
        assertEquals("ce", inputProcessor.getCommand()); sleep();

        akProcessor.process(ArrowKeys.UP);
        assertEquals("ab", inputProcessor.getCommand()); sleep();

        akProcessor.process(ArrowKeys.DOWN);
        assertEquals("ce", inputProcessor.getCommand()); sleep();

        akProcessor.process(ArrowKeys.UP);
        assertEquals("ab", inputProcessor.getCommand()); sleep();

        akProcessor.process(ArrowKeys.NONE);
        assertEquals("ab", inputProcessor.getCommand()); sleep();

        akProcessor.process(ArrowKeys.LEFT);
        assertEquals("ab", inputProcessor.getCommand()); sleep();

        akProcessor.process(ArrowKeys.LEFT);
        assertEquals("ab", inputProcessor.getCommand()); sleep();

        akProcessor.process(ArrowKeys.RIGHT);
        assertEquals("ab", inputProcessor.getCommand()); sleep();

        akProcessor.process(ArrowKeys.DOWN);
        assertEquals("ce", inputProcessor.getCommand()); sleep();

        akProcessor.process(ArrowKeys.DOWN);
        assertEquals("", inputProcessor.getCommand()); sleep();

        inputProcessor.getPrevCommands().clear();
    }

    /*
     * Tests both key handling and arrow key handling for terminal module.
     * TODO: TEST TABS WITH MOCK FILE STRUCTURE
     */
    @Test
    public void combinedTest() {
        Terminal terminal = new Terminal();
        TermInputProcessor inputProcessor = terminal.getInputProcessor();

        // reset variables that might have been modified elsewhere needed for clean test
        inputProcessor.setCommand("");
        inputProcessor.getPrevCommands().clear();
        inputProcessor.getArrowKeyProcessor().setCommandListPosition(0);

        boolean isWin = SystemUtils.IS_OS_WINDOWS;
        sleep();

        inputProcessor.process('h'); sleep();
        inputProcessor.process('e'); sleep();
        inputProcessor.process('l'); sleep();
        inputProcessor.process('p'); sleep();
        inputProcessor.getKeyHandler().newLineEvent(); // simulate newline
        terminal.parse(inputProcessor.getCommand()); // parse command (so command will be cleared)

        assertEquals(1, inputProcessor.getPrevCommands().size());
        assertEquals("help", inputProcessor.getPrevCommands().get(0));
        assertEquals("", inputProcessor.getCommand());

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
        terminal.parse(inputProcessor.getCommand()); // parse command (so command will be cleared)

        assertEquals(2, inputProcessor.getPrevCommands().size());
        assertEquals("tess", inputProcessor.getPrevCommands().get(1));
        assertEquals("", inputProcessor.getCommand());

        if (isWin) {
            inputProcessor.process(57424); // down
            inputProcessor.process(57420); // nothing
            inputProcessor.process(57416); // up
            assertEquals("tess", inputProcessor.getCommand()); // moved up one, so last command input
            inputProcessor.process(57419); // left
            inputProcessor.process(574200); // nothing
            assertEquals("tess", inputProcessor.getCommand()); // no movement, so same as last time
            inputProcessor.process(57416); // up
            assertEquals("help", inputProcessor.getCommand()); // moved up one, so second last input
            inputProcessor.process(57416); // up
            assertEquals("help", inputProcessor.getCommand()); // moved up but at top of list, so same as before
            inputProcessor.process(57424); // down
            assertEquals("tess", inputProcessor.getCommand()); // moved down to last input
            inputProcessor.process(57424); // down
            inputProcessor.process(57424); // down
            inputProcessor.process(57424); // down
            assertEquals("", inputProcessor.getCommand()); // moved down past end of list, so print current input ("")
            inputProcessor.process(57416); // leaves command equaling "tess"
        } else { // assumes UNIX
            simUnixLeftArrow(inputProcessor); // left
            inputProcessor.process(2); // nothing
            simUnixUpArrow(inputProcessor); // up
            assertEquals("tess", inputProcessor.getCommand()); // moved up one, so last command input
            simUnixRightArrow(inputProcessor); // right
            inputProcessor.process(2000); // nothing
            assertEquals("tess", inputProcessor.getCommand()); // no movement, so same as last time
            simUnixUpArrow(inputProcessor); // up
            assertEquals("help", inputProcessor.getCommand()); // moved up but at top of list, so same as before
            simUnixDownArrow(inputProcessor); // down
            assertEquals("tess", inputProcessor.getCommand()); // moved down to last input
            simUnixDownArrow(inputProcessor); // down
            simUnixDownArrow(inputProcessor); // down
            simUnixDownArrow(inputProcessor); // down
            assertEquals("", inputProcessor.getCommand()); // moved down past end of list, so print current input ("")
            simUnixUpArrow(inputProcessor); // leaves command equaling "tess"
        }

        inputProcessor.getKeyHandler().backspaceEvent(); // simulate backspace
        inputProcessor.getKeyHandler().backspaceEvent(); // now command should equal "te"
        assertEquals("te", inputProcessor.getCommand());
        inputProcessor.getKeyHandler().newLineEvent(); // simulate newline
        terminal.parse(inputProcessor.getCommand()); // parse command (so command will be cleared)

        assertEquals(3, inputProcessor.getPrevCommands().size());
        assertEquals("te", inputProcessor.getPrevCommands().get(2));
        assertEquals("tess", inputProcessor.getPrevCommands().get(1));
        assertEquals("help", inputProcessor.getPrevCommands().get(0));
    }

    /*
     * This test tries to intentionally break all possible elements of the input system for the terminal module.
     */
    @Test
    public void attemptToBreak() {
        // reset variables that might have been modified elsewhere needed for clean test

        TermInputProcessor inputProcessor = new TermInputProcessor(new Terminal());
        TermKeyProcessor keyProcessor = inputProcessor.getKeyProcessor();
        inputProcessor.process(Integer.MAX_VALUE);
        inputProcessor.process(Integer.MIN_VALUE);
        inputProcessor.process(-1);
        for (int i = 0; i < 32; i++)
            inputProcessor.process(i);

        assertEquals("", inputProcessor.getCommand());
        String expectedCommand = "";
        for (int i = 48; i < 123; i++) {
            keyProcessor.process(i);
            expectedCommand += (char)i;
        }
        assertEquals(expectedCommand, inputProcessor.getCommand());

        for (int i = 0; i < 1000; i++)
            keyProcessor.backspaceEvent();
        for (int i = 0; i < 1050; i++)
            keyProcessor.newLineEvent();

        assertEquals("", inputProcessor.getCommand());
        assertEquals(0, inputProcessor.getPrevCommands().size());

        // test very long strings, check that nothing gets broken
        for (int i = 0; i < 10000; i++)
            keyProcessor.process(i % 73 + 50);
        assertFalse("".equals(inputProcessor.getCommand()));
        keyProcessor.newLineEvent();
        assertTrue(inputProcessor.getPrevCommands().get(0).length() == 10000);
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
