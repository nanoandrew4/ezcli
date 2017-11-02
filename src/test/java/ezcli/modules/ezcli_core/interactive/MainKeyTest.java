package ezcli.modules.ezcli_core.interactive;

import ezcli.modules.ezcli_core.Ezcli;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.OutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

public class MainKeyTest {

    @BeforeClass
    public static void disableOutput() {
        System.setOut(new PrintStream(new OutputStream() {
            public void write(int b) {
                // no output
            }
        }));
    }

    @Test
    public void testInput() {
        Ezcli.setOS();
        Interactive interactive = new Interactive();
        MainInputProcessor inputProcessor = interactive.getInputProcessor();

        sleep();

        inputProcessor.process('a');
        assertEquals("a", inputProcessor.getWasCommand());
        interactive.parse(inputProcessor.getWasCommand()); sleep();

        inputProcessor.process('\t');
        assertEquals("", inputProcessor.getWasCommand());
        interactive.parse(inputProcessor.getWasCommand()); sleep();

        inputProcessor.process('\n');
        assertEquals("", inputProcessor.getWasCommand());
        interactive.parse(inputProcessor.getWasCommand()); sleep();

        inputProcessor.process((char)1);
        assertEquals("", inputProcessor.getWasCommand());
        interactive.parse(inputProcessor.getWasCommand()); sleep();

        inputProcessor.process((char) 127);
        assertEquals("", inputProcessor.getWasCommand());
        interactive.parse(inputProcessor.getWasCommand()); sleep();
    }

    /*
     * Prevents Unix arrow key catching from messing with input
     */
    private void sleep() {
        try {
            Thread.sleep(11);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
