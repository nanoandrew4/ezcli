package ezcli.modules.smart_autocomplete;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * This class sorts through a command history (which is kept by the program), tries to analyze them
 * for similarities and then suggests the user a command based on the analysis.
 */
public class CmdComplete {

    // Sorted list of most used commands and generalizations derived from users prior input
    private ArrayList<CommandFreq> freqCommands = new ArrayList<>();

    /**
     * Initializes the smart completion, by loading the history file and initializing the freqCommands
     * list.
     *
     * @param pathToStoredCommands Path to file where all the previously written commands are
     */
    CmdComplete(String pathToStoredCommands) {
        try {
            List<String> commands = Files.readAllLines(Paths.get(pathToStoredCommands));

            init(commands);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the list of frequently used commands.
     *
     * @return List storing CommandFreq objects containing info of frequently used commands
     */
    public ArrayList<CommandFreq> getFreqCommands() {
        return freqCommands;
    }

    /**
     * Initializes freqCommands list, generalizes the command history to improve smart complete results
     * and sorts list from most used to least used.
     *
     * @param commands List of commands to initialize class with
     */
    public void init(List<String> commands) {
        for (int i = 0; i < commands.size(); i++)
            commands.set(i, removeAllAfterQuotes(commands.get(i)));

        for (String s : commands)
            process(s);

        sort(0, freqCommands.size() - 1);
    }

    /**
     * Processes each individual string, adds them to the list and tries to determine if it can be generalized.
     *
     * @param command Command to process
     */
    public void process(String command) {
        store(command);
        compare(command);
    }

    /**
     * Stores a command if it does not already exist, and increments the counter on it if the
     * command is already stored.
     * @param command
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
     * Sorts the list freqCommands using quicksort.
     *
     * @param lPiv Leftmost chunk of list to sort
     * @param rPiv Rightmost chunk of list to sort
     */
    private void sort(int lPiv, int rPiv) {
        int cPiv = freqCommands.get((rPiv + lPiv) / 2).getFreq();
        int a = lPiv, b = rPiv;

        while (a <= b) {
            while (freqCommands.get(a).getFreq() > cPiv)
                a++;
            while (freqCommands.get(b).getFreq() < cPiv)
                b--;
            if (a <= b) {
                CommandFreq cfTmp = freqCommands.get(a);
                freqCommands.set(a, freqCommands.get(b));
                freqCommands.set(b, cfTmp);
                a++;
                b--;
            }
        }

        if (b < rPiv)
            sort(lPiv, b);
        if (a < rPiv)
            sort(a, rPiv);
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
     * Compares a string to the rest of the freqCommands list to determine if a generalization is viable,
     * and if so, generalizes the string and removes any similar ones.
     *
     * @param command String to attempt to generalize
     */
    private void compare(String command) {
        String[] oCommand = command.split(" ");

        if (oCommand.length < 3)
            return;

        LinkedList<String> commonStrings = new LinkedList<>();
        HashMap<String, CommandFreq> freqPartialCommands = new HashMap<>();
        HashMap<String, LinkedList<String>> similarCommandsHashMap = new HashMap<>();

        for (CommandFreq cf : freqCommands) {
            int seqsInCommon = 0;
            String[] sCommand = cf.getCommand().split(" ");
            StringBuilder partsInCommon = new StringBuilder("");

            for (int i = 0; i < oCommand.length && i < sCommand.length; i++) {
                if (oCommand[i].equals(sCommand[i])) {
                    seqsInCommon++;
                    partsInCommon.append(sCommand[i]).append(" ");
                } else
                    break;
            }

            double fitness = seqsInCommon / (double)sCommand.length;
            if (fitness > 0.7) {

                if (!commonStrings.contains(partsInCommon.toString()))
                    commonStrings.add(partsInCommon.toString());

                //System.out.println(cf.getCommand() + " -> " + partsInCommon.toString());

                CommandFreq gcf = freqPartialCommands.get(partsInCommon.toString());
                if (gcf == null) {
                    freqPartialCommands.put(partsInCommon.toString(), new CommandFreq(partsInCommon.toString()));
                    LinkedList<String> newList = new LinkedList<>();
                    newList.add(cf.getCommand());
                    similarCommandsHashMap.put(partsInCommon.toString(), newList);
                } else {
                    gcf.incrementFreq();
                    LinkedList<String> simCommands = similarCommandsHashMap.get(partsInCommon.toString());
                    simCommands.add(cf.getCommand());
                }
            }
        }

        ArrayList<CommandFreq> tmp = (ArrayList<CommandFreq>) freqCommands.clone();

        for (String s : commonStrings) {
            CommandFreq cf = freqPartialCommands.get(s);
            if (cf.getFreq() > 3) {
                LinkedList<String> similarCommands = similarCommandsHashMap.get(s);
                for (String simCmd : similarCommands) // vision of nightmare
                    for (CommandFreq cfs : freqCommands)
                        if (cfs.getCommand().startsWith(simCmd))
                            tmp.remove(cfs);
            }
        }

        freqCommands = tmp;
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
