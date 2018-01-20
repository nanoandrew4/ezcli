package ezcli.modules.ezcli_core.interactive;

import ezcli.modules.ezcli_core.Ezcli;
import ezcli.modules.ezcli_core.global_io.Command;
import ezcli.modules.ezcli_core.global_io.handlers.InputHandler;
import ezcli.modules.ezcli_core.modularity.Module;

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

    public void run() {
        Ezcli.ezcliOutput.print(Ezcli.prompt, "prompt");

        while (!exit) {
            inputProcessor.process(InputHandler.getKey());
        }
    }

    public void parse(String command) {
        this.currentlyActive = true;
        Ezcli.ezcliOutput.println();

        switch (command) {
            case "h":
                help();
                break;
            case "b":
                exit = true;
                Ezcli.ezcliOutput.println("Exiting application", "info");
                return;
            case "o":
                tour();
                break;
            default: // Pass to hashmap in Module
                Module m = Module.moduleMap.get(command);
                if (m == null) {
                    Ezcli.ezcliOutput.println("Module not found", "info");
                    inputProcessor.setCommand("");
                    Ezcli.ezcliOutput.print(Ezcli.prompt, "prompt");
                    return;
                } else {
                    this.currentlyActive = false;
                    m.run();
                    this.currentlyActive = true;
                }
        }

        inputProcessor.setCommand("");
        if (!"h".equals(command))
            Ezcli.ezcliOutput.println("Back in interactive module", "info");
        Ezcli.ezcliOutput.print(Ezcli.prompt, "prompt");
    }

    private void help() {
        Ezcli.ezcliOutput.println("ezcli version: " + Ezcli.VERSION + "\n", "info");
        Ezcli.ezcliOutput.println("Enter \"t\" to enter terminal mode", "info");
        Ezcli.ezcliOutput.println("Enter \"h\" to display this menu", "info");
        Ezcli.ezcliOutput.println("Enter \"o\" to get a guided tour of the program", "info");
        Ezcli.ezcliOutput.println("Enter \"b\" to exit the program", "info");
    }

    @Override
    public void tour() {
        Ezcli.ezcliOutput.println("You are currently in the " + this.moduleName + " module.", "info");
        Ezcli.ezcliOutput.println("From this module you will access all other modules.", "info");
        Ezcli.ezcliOutput.println("You will now be guided through the rest of the program.", "info");
        Ezcli.ezcliOutput.println("Press Ctrl+C at any time to exit.\n\n", "info");
        if (Ezcli.sleep(7) != Command.NONE)
            return;
        for (Module m : Module.modules.values())
            m.tour();
    }
}
