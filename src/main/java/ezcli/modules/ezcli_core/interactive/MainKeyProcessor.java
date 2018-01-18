package ezcli.modules.ezcli_core.interactive;

import ezcli.modules.ezcli_core.Ezcli;
import ezcli.modules.ezcli_core.global_io.handlers.KeyHandler;

/**
 * Processes key presses (except arrow keys) for Interactive module.
 *
 * @see Interactive
 */
public class MainKeyProcessor extends KeyHandler {

    private MainInputProcessor inputProcessor;

    MainKeyProcessor(MainInputProcessor inputProcessor) {
        this.inputProcessor = inputProcessor;
        setUpTabEvent();
        setUpNWLNEvent();
        setUpCharEvent();
        setUpBackspaceEvent();
    }

    private void setUpTabEvent() {
        tabEvent = () -> {};
    }

    private void setUpNWLNEvent() {
        newLineEvent = () -> {};
    }

    private void setUpCharEvent() {
        charEvent = (char input) -> {
            inputProcessor.setCommand(inputProcessor.getCommand() + input);
            Ezcli.ezcliOutput.print(input, "command");
            inputProcessor.parse();
        };
    }

    private void setUpBackspaceEvent() {
        backspaceEvent = () -> {
            if (inputProcessor.getCommand().length() > 0) {
                Ezcli.ezcliOutput.println("\b \b", "command");

                String command = inputProcessor.getCommand();
                int commandLength = command.length();

                inputProcessor.setCommand(new StringBuilder(command).deleteCharAt(commandLength).toString());
            }
        };
    }

    @Override
    public void process(int input) {
        super.process(input);
    }
}
