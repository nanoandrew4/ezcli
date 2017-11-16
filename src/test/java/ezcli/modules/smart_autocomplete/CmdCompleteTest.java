package ezcli.modules.smart_autocomplete;

import ezcli.modules.ezcli_core.Ezcli;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class CmdCompleteTest {

    @BeforeClass
    public static void disableOutput() {
        if (!Ezcli.testOutput || !Ezcli.smartCompleteOuput)
            System.setOut(new PrintStream(new OutputStream() {
                public void write(int b) {
                    // no output
                }
            }));
        else
            System.setOut(Ezcli.os);
    }

    @Test
    public void test() {
        CmdComplete cmdComplete =
                new CmdComplete("src/test/java/ezcli/modules/smart_autocomplete/prevCommands.txt");

        ArrayList<CommandFreq> freqCommands = cmdComplete.getFreqCommands();
        for (CommandFreq cf : freqCommands) {
            System.out.println(cf.getCommand() + ", " + cf.getFreq());
        }

        System.out.println("\n");

        System.out.println(freqCommands.size());
    }
}
