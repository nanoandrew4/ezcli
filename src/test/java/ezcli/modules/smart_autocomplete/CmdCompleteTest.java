package ezcli.modules.smart_autocomplete;

import ezcli.modules.ezcli_core.Ezcli;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.OutputStream;
import java.io.PrintStream;

public class CmdCompleteTest {

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
        /*SmartAutocomplete cmdComplete =
                new SmartAutocomplete("src/test/java/ezcli/modules/smart_autocomplete/prevCommands.txt");

        ANSIColorOutput co = new ANSIColorOutput(ANSIColorOutput.DEFAULT_COLOR, ANSIColorOutput.PRETTY_BLUE);

        ArrayList<CommandFreq> freqCommands = cmdComplete.getFreqCommands();
        for (CommandFreq cf : freqCommands) {
            System.out.println(cf.getCommand() + ", " + cf.getFreq());
        }

        System.out.println("\n");

        co.print(freqCommands.size());*/
    }
}
