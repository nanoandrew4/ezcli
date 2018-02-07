package ezcli.modules.smart_autocomplete;

import ezcli.modules.ezcli_core.Ezcli;
import ezcli.modules.ezcli_core.global_io.handlers.InputHandler;
import ezcli.modules.ezcli_core.modularity.Module;
import ezcli.modules.ezcli_core.terminal.TermInputProcessor;
import ezcli.modules.ezcli_core.terminal.Terminal;

import java.util.ArrayList;
import java.util.List;

/**
 * This class sorts through a command history (which is kept by the program), tries to analyze them
 * for similarities and then suggests the user a command based on the analysis.
 */
public class SmartAutocomplete extends Module {

    private Terminal terminal;

    private MultiCmdComplete mcc = null;

    // Sorted list of most used commands and generalizations derived from users prior input
    private ArrayList<CommandSeq> freqCommands = new ArrayList<>();

    private double initTime = 0;

    private String currentSuggestion = "";

    public SmartAutocomplete() {
        super("SmartAutocomplete");
        terminal = (Terminal) modules.get("Terminal");
        Module.modules.put(moduleName, this);

        // Overrides events in KeyHandler and ArrowKeyHandler
        overrideCharEvent();
        overrideBackspaceEvent();
        overrideRArrEvent();

        initModule();
    }

    private void overrideCharEvent() {
        TermInputProcessor inputProcessor = terminal.inputProcessor;
        inputProcessor.getKeyProcessor().charEvent = (char input) -> {
            String command = inputProcessor.getCommand();
            int cursorPos = inputProcessor.getCursorPos();

            InputHandler.clearLine(command + currentSuggestion, true);

            if (inputProcessor.getCursorPos() == command.length()) {
                Ezcli.ezcliOutput.print(Ezcli.prompt, "prompt");
                Ezcli.ezcliOutput.print(command + input, "command");
                inputProcessor.setCommand(command + input);
            } else {
                inputProcessor.setCommand(new StringBuilder(command).insert(cursorPos, input).toString());
                Ezcli.ezcliOutput.print(Ezcli.prompt, "prompt");
                Ezcli.ezcliOutput.print(inputProcessor.getCommand(), "command");
            }

            currentSuggestion = getMatchingCommand(inputProcessor.getCommand());
            Ezcli.ezcliOutput.print(currentSuggestion, "suggestion");

            inputProcessor.increaseCursorPos();
            moveToCursorPos();
        };
    }

    private void overrideBackspaceEvent() {
        TermInputProcessor inputProcessor = terminal.inputProcessor;
        inputProcessor.getKeyProcessor().backspaceEvent = () -> {
            if (inputProcessor.getCommand().length() > 0 && inputProcessor.getCursorPos() > 0) {
                int charToDelete = inputProcessor.getCursorPos() - 1;
                String command = inputProcessor.getCommand();

                InputHandler.clearLine(command + currentSuggestion, true);

                inputProcessor.setCommand(new StringBuilder(command).deleteCharAt(charToDelete).toString());
                Ezcli.ezcliOutput.print(Ezcli.prompt, "prompt");
                Ezcli.ezcliOutput.print(inputProcessor.getCommand(), "command");

                currentSuggestion = getMatchingCommand(inputProcessor.getCommand());
                Ezcli.ezcliOutput.print(currentSuggestion, "suggestion");

                inputProcessor.decreaseCursorPos();
                moveToCursorPos();
            }
        };
    }

    private void overrideRArrEvent() {
        TermInputProcessor inputProcessor = terminal.inputProcessor;
        inputProcessor.getArrowKeyProcessor().rArrEvent = () -> {
            int cursorPos = inputProcessor.getCursorPos();
            String command = inputProcessor.getCommand();
            if (cursorPos < command.length() + currentSuggestion.length() && cursorPos == command.length()) {
                InputHandler.clearLine(command + currentSuggestion, true);

                inputProcessor.setCommand(command + currentSuggestion.charAt(0));
                command = inputProcessor.getCommand();
                currentSuggestion = getMatchingCommand(command);

                Ezcli.ezcliOutput.print(Ezcli.prompt, "prompt");
                Ezcli.ezcliOutput.print(command, "command");
                Ezcli.ezcliOutput.print(currentSuggestion, "suggestion");

                inputProcessor.increaseCursorPos();
                moveToCursorPos();
            } else if (cursorPos < command.length()) {
                Ezcli.ezcliOutput.print(command.charAt(cursorPos), "command");
                inputProcessor.increaseCursorPos();
            }
        };
    }

