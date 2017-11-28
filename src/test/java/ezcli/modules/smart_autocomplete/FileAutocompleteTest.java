package ezcli.modules.smart_autocomplete;

import ezcli.modules.ezcli_core.Ezcli;
import ezcli.modules.smart_autocomplete.FileAutocomplete;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.OutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

public class FileAutocompleteTest {

    @BeforeClass
    public static void disableOutput() {
        if (!Ezcli.testOutput || !Ezcli.ezcliCoreOuput)
            System.setOut(new PrintStream(new OutputStream() {
                public void write(int b) {
                    // no output
                }
            }));
        else
            System.setOut(Ezcli.stdOutput);
    }

    /*
     * Tests the getPath() method in FileAutocomplete
     */
    @Test
    public void getPathTest() {
        FileAutocomplete.setCurrText("/home/username/fishingsim2k17/baz");
        assertEquals("/home/username/fishingsim2k17/", FileAutocomplete.getPath());
        FileAutocomplete.resetVars();

        FileAutocomplete.setCommand("/home/othername/sssssssstatic ");
        assertEquals("", FileAutocomplete.getPath());
        FileAutocomplete.resetVars();

        FileAutocomplete.setCommand("/");
        assertEquals("/", FileAutocomplete.getPath());
        FileAutocomplete.resetVars();

        FileAutocomplete.setCommand("username/anothertest/te");
        assertEquals("username/anothertest/", FileAutocomplete.getPath());
        FileAutocomplete.resetVars();

        FileAutocomplete.setCommand("username/");
        assertEquals("username/", FileAutocomplete.getPath());
        FileAutocomplete.resetVars();
    }
}