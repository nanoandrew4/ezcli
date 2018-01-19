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
        if (!Ezcli.displayTestOutput || !Ezcli.displayCoreTestOutput)
            System.setOut(new PrintStream(new OutputStream() {
            public void write(int b) {
                // no output
            }
        }));
        else
            System.setOut(Ezcli.stdOutput);
    }

    @Test
    public void testInput() {
        Ezcli.setOS();
        Interactive interactive = new Interactive();
        MainInputProcessor inputProcessor = interactive.getInputProcessor();
        MainKeyProcessor keyProcessor = inputProcessor.getKeyProcessor();

        keyProcessor.process('a');
        assertEquals("a", inputProcessor.getWasCommand());
        interactive.parse(inputProcessor.getWasCommand());

        keyProcessor.process('\t');
        assertEquals("", inputProcessor.getWasCommand());
        interactive.parse(inputProcessor.getWasCommand());

        keyProcessor.process('\n');
        assertEquals("", inputProcessor.getWasCommand());
        interactive.parse(inputProcessor.getWasCommand());

        keyProcessor.process((char)1);
        assertEquals("", inputProcessor.getWasCommand());
        interactive.parse(inputProcessor.getWasCommand());

        keyProcessor.process((char) 127);
        assertEquals("", inputProcessor.getWasCommand());
        interactive.parse(inputProcessor.getWasCommand());
    }
}
