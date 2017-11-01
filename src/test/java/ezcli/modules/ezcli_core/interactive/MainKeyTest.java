package ezcli.modules.ezcli_core.interactive;

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
        Interactive interactive = new Interactive();
        MainInputProcessor inputProcessor = interactive.getInputProcessor();

        sleep();

        inputProcessor.process('a');
        assertEquals("a", inputProcessor.getCommand());
        interactive.parse(inputProcessor.getCommand()); sleep();

        inputProcessor.process('\t');
        assertEquals("", inputProcessor.getCommand());
        interactive.parse(inputProcessor.getCommand()); sleep();

        inputProcessor.process('\n');
        assertEquals("", inputProcessor.getCommand());
        interactive.parse(inputProcessor.getCommand()); sleep();

        inputProcessor.process((char)1);
        assertEquals("", inputProcessor.getCommand());
        interactive.parse(inputProcessor.getCommand()); sleep();

        inputProcessor.process((char) 127);
        assertEquals("", inputProcessor.getCommand());
        interactive.parse(inputProcessor.getCommand()); sleep();
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
