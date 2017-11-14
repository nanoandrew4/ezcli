package ezcli.modules.smart_autocomplete;

import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;

public class MultiCmdCompleteTest {

    @Test
    public void test() {
        MultiCmdComplete mcc =
                new MultiCmdComplete("src/test/java/ezcli/modules/smart_autocomplete/prevCommands.txt");

        LinkedList<MultiCmdFreq> commandSequences = mcc.getCommandSequences();

        for (MultiCmdFreq mcf : commandSequences)
            System.out.println(mcf.getCommandSequence() + ", f: " + mcf.getFreq());

        System.out.println(mcc.getCommandSequences().size());
        System.out.println("\n");
    }
}
