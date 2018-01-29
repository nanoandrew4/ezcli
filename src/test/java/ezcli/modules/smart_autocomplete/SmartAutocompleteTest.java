package ezcli.modules.smart_autocomplete;

import ezcli.modules.ezcli_core.Ezcli;
import ezcli.modules.ezcli_core.modularity.Module;
import ezcli.modules.ezcli_core.terminal.Terminal;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SmartAutocompleteTest {

    @BeforeClass
    public static void disableOutput() {
        if (!Ezcli.displayTestOutput || !Ezcli.displaySmartAutocompleteOutput)
            System.setOut(new PrintStream(new OutputStream() {
                public void write(int b) {
                    // no output
                }
            }));
        else
            System.setOut(Ezcli.stdOutput);
    }

    @Test
    public void test() {
        Module.modules.clear();

        Terminal t = new Terminal();
        t.inputProcessor.commandHistory.clear();
        try {
            List<String> l =
                    Files.readAllLines(Paths.get("src/test/java/ezcli/modules/smart_autocomplete/prevCommands.txt"));
            t.inputProcessor.commandHistory.addAll(l);
        } catch (IOException e) {
            e.printStackTrace();
        }

        SmartAutocomplete smartAutocomplete = new SmartAutocomplete();

        for (CommandFreq cf : smartAutocomplete.getMcc().getCommandSequences())
            System.out.println(cf.getCommandSequence());

        System.out.println("\n\n");

        assertEquals("n package && java -jar target/ezcli-0.3.0-jar-with-dependencies.jar",
                smartAutocomplete.getMatchingCommand("mv"));
        assertEquals("JTerm/", smartAutocomplete.getMatchingCommand("cd "));
        assertEquals(" -jar IdeaProjects/JTerm/target/jterm-0.6.2-jar-with-dependencies.jar headless && clear",
                smartAutocomplete.getMatchingCommand("java"));
        assertEquals("ear && java -jar IdeaProjects/JTerm/target/jterm-0.6.2-jar-with-dependencies.jar headless",
                smartAutocomplete.getMatchingCommand("cl"));
        assertEquals("arget/ezcli-0.3.0-jar-with-dependencies.jar && mvn package",
                smartAutocomplete.getMatchingCommand("java -jar t"));
        assertEquals("est",  smartAutocomplete.getMatchingCommand("mvn t"));
    }
}
