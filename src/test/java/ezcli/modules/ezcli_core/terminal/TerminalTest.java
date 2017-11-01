package ezcli.modules.ezcli_core.terminal;

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
    }
}
