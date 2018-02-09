package ezcli.modules.smart_autocomplete;

import java.util.ArrayList;
import java.util.List;

public class MultiCmdComplete {

    // List containing all previously typed commands
    private ArrayList<String> commandHistory;

    private final static int COMMAND_COMBO_LIMIT = 5;
    private final static int MIN_COMMAND_FREQ = 4;

    // List containing all command sequences
    private ArrayList<CommandSeq> freqCommandCombos;

    /**
     * Init class using command history file.
     */
    MultiCmdComplete(List<String> commandHistory) {

        this.commandHistory = new ArrayList<>(commandHistory);
        freqCommandCombos = new ArrayList<>();

        populateList(2);
        SmartAutocomplete.sort(0, freqCommandCombos.size() - 1, freqCommandCombos);
    }

    public ArrayList<CommandSeq> getFreqCommandCombos() {
        return freqCommandCombos;
    }

    /**
     * Populate freqCommandCombos list with sequences from command history list.
     * Command sequences are composed by a number of sequential strings from commandHistory list,
     * and tries to find sequences that exist multiple times throughout the commandHistory list.
     * <p>
     * A combination of commands might look like (git add, git commit, git push). If those three elements are found in
     * that order elsewhere on the commandHistory list, they will be treated as a command combination, and suggested
     * at a later time based on user input.
     *
     * @param elementsPerCombo Elements per combination (e.g. command pair, etc...)
     */
    private void populateList(int elementsPerCombo) {
        ArrayList<CommandSeq> freqCommandCombos = new ArrayList<>(this.freqCommandCombos);
        if (elementsPerCombo == 2) {
            /*
             * Load all pairs of commands to list
             */
            for (int i = 1; i < commandHistory.size(); i++) {
                if (!commandHistory.get(i - 1).trim().equals(commandHistory.get(i).trim()))
                    this.freqCommandCombos.add(
                            new CommandSeq(i - 1, commandHistory.get(i - 1), commandHistory.get(i).trim())
                    );
            }
        } else {
            /*
             * For all command combinations not deleted in removeInfrequentCombos call, find all instances of the
             * non removed command combinations and add the command that comes after each combination in the history
             * file to a copy of the non removed command combination.
             */
            boolean match; // True if an instance of mcf is found while searching commandHistory
            String[] cmd; // For checking against commandHistory

            for (CommandSeq mcf : freqCommandCombos) {
                cmd = mcf.getCommand().split(" && ");
                if (mcf.size() + 1 == elementsPerCombo) {
                    for (int cmdHisIndex = 0; cmdHisIndex < commandHistory.size(); cmdHisIndex++) {
                        if (cmdHisIndex + mcf.size() >= commandHistory.size())
                            continue;

                        match = true;
                        for (int a = 0; a < mcf.size(); a++) {
                            if (!cmd[a].trim().equals(commandHistory.get(cmdHisIndex + a).trim()))
                                match = false;
                        }

                        if (match) {
                            CommandSeq mcfNew = new CommandSeq(cmdHisIndex);
                            for (int i = cmdHisIndex; i < cmdHisIndex + elementsPerCombo; i++)
                                mcfNew.add(commandHistory.get(i));
                            this.freqCommandCombos.add(mcfNew);
                        }
                    }
                }
            }
        }

        if (removeInfrequentCombos(elementsPerCombo) && elementsPerCombo < COMMAND_COMBO_LIMIT)
            populateList(elementsPerCombo + 1);
    }

    /**
     * Removes duplicate objects from freqCommandCombos list, and keeps tabs on the frequency of each
     * command combination.
     *
     * @param elementsPerCombo Elements in each combination of commands for this iteration
     * @return True if recurring sequences were found
     */
    private boolean removeInfrequentCombos(int elementsPerCombo) {

        ArrayList<CommandSeq> freqCommandCombos = new ArrayList<>(this.freqCommandCombos);

        // Initial size of freqCommandCombos list, used to determine if any objects were removed at the end
        final int INIT_SIZE = freqCommandCombos.size();

        // Size of the list, changes as objects get removed
        int listSize = freqCommandCombos.size();

        // Frequency of a combination of strings in freqCommandCombos
        int freq = 0;

        // Only increase counter if no object was removed, since list is dynamically shrinking
        for (int i = 0; i < listSize; i += (freq == 1 ? 0 : 1)) {
            CommandSeq mcf = freqCommandCombos.get(i);

            // Skip sequences that do not contain the same number of command combinations
            if (mcf.size() != elementsPerCombo && !mcf.isRecurring()) {
                freqCommandCombos.remove(mcf);
                listSize--;
                continue;
            } else if (mcf.size() != elementsPerCombo)
                continue;

            freq = removeDuplicatesOf(mcf, freqCommandCombos, listSize);
            listSize = freqCommandCombos.size();

            if (freq > MIN_COMMAND_FREQ) {
                mcf.setFreq(freq);
                mcf.setRecurring();
            } else {
                freqCommandCombos.remove(mcf);
                listSize--;
            }
        }

        this.freqCommandCombos = new ArrayList<>(freqCommandCombos);

        return INIT_SIZE != freqCommandCombos.size();
    }

    /**
     * Removes duplicates of a CommandSeq object, and keeps tabs on how many instances it has found.
     *
     * @param mcf               CommandSeq object to remove duplicates of
     * @param freqCommandCombos List to remove CommandSeq objects from
     * @param listSize          Current size of freqCommandCombos list
     * @return Number of instances of CommandSeq command in freqCommandCombos list
     */
    private int removeDuplicatesOf(CommandSeq mcf, ArrayList<CommandSeq> freqCommandCombos, int listSize) {

        int freq = 1;

        // Only increase counter if no object was removed, otherwise skipping one each time
        for (int i = 0; i < listSize; ) {
            CommandSeq mcf1 = freqCommandCombos.get(i);

            // Removes all duplicates of mcf
            if (mcf.isSameAs(mcf1) && !mcf.equals(mcf1)) {
                freq++;
                freqCommandCombos.remove(mcf1);
                listSize--;
            } else
                i++;
        }

        return freq;
    }
}
