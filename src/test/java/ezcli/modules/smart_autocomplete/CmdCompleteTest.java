package ezcli.modules.smart_autocomplete;

import ezcli.modules.ezcli_core.Ezcli;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CmdCompleteTest {

    @BeforeClass
    public static void disableOutput() {
        if (!Ezcli.testOutput) {
            System.setOut(new PrintStream(new OutputStream() {
                public void write(int b) {
                    // no output
                }
            }));
        }
    }

    @Test
    public void test() {
        CmdComplete cmdComplete = new CmdComplete("");
        try {
            List<String> commands =
                    Files.readAllLines(Paths.get("src/test/java/ezcli/modules/smart_autocomplete/prevCommands.txt"));
            cmdComplete.init(commands);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<CommandFreq> freqCommands = cmdComplete.getFreqCommands();
        for (CommandFreq cf : freqCommands) {
            System.out.println(cf.getCommand() + ", " + cf.getFreq());
        }

        System.out.println("\n");

        System.out.println(freqCommands.size());
    }
}
