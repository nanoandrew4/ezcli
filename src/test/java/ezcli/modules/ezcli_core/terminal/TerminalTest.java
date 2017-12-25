package ezcli.modules.ezcli_core.terminal;

import ezcli.modules.ezcli_core.Ezcli;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.OutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

public class TerminalTest {

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
    public void onlySpacesTest() {
        assertTrue(Terminal.containsOnlySpaces("   "));
        assertFalse(Terminal.containsOnlySpaces("a"));
        assertTrue(Terminal.containsOnlySpaces(""));
        assertFalse(Terminal.containsOnlySpaces(" a "));
        assertFalse(Terminal.containsOnlySpaces(" a ~" + (char) 3));
    }
}
