package ezcli.modules.ezcli_core.interactive;

import org.junit.Test;

import static org.junit.Assert.*;
import static ezcli.modules.ezcli_core.interactive.MainInputProcessor.getCommand;

public class MainKeyTest {

    @Test
    public void testInput() {
        Interactive interactive = new Interactive();
        MainInputProcessor inputProcessor = new MainInputProcessor();

        sleep();

        inputProcessor.process('a');
        assertEquals("a", getCommand());
        interactive.parse(getCommand());
        sleep();

        inputProcessor.process('\t');
        assertEquals("", getCommand());
        interactive.parse(getCommand());
        sleep();

        inputProcessor.process('\n');
        assertEquals("", getCommand());
        interactive.parse(getCommand());
        sleep();

        inputProcessor.process((char)1);
        assertEquals("", getCommand());
        interactive.parse(getCommand());
        sleep();

        inputProcessor.process((char) 127);
        assertEquals("", getCommand());
        interactive.parse(getCommand());
        sleep();
    }

    /*
     * Prevents Unix arrow key catching from messing with input
     */
    private void sleep() {
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
