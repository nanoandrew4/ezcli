package ezcli.modules.ezcli_core.interactive;

import ezcli.modules.ezcli_core.Module;
import ezcli.modules.ezcli_core.Ezcli;
import ezcli.modules.ezcli_core.global_io.InputHandler;

/**
 * Interactive module. Entry point for program, and module used to reach all others.
 * When adding a new module, simply add a case clause in the switch inside the parse method
 * which points at the modules run method.
 */
public class Interactive extends Module {

    private MainInputProcessor inputProcessor; // processes input in interactive mode

    protected boolean parse;
    private boolean exit;

    public Interactive() {
        inputProcessor = new MainInputProcessor(this);
    }

    protected MainInputProcessor getInputProcessor() {
        return inputProcessor;
    }

    @Override
    public void run() {
        System.out.print(Ezcli.prompt);

        while (!exit) {
            inputProcessor.process(InputHandler.getKey());
            if (parse)
                parse(inputProcessor.getCommand());
        }
    }

    @Override
    public void parse(String command) {
        parse = false;

        switch (command) {
            case "h": // help
                help();
                break;
            case "b": // exit
                exit = true;
                System.out.println("\nExiting application");
                return;
            default: // pass to hashmap in Module
                Module m = Module.moduleMap.get(command);
                if (m == null) {
                    System.out.println("\nModule not found");
                    inputProcessor.setCommand("");
                    System.out.print(Ezcli.prompt);
                    return;
                } else
                    m.run();
        }
        inputProcessor.setCommand("");
        if (!"h".equals(command))
            System.out.println("Back in interactive mode");
        System.out.print(Ezcli.prompt);
    }

    @Override
    public void help() {
        System.out.println("ezcli version: " + Ezcli.VERSION + "\n");
        System.out.println("Enter \"t\" to enter terminal mode");
        System.out.println("Enter \"h\" to display this menu");
        System.out.println("Enter \"b\" to exit the program");
    }

    @Override
    public void tour() {
        // to be implemented
    }
}
