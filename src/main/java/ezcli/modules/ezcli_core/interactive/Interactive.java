package ezcli.modules.ezcli_core.interactive;

import ezcli.modules.ezcli_core.Module;
import ezcli.modules.ezcli_core.Ezcli;
import ezcli.modules.ezcli_core.term.Terminal;

/**
 * Interactive module. Entry point for program, and module used to reach all others.
 * When adding a new module, simply add a case clause in the switch inside the parse method
 * which points at the modules run method.
 */
public class Interactive extends Module {

    private Terminal term; // terminal

    private MainInputProcessor inputProcessor; // processes input in interactive mode

    static boolean parse;
    private boolean exit;

    public Interactive() {
        super();
        inputProcessor = new MainInputProcessor();
        term = new Terminal();
    }

    @Override
    public void run() {
        System.out.print(Ezcli.prompt);

        while (!exit) {
            inputProcessor.process();
            if (parse)
                parse(MainInputProcessor.command);
        }
    }

    @Override
    public void parse(String command) {
        parse = false;
        System.out.println();

        switch (command) {
            case "t": // terminal
                term.run();
                break;
            case "h": // help
                help();
                break;
            case "b": // exit
                exit = true;
                System.out.println("Exiting application");
                return;
            default:
                System.out.println("Module not found");
                MainInputProcessor.command = "";
                System.out.print(Ezcli.prompt);
                return;
        }
        MainInputProcessor.command = "";
        if (!"h".equals(command))
            System.out.println("Back in interactive mode");
        System.out.print(Ezcli.prompt);
    }

    @Override
    public void help() {
        System.out.println("Enter \"t\" to enter terminal mode");
        System.out.println("Enter \"h\" to display this menu");
        System.out.println("Enter \"b\" to exit the program");
    }

    @Override
    public void tour() {
        // to be implemented
    }
}
