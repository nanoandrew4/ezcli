package ezcli.modules.ezcli_core.interactive;

import ezcli.modules.ezcli_core.Ezcli;
import ezcli.modules.ezcli_core.global_io.Command;
import ezcli.modules.ezcli_core.global_io.handlers.InputHandler;
import ezcli.modules.ezcli_core.modularity.Module;

/**
 * Interactive module. Entry point for program, and module used to reach all proper modules, in other words,
 * the ones which call the super class init(Module, String) method.
 * <p>
 * For more information on how to add your module to the program, see the Module class.
 *
 * @see Module
 */
public class Interactive extends Module {

    // Input processor for this module
    public MainInputProcessor inputProcessor;

    private boolean exit;

    public Interactive() {
        super("Interactive");
        inputProcessor = new MainInputProcessor(this);
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
        Module m = null;

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
                m = Module.moduleMap.get(command);
                if (m == null)
                    Ezcli.ezcliOutput.println("Module not found", "info");
                else {
                    this.currentlyActive = false;
                    m.run();
                    this.currentlyActive = true;
                }
        }

        inputProcessor.setCommand("");
        if (!"h".equals(command) && m != null)
            Ezcli.ezcliOutput.println("Back in interactive module", "info");
        Ezcli.ezcliOutput.print(Ezcli.prompt, "prompt");
    }

    private void help() {
        Ezcli.ezcliOutput.println("ezcli version: " + Ezcli.VERSION + "\n", "info");
        Ezcli.ezcliOutput.println("Enter \"h\" to display this menu", "info");
        Ezcli.ezcliOutput.println("Enter \"o\" to get a guided tour of the program", "info");
        Ezcli.ezcliOutput.println("Enter \"b\" to exit the program", "info");
        Ezcli.ezcliOutput.println("List of modules: ", "info");

        Object[] keyMaps = Module.moduleMap.keySet().toArray();
        Object[] modules = Module.moduleMap.values().toArray();
        for (int i = 0; i < Module.moduleMap.size(); i++)
            System.out.println("Module \"" + modules[i].toString() + "\" mapped to key \"" + keyMaps[i] + "\"");
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
