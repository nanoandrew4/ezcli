package ezcli.modules.ezcli_core.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class FileAutocompleteTest {

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
