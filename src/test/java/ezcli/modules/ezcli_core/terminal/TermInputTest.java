package ezcli.modules.ezcli_core.terminal;

import ezcli.modules.ezcli_core.Ezcli;
import org.junit.BeforeClass;
import org.junit.Test;
import ezcli.modules.ezcli_core.global_io.ArrowKeys;

import java.io.OutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

public class TermInputTest {

    @BeforeClass
    public static void disableOutput() {
        if (!Ezcli.testOutput || !Ezcli.testTermOuput)
            System.setOut(new PrintStream(new OutputStream() {
                public void write(int b) {
                    // no output
                }
            }));
        else
            System.setOut(Ezcli.stdOutput);
    }

    @Test
    public void disassembleCommandTest() {
        Ezcli.setOS();

        assertEquals("command", TermInputProcessor.disassembleCommand("command", 0)[1]);

        assertEquals("another", TermInputProcessor.disassembleCommand("command && anothercommand", 18)[1]);

        assertEquals("d", TermInputProcessor.disassembleCommand("a && b && c && d", 16)[1]);

        assertEquals("d", TermInputProcessor.disassembleCommand("a && b && c && d && e", 16)[1]);

        assertEquals("d", TermInputProcessor.disassembleCommand("a && b && c &&d && e", 15)[1]);

        String[] cmd = TermInputProcessor.disassembleCommand("/home/username && /etc/", 14);
        assertEquals("/home/username && /etc/", cmd[0] + cmd[1] + cmd[2]);
        assertEquals("", cmd[0]);
        assertEquals("/home/username", cmd[1]);
        assertEquals(" && /etc/", cmd[2]);

        cmd = TermInputProcessor.disassembleCommand("/home/username && /etc/", 16);
        assertEquals("/home/username && /etc/", cmd[0] + cmd[1] + cmd[2]);
        assertEquals("", cmd[0]);
        assertEquals("/home/username && /etc/", cmd[1]);
        assertEquals("", cmd[2]);

        cmd = TermInputProcessor.disassembleCommand("/home/username && /etc/", 15);
        assertEquals("/home/username && /etc/", cmd[0] + cmd[1] + cmd[2]);
        assertEquals("/home/username ", cmd[0]);
        assertEquals("", cmd[1]);
        assertEquals("&& /etc/", cmd[2]);

        cmd = TermInputProcessor.disassembleCommand("cd / && cd /home", 16);
        assertEquals("cd / && cd /home", cmd[0] + cmd[1] + cmd[2]);
        assertEquals("cd / && ", cmd[0]);
        assertEquals("cd /home", cmd[1]);
        assertEquals("", cmd[2]);
    }

    /*
     * No testing can be done on file autocomplete since it will fail on different systems.
     * Changing this test will require modification of all other tests in this class.
     * Tests that key processing in the Terminal module is functional and robust.
     */
    @Test
    public void keyTest() {
        Ezcli.setOS();
        Terminal terminal = new Terminal();
        TermInputProcessor inputProcessor = terminal.inputProcessor;
        TermKeyProcessor keyProcessor = inputProcessor.getKeyProcessor();

        keyProcessor.process('a');
        assertEquals("a", inputProcessor.getCommand());

        keyProcessor.process('b');
        assertEquals("ab", inputProcessor.getCommand());

        keyProcessor.process((char) 3);
        assertEquals("ab", inputProcessor.getCommand());

        keyProcessor.process((char) 1);
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
        TermInputProcessor inputProcessor = terminal.inputProcessor;
        TermArrowKeyProcessor akProcessor = inputProcessor.getArrowKeyProcessor();

        inputProcessor.getPrevCommands().add("ab");
        inputProcessor.getPrevCommands().add("ce");
        inputProcessor.getArrowKeyProcessor().setCommandListPosition(2);

        akProcessor.process(ArrowKeys.NONE);
        assertEquals("", inputProcessor.getCommand());


        assertEquals(2, inputProcessor.getPrevCommands().size());

        akProcessor.process(ArrowKeys.UP);
        assertEquals("ce", inputProcessor.getCommand());

        akProcessor.process(ArrowKeys.UP);
        assertEquals("ab", inputProcessor.getCommand());

        akProcessor.process(ArrowKeys.DOWN);
        assertEquals("ce", inputProcessor.getCommand());

        akProcessor.process(ArrowKeys.UP);
        assertEquals("ab", inputProcessor.getCommand());

        akProcessor.process(ArrowKeys.NONE);
        assertEquals("ab", inputProcessor.getCommand());

        akProcessor.process(ArrowKeys.LEFT);
        assertEquals("ab", inputProcessor.getCommand());

        akProcessor.process(ArrowKeys.LEFT);
        assertEquals("ab", inputProcessor.getCommand());

        akProcessor.process(ArrowKeys.RIGHT);
        assertEquals("ab", inputProcessor.getCommand());

        akProcessor.process(ArrowKeys.DOWN);
        assertEquals("ce", inputProcessor.getCommand());

        inputProcessor.getArrowKeyProcessor().processLeft();
        inputProcessor.getKeyProcessor().backspaceEvent();
        inputProcessor.getKeyProcessor().process('a');
        assertEquals("ae", inputProcessor.getCommand());

        inputProcessor.getArrowKeyProcessor().processLeft();
        inputProcessor.getArrowKeyProcessor().processLeft();
        inputProcessor.getKeyProcessor().process('z');
        assertEquals("zae", inputProcessor.getCommand());
        inputProcessor.getArrowKeyProcessor().processRight();
        inputProcessor.getArrowKeyProcessor().processRight();
        inputProcessor.getKeyProcessor().backspaceEvent();
        inputProcessor.getKeyProcessor().backspaceEvent();
        assertEquals("z", inputProcessor.getCommand());

        inputProcessor.getPrevCommands().clear();
    }

    /*
     * Tests both key handling and arrow key handling for terminal module.
     */
    @Test
    public void combinedTest() {

        System.out.println("entered");

        Terminal terminal = new Terminal();
        TermInputProcessor inputProcessor = terminal.inputProcessor;

        // reset variables that might have been modified elsewhere needed for clean test
        inputProcessor.setCommand("");
        inputProcessor.getPrevCommands().clear();
        inputProcessor.getArrowKeyProcessor().setCommandListPosition(0);

        inputProcessor.getKeyProcessor().process('h');
        inputProcessor.getKeyProcessor().process('e');
        inputProcessor.getKeyProcessor().process('l');
        inputProcessor.getKeyProcessor().process('p');
        inputProcessor.getKeyProcessor().newLineEvent(); // simulate newline

        assertEquals(1, inputProcessor.getPrevCommands().size());
        assertEquals("help", inputProcessor.getPrevCommands().get(0));
        assertEquals("", inputProcessor.getCommand());

        inputProcessor.getKeyProcessor().backspaceEvent();  // simulate backspace
        inputProcessor.getKeyProcessor().process('t');
        inputProcessor.getKeyProcessor().process('e');
        inputProcessor.getKeyProcessor().process('s');
        inputProcessor.getKeyProcessor().process('t');
        inputProcessor.getKeyProcessor().backspaceEvent();  // simulate backspace
        inputProcessor.getKeyProcessor().backspaceEvent();  // simulate backspace
        inputProcessor.getKeyProcessor().process('s');
        inputProcessor.getKeyProcessor().process('s');
        inputProcessor.getKeyProcessor().newLineEvent();  // simulate newline

        assertEquals(2, inputProcessor.getPrevCommands().size());
        assertEquals("tess", inputProcessor.getPrevCommands().get(1));
        assertEquals("", inputProcessor.getCommand());

        inputProcessor.getArrowKeyProcessor().processDown();
        inputProcessor.getArrowKeyProcessor().processUp();
        assertEquals("tess", inputProcessor.getCommand()); // moved up one, so last command input
        inputProcessor.getArrowKeyProcessor().processLeft();
        assertEquals("tess", inputProcessor.getCommand()); // no movement, so same as last time
        inputProcessor.getArrowKeyProcessor().processUp();
        assertEquals("help", inputProcessor.getCommand()); // moved up one, so second last input
        inputProcessor.getArrowKeyProcessor().processUp();
        assertEquals("help", inputProcessor.getCommand()); // moved up but at top of list, so same as before
        inputProcessor.getArrowKeyProcessor().processDown();
        assertEquals("tess", inputProcessor.getCommand()); // moved down to last input
        inputProcessor.getArrowKeyProcessor().processDown();
        inputProcessor.getArrowKeyProcessor().processDown();
        inputProcessor.getArrowKeyProcessor().processDown();
        assertEquals("", inputProcessor.getCommand()); // moved down past end of list, so print current input ("")
        inputProcessor.getArrowKeyProcessor().processUp(); // leaves command equaling "tess"

        inputProcessor.getArrowKeyProcessor().processLeft(); // move cursor one character to the left
        inputProcessor.getKeyProcessor().backspaceEvent(); // simulate backspace
        inputProcessor.getKeyProcessor().backspaceEvent(); // now command should equal "te"
        assertEquals("ts", inputProcessor.getCommand());
        inputProcessor.getKeyProcessor().newLineEvent(); // simulate newline

        assertEquals(3, inputProcessor.getPrevCommands().size());
        assertEquals("ts", inputProcessor.getPrevCommands().get(2));
        assertEquals("tess", inputProcessor.getPrevCommands().get(1));
        assertEquals("help", inputProcessor.getPrevCommands().get(0));
    }

    /*
     * This test tries to intentionally break all possible elements of the input system for the terminal module.
     */
    @Test
    public void attemptToBreak() {
        System.out.println("entered");
        // reset variables that might have been modified elsewhere needed for clean test
        Ezcli.setOS();
        TermInputProcessor inputProcessor = new TermInputProcessor(new Terminal());
        TermKeyProcessor keyProcessor = inputProcessor.getKeyProcessor();
        keyProcessor.process(Integer.MAX_VALUE);
        keyProcessor.process(Integer.MIN_VALUE);
        keyProcessor.process(-1);

        for (int i = 0; i < 32; i++)
            keyProcessor.process(i);

        assertEquals("", inputProcessor.getCommand());
        StringBuilder expectedCommand = new StringBuilder();
        for (int i = 48; i < 123; i++) {
            keyProcessor.process(i);
            expectedCommand.append((char) i);
        }
        assertEquals(expectedCommand.toString(), inputProcessor.getCommand());

        for (int i = 0; i < 1000; i++)
            keyProcessor.backspaceEvent();
        for (int i = 0; i < 1050; i++)
            keyProcessor.newLineEvent();
        assertEquals("", inputProcessor.getCommand());
        assertEquals(0, inputProcessor.getPrevCommands().size());

        for (int i = 0; i < 1000; i++)
            keyProcessor.process(i % 73 + 48);

        assertFalse("".equals(inputProcessor.getCommand()));
        keyProcessor.newLineEvent();
        assertEquals(1000, inputProcessor.getPrevCommands().get(0).length());
    }
}
