package ezcli.modules.ezcli_core.global_io;

import org.junit.Test;
import static org.junit.Assert.*;

public class ArrowKeyTest {

    /*
     * Tests that processing of arrow key codes on Windows systems work
     */
    @Test
    public void testWinArrowHandling() {
        assertEquals(ArrowKeys.UP, ArrowKeyHandler.arrowKeyCheckWindows(57416));
        assertEquals(ArrowKeys.DOWN, ArrowKeyHandler.arrowKeyCheckWindows(57424));
        assertEquals(ArrowKeys.LEFT, ArrowKeyHandler.arrowKeyCheckWindows(57419));
        assertEquals(ArrowKeys.RIGHT, ArrowKeyHandler.arrowKeyCheckWindows(57421));
        assertEquals(ArrowKeys.NONE, ArrowKeyHandler.arrowKeyCheckWindows(0));
        assertEquals(ArrowKeys.NONE, ArrowKeyHandler.arrowKeyCheckWindows(10000));
    }

    /*
     * Tests that processing of arrow key codes on Unix systems work
     */
    @Test
    public void testUnixArrowHandling() {

        // random key code to attempt to throw off arrow handler
        assertEquals(ArrowKeys.NONE, ArrowKeyHandler.arrowKeyCheckUnix(1000));
        assertEquals(ArrowKeys.NONE, ArrowKeyHandler.arrowKeyCheckUnix(-22));

        // test up arrow key
        assertEquals(ArrowKeys.NONE, ArrowKeyHandler.arrowKeyCheckUnix(27));
        assertEquals(ArrowKeys.NONE, ArrowKeyHandler.arrowKeyCheckUnix(91));
        assertEquals(ArrowKeys.UP, ArrowKeyHandler.arrowKeyCheckUnix(65));

        // test down arrow key
        assertEquals(ArrowKeys.NONE, ArrowKeyHandler.arrowKeyCheckUnix(27));
        assertEquals(ArrowKeys.NONE, ArrowKeyHandler.arrowKeyCheckUnix(91));
        assertEquals(ArrowKeys.DOWN, ArrowKeyHandler.arrowKeyCheckUnix(66));

        // random key code to attempt to throw off arrow handler
        assertEquals(ArrowKeys.NONE, ArrowKeyHandler.arrowKeyCheckUnix(22));
        assertEquals(ArrowKeys.NONE, ArrowKeyHandler.arrowKeyCheckUnix(Integer.MIN_VALUE));

        // test right arrow key
        assertEquals(ArrowKeys.NONE, ArrowKeyHandler.arrowKeyCheckUnix(27));
        assertEquals(ArrowKeys.NONE, ArrowKeyHandler.arrowKeyCheckUnix(91));
        assertEquals(ArrowKeys.RIGHT, ArrowKeyHandler.arrowKeyCheckUnix(67));

        // random key code to attempt to throw off arrow handler
        assertEquals(ArrowKeys.NONE, ArrowKeyHandler.arrowKeyCheckUnix(-1));
        assertEquals(ArrowKeys.NONE, ArrowKeyHandler.arrowKeyCheckUnix(1));
        assertEquals(ArrowKeys.NONE, ArrowKeyHandler.arrowKeyCheckUnix(Integer.MAX_VALUE));

        // test left arrow key
        assertEquals(ArrowKeys.NONE, ArrowKeyHandler.arrowKeyCheckUnix(27));
        assertEquals(ArrowKeys.NONE, ArrowKeyHandler.arrowKeyCheckUnix(91));
        assertEquals(ArrowKeys.LEFT, ArrowKeyHandler.arrowKeyCheckUnix(68));
    }
}
