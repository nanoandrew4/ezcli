package ezcli.modules.ezcli_core.modularity;

import ezcli.modules.ezcli_core.Ezcli;
import ezcli.modules.ezcli_core.global_io.ArrowKeys;
import ezcli.modules.ezcli_core.terminal.*;
import ezcli.modules.testModules.*;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.OutputStream;
import java.io.PrintStream;

public class BasicModularityTest {

    @BeforeClass
    public static void disableOutput() {
        if (!Ezcli.displayTestOutput || !Ezcli.displayModularityTestOutput)
            System.setOut(new PrintStream(new OutputStream() {
                public void write(int b) {
                    // no output
                }
            }));
        else
            System.setOut(Ezcli.stdOutput);
    }

    @Test
    public void modularityTest() {
        Ezcli.setOS();
        Module.initModules("src/test/java/ezcli/modules/ezcli_core/modularity/basicModulesTest.txt");

        Terminal t = (Terminal) Module.modules.get("Terminal");
        TermKeyProcessor tkp = t.inputProcessor.getKeyProcessor();
        TermArrowKeyProcessor takp = t.inputProcessor.getArrowKeyProcessor();

        tkp.charEvent.process('a');
        tkp.charEvent.process('/');
        tkp.charEvent.process('\n');
        takp.process(ArrowKeys.DOWN);
        assertEquals("0", String.valueOf(Module1.someChar));
        assertEquals(49, Module2.someInt);
        assertEquals("cba00", Module3.someStr);

        takp.process(ArrowKeys.RIGHT);
        takp.process(ArrowKeys.UP);
        assertEquals("0", String.valueOf(Module1.someChar));
        assertEquals(50, Module2.someInt);
        assertEquals("cba0012", Module3.someStr);

        tkp.process('b');
        System.out.println();
        assertEquals("2", String.valueOf(Module1.someChar));
        assertEquals(50, Module2.someInt);
        assertEquals("cba00122", Module3.someStr);
    }
}