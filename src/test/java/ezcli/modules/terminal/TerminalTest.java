package ezcli.modules.terminal;

import org.junit.Test;
import static org.junit.Assert.*;

public class TerminalTest {

    @Test
    public void onlySpacesTest() {
        assertTrue(Terminal.containsOnlySpaces("   "));
        assertFalse(Terminal.containsOnlySpaces("a"));
        assertTrue(Terminal.containsOnlySpaces(""));
        assertFalse(Terminal.containsOnlySpaces(" a "));
        assertFalse(Terminal.containsOnlySpaces(" a ~" + (char)3));

        assertEquals("test", Terminal.removeSpaces("  test "));
        assertEquals("test", Terminal.removeSpaces("                           test              "));
        assertEquals("t e s t", Terminal.removeSpaces("   t e s t   "));
    }
}