    public void moveToCursorPos() {
        TermInputProcessor iP = terminal.inputProcessor;
        for (int i = iP.getCommand().length() + currentSuggestion.length(); i > iP.getCursorPos(); i--)
            Ezcli.ezcliOutput.print("\b", "command");
    }

    public MultiCmdComplete getMcc() {
        return mcc;
    }

    /**
     * Initializes freqCommands list, generalizes the command history to improve smart complete results
     * and sorts list from most used to least used.
     */
    public void initModule() {
        long start = System.currentTimeMillis();

        List<String> commands = terminal.inputProcessor.commandHistory;

        for (int i = 0; i < commands.size(); i++)
            commands.set(i, removeAllAfterQuotes(commands.get(i)));

        for (String s : commands)
            store(s);

        sort(0, freqCommands.size() - 1, freqCommands);

        mcc = new MultiCmdComplete(commands);

        initTime = ((double) (System.currentTimeMillis() - start) / 1000d);
        System.out.println("Init time for SmartAutocomplete was: " + initTime);
    }

    /**
     * Returns the list of frequently used commands.
     *
     * @return List storing CommandSeq objects containing info of frequently used commands
     */
    public ArrayList<CommandSeq> getFreqCommands() {
        return freqCommands;
    }

    /**
     * Returns best guess at what user will type based on input history.
     *
     * @param command Command to suggest a guess for
     * @return Best guess at what the user will enter based command history
     */
    public String getMatchingCommand(String command) {
        if (command.length() < 2)
            return "";

        if (mcc != null) {
            for (CommandSeq c : mcc.getFreqCommandCombos())
                if (c.getCommand().startsWith(command))
                    return c.getCommand().substring(command.length());
        }

        for (CommandSeq c : freqCommands)
            if (c.getCommand().startsWith(command))
                return c.getCommand().substring(command.length());

        return "";
    }

    /**
     * Stores a command if it does not already exist, and increments the counter on it if the
     * command is already stored.
     *
     * @param command Command to store
     */
    public void store(String command) {
        boolean stored = false;
        for (CommandSeq cf : freqCommands) {
            if (cf.getCommand().equals(command)) {
                cf.incrementFreq();
                stored = true;
                break;
            }
        }

        if (!stored)
            freqCommands.add(new CommandSeq(command));
    }

    /**
     * Removes anything after quotes in strings, since algorithm will not be able to generalize with
     * such variable discrepancies.
     *
     * @param rawCommand Command to parse
     * @return Command with anything after the quotes removed
     */
    private String removeAllAfterQuotes(String rawCommand) {
        int pos = 0;
        for (; pos < rawCommand.length(); pos++)
            if (rawCommand.charAt(pos) == '\"')
                return rawCommand.substring(0, pos - 1);

        return rawCommand;
    }

    /**
     * Sorts a CommandSeq list using quicksort.
     *
     * @param lPiv Leftmost chunk of list to sort
     * @param rPiv Rightmost chunk of list to sort
     */
    protected static void sort(int lPiv, int rPiv, ArrayList<CommandSeq> freqCommands) {
        if (freqCommands.size() == 0)
            return;

        int cPiv = freqCommands.get((rPiv + lPiv) / 2).getFreq();
        int a = lPiv, b = rPiv;

        while (a <= b) {
            while (freqCommands.get(a).getFreq() > cPiv)
                a++;
            while (freqCommands.get(b).getFreq() < cPiv)
                b--;
            if (a <= b) {
                CommandSeq cfTmp = freqCommands.get(a);
                freqCommands.set(a, freqCommands.get(b));
                freqCommands.set(b, cfTmp);
                a++;
                b--;
            }
        }

        if (b < rPiv)
            sort(lPiv, b, freqCommands);
        if (a < rPiv)
            sort(a, rPiv, freqCommands);
    }

    @Override
    public void run() {/* Nothing do to */}

    @Override
    public void tour() {
        // TODO
    }
}
