package ezcli.modules.ezcli_core.global_io;

import org.junit.Test;
import static org.junit.Assert.*;

public class ArrowKeyTest {

    @Test
    public void testWinArrowHandling() {
        /*
         * Test that processing of arrow keys on Windows systems work
         */
        assertEquals(ArrowKeys.UP, ArrowKeyHandler.arrowKeyCheckWindows(57416));
        assertEquals(ArrowKeys.DOWN, ArrowKeyHandler.arrowKeyCheckWindows(57424));
        assertEquals(ArrowKeys.LEFT, ArrowKeyHandler.arrowKeyCheckWindows(57419));
        assertEquals(ArrowKeys.RIGHT, ArrowKeyHandler.arrowKeyCheckWindows(57421));
        assertEquals(ArrowKeys.NONE, ArrowKeyHandler.arrowKeyCheckWindows(0));
        assertEquals(ArrowKeys.NONE, ArrowKeyHandler.arrowKeyCheckWindows(10000));
    }

    @Test
    public void testUnixArrowHandling() {
        /*
         * Test that processing of arrow keys on Unix systems work
         */

        // random key code to attempt to throw off arrow handler
        assertEquals(ArrowKeys.NONE, ArrowKeyHandler.arrowKeyCheckUnix((int)(Math.random() * 100)));
        assertEquals(ArrowKeys.NONE, ArrowKeyHandler.arrowKeyCheckUnix((int)(Math.random() * 100)));

        // test up arrow key
        assertEquals(ArrowKeys.NONE, ArrowKeyHandler.arrowKeyCheckUnix(27));
        assertEquals(ArrowKeys.NONE, ArrowKeyHandler.arrowKeyCheckUnix(91));
        assertEquals(ArrowKeys.UP, ArrowKeyHandler.arrowKeyCheckUnix(65));

        // test down arrow key
        assertEquals(ArrowKeys.NONE, ArrowKeyHandler.arrowKeyCheckUnix(27));
        assertEquals(ArrowKeys.NONE, ArrowKeyHandler.arrowKeyCheckUnix(91));
        assertEquals(ArrowKeys.DOWN, ArrowKeyHandler.arrowKeyCheckUnix(66));

        // random key code to attempt to throw off arrow handler
        assertEquals(ArrowKeys.NONE, ArrowKeyHandler.arrowKeyCheckUnix((int)(Math.random() * 100)));
        assertEquals(ArrowKeys.NONE, ArrowKeyHandler.arrowKeyCheckUnix((int)(Math.random() * 100)));

        // test right arrow key
        assertEquals(ArrowKeys.NONE, ArrowKeyHandler.arrowKeyCheckUnix(27));
        assertEquals(ArrowKeys.NONE, ArrowKeyHandler.arrowKeyCheckUnix(91));
        assertEquals(ArrowKeys.RIGHT, ArrowKeyHandler.arrowKeyCheckUnix(67));

        // random key code to attempt to throw off arrow handler
        assertEquals(ArrowKeys.NONE, ArrowKeyHandler.arrowKeyCheckUnix((int)(Math.random() * 100)));
        assertEquals(ArrowKeys.NONE, ArrowKeyHandler.arrowKeyCheckUnix((int)(Math.random() * 100)));
        assertEquals(ArrowKeys.NONE, ArrowKeyHandler.arrowKeyCheckUnix((int)(Math.random() * 100)));

        // test left arrow key
        assertEquals(ArrowKeys.NONE, ArrowKeyHandler.arrowKeyCheckUnix(27));
        assertEquals(ArrowKeys.NONE, ArrowKeyHandler.arrowKeyCheckUnix(91));
        assertEquals(ArrowKeys.LEFT, ArrowKeyHandler.arrowKeyCheckUnix(68));
    }
}
