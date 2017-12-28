package ezcli.modules.ezcli_core.interactive;

import ezcli.modules.ezcli_core.EventState;
import ezcli.modules.ezcli_core.Module;
import ezcli.modules.ezcli_core.Ezcli;
import ezcli.modules.ezcli_core.global_io.Command;
import ezcli.modules.ezcli_core.global_io.InputHandler;

/**
 * Interactive module. Entry point for program, and module used to reach all others.
 * When adding a new module, simply add a case clause in the switch inside the parse method
 * which points at the modules run method.
 */
public class Interactive extends Module {

    // Input processor for this module
    private MainInputProcessor inputProcessor;

    private boolean exit;

    public Interactive() {
        super("Interactive");
        inputProcessor = new MainInputProcessor(this);
    }

    protected MainInputProcessor getInputProcessor() {
        return inputProcessor;
    }

    @Override
    public EventState getWhenToRun() {
        return null;
    }

    public void run() {
        System.out.print(Ezcli.prompt);

        while (!exit) {
            inputProcessor.process(InputHandler.getKey());
        }
    }

    public void parse(String command) {
        System.out.println();

        switch (command) {
            case "h":
                help();
                break;
            case "b":
                exit = true;
                System.out.println("Exiting application");
                return;
            case "o":
                tour();
                break;
            default: // Pass to hashmap in Module
                Module m = Module.moduleMap.get(command);
                if (m == null) {
                    System.out.println("Module not found");
                    inputProcessor.setCommand("");
                    System.out.print(Ezcli.prompt);
                    return;
                } else
                    m.run();
        }

        inputProcessor.setCommand("");
        if (!"h".equals(command))
            System.out.println("Back in interactive module");
        System.out.print(Ezcli.prompt);
    }

    private void help() {
        System.out.println("ezcli version: " + Ezcli.VERSION + "\n");
        System.out.println("Enter \"t\" to enter terminal mode");
        System.out.println("Enter \"h\" to display this menu");
        System.out.println("Enter \"o\" to get a guided tour of the program");
        System.out.println("Enter \"b\" to exit the program");
    }

    @Override
    public void tour() {
        System.out.println("You are currently in the " + this.moduleName + " module.");
        System.out.println("From this module you will access all other modules.");
        System.out.println("You will now be guided through the rest of the program.");
        System.out.println("Press Ctrl+C at any time to exit.\n\n");
        if (Ezcli.sleep(7) != Command.NONE)
            return;
        for (Module m : Module.modules.values())
            m.tour();
    }
}
