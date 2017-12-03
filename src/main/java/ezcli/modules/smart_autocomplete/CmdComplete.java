package ezcli.modules.smart_autocomplete;

import ezcli.modules.ezcli_core.util.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class sorts through a command history (which is kept by the program), tries to analyze them
 * for similarities and then suggests the user a command based on the analysis.
 */
public class CmdComplete {

    private MultiCmdComplete mcc;

    // Sorted list of most used commands and generalizations derived from users prior input
    private ArrayList<CommandFreq> freqCommands = new ArrayList<>();

    private double initTime = 0;

    /**
     * Initializes the smart completion, by loading the history file and initializing the freqCommands
     * list.
     *
     * @param pathToStoredCommands Path to file where all the previously written commands are
     */
    public CmdComplete(String pathToStoredCommands) {

        long start = System.currentTimeMillis();
        try {
            List<String> commands = Files.readAllLines(Paths.get(pathToStoredCommands));

            init(commands);
            mcc = new MultiCmdComplete(pathToStoredCommands);
        } catch (IOException e) {
            init(new LinkedList<>());
            mcc = null;
            System.err.println("Error. No history file found, creating empty list");
        }

        initTime = ((double)(System.currentTimeMillis() - start) / 1000d);
        System.out.println("Init time for CmdComplete was: " + initTime);
    }

    /**
     * Returns best guess at what user will type based on input history.
     *
     * @param command Command to suggest a guess for
     * @return Best guess at what the user will enter based command history
     */
    public String getMatchingCommand(String command) {
        if (mcc != null) {
            for (CommandFreq c : mcc.getCommandSequences())
                if (c.getCommandSequence().startsWith(command))
                    return c.getCommandSequence().substring(command.length());
        }

        for (CommandFreq c : freqCommands)
            if (c.getCommand().startsWith(command))
                return c.getCommand().substring(command.length());

        return "";
    }

    /**
     * Returns the list of frequently used commands.
     *
     * @return List storing CommandFreq objects containing info of frequently used commands
     */
    public ArrayList<CommandFreq> getFreqCommands() {
        return freqCommands;
    }

    public double getInitTime() {
        return initTime;
    }

    /**
     * Initializes freqCommands list, generalizes the command history to improve smart complete results
     * and sorts list from most used to least used.
     *
     * @param commands List of commands to initialize class with
     */
    private void init(List<String> commands) {
        for (int i = 0; i < commands.size(); i++)
            commands.set(i, removeAllAfterQuotes(commands.get(i)));

        for (String s : commands)
            store(s);

        Util.sort(0, freqCommands.size() - 1, freqCommands);
    }

    /**
     * Stores a command if it does not already exist, and increments the counter on it if the
     * command is already stored.
     * @param command Command to store
     */
    private void store(String command) {
        boolean stored = false;
        for (CommandFreq cf : freqCommands) {
            if (cf.getCommand().equals(command)) {
                cf.incrementFreq();
                stored = true;
                break;
            }
        }

        if (!stored)
            freqCommands.add(new CommandFreq(command));
    }

    /**
     * Returns if freqCommands is sorted from most frequent to least frequent.
     *
     * @return if freqCommands is sorted from greatest to smallest frequency
     */
    private boolean isSorted() {
        for (int i = 1; i < freqCommands.size(); i++)
            if (freqCommands.get(i - 1).getFreq() < freqCommands.get(i).getFreq())
                return false;
        return true;
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
}
