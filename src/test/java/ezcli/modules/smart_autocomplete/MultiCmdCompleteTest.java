package ezcli.modules.smart_autocomplete;

import ezcli.modules.ezcli_core.Ezcli;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class MultiCmdCompleteTest {

    @BeforeClass
    public static void disableOutput() {
        if (!Ezcli.testOutput || !Ezcli.smartCompleteOuput)
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
        MultiCmdComplete mcc =
                new MultiCmdComplete("src/test/java/ezcli/modules/smart_autocomplete/prevCommands.txt");

        ArrayList<CommandFreq> commandSequences = mcc.getCommandSequences();

        for (CommandFreq mcf : commandSequences)
            System.out.println(mcf.getCommandSequence() + ", f: " + mcf.getFreq());

        System.out.println(mcc.getCommandSequences().size());
        System.out.println("\n");

    }
}
