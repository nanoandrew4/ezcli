package ezcli.modules.smart_autocomplete;

import java.util.ArrayList;
import java.util.List;

public class MultiCmdComplete {

    // List containing all previously typed commands
    private List<String> commandHistory;

    // List containing all command sequences
    private ArrayList<CommandFreq> commandSequences;

    /**
     * Init class using command history file.
     */
    MultiCmdComplete(List<String> commandHistory) {

        this.commandHistory = commandHistory;
        commandSequences = new ArrayList<>();

        populateList(2);

        SmartAutocomplete.sort(0, commandSequences.size() - 1, commandSequences);
    }

    public ArrayList<CommandFreq> getCommandSequences() {
        return commandSequences;
    }

    /**
     * Populate commandSequences list with sequences from command history list.
     * Command sequences are composed by a number of sequential strings from commandHistory list,
     * and tries to find sequences that exist multiple times throughout the commandHistory list.
     * <p>
     * A sequence of commands might look like (git add, git commit, git push). If those three elements are found in
     * that order elsewhere on the commandHistory list, they will be treated as a command sequence, and suggested
     * at a later time based on user input.
     *
     * @param elementsPerSeq Elements per sequence (e.g. command pair, etc...)
     */

    private void populateList(int elementsPerSeq) {
        ArrayList<CommandFreq> commandSequences = new ArrayList<>(this.commandSequences);
        if (elementsPerSeq == 2) {
            // Load all pairs of commands to list
            for (int i = 1; i < commandHistory.size(); i++) {
                if (!commandHistory.get(i - 1).trim().equals(commandHistory.get(i).trim()))
                    this.commandSequences.add(
                            new CommandFreq(i - 1, commandHistory.get(i - 1), commandHistory.get(i).trim())
                    );
            }
        } else {
            /*
             * For all command sequences not deleted in removeNonDuplicates call, increment
             * sequence by one and attempt to find sequences that match it.
             */
            for (CommandFreq mcf : commandSequences) {
                if (mcf.getCommandSeq().size() + 1 == elementsPerSeq) {
                    CommandFreq mcfNew = new CommandFreq(mcf.getStartIndex());
                    mcfNew.setFreq(1);
                    if (mcfNew.getStartIndex() + elementsPerSeq <= commandHistory.size())
                        for (int i = mcfNew.getStartIndex(); i < mcfNew.getStartIndex() + elementsPerSeq; i++)
                            if (!mcf.getCommandSeq().contains(commandHistory.get(i).trim()))
                                mcfNew.getCommandSeq().add(commandHistory.get(i).trim());
                    this.commandSequences.add(mcfNew);
                }
            }
        }

        if (removeNonDuplicates(elementsPerSeq))
            populateList(elementsPerSeq + 1);
    }

    /**
     * Removes duplicate objects from commandSequences list, and keeps tabs on the frequency of each
     * command sequence.
     *
     * @param elementsPerSeq Elements in each sequence of commands for this iteration
     * @return True if recurring sequences were found
     */
    private boolean removeNonDuplicates(int elementsPerSeq) {

        ArrayList<CommandFreq> commandSequences = new ArrayList<>(this.commandSequences);

        // Initial size of commandSequences list, used to determine if any objects were removed at the end
        final int INIT_SIZE = commandSequences.size();

        // Size of the list, changes as objects get removed
        int listSize = commandSequences.size();

        // Frequency of a sequence of strings in commandSequences
        int freq = -2;

        // Only increase counter if no object was removed, otherwise skipping one each time
        for (int i = 0; i < listSize; i += (freq == 1 ? 0 : 1)) {
            CommandFreq mcf = commandSequences.get(i);

            // Skip sequences that do not contain the same number of strings
            if (mcf.getCommandSeq().size() != elementsPerSeq)
                continue;

            freq = removeDuplicatesOf(mcf, commandSequences, listSize);
            listSize = commandSequences.size();

            if (freq < 5) {
                commandSequences.remove(mcf);
                listSize--;
            }
            else {
                mcf.setFreq(freq);
            }
        }

        this.commandSequences = new ArrayList<>(commandSequences);

        for (CommandFreq cf : this.commandSequences)
            System.out.println(cf.getCommandSequence() + ": " + cf.getFreq());

        return INIT_SIZE != commandSequences.size();
    }

    /**
     * Removes duplicates of a CommandFreq object, and keeps tabs on how many instances it has found.
     *
     * @param mcf CommandFreq object to remove duplicates of
     * @param commandSequences List to remove CommandFreq objects from
     * @param listSize Current size of commandSequences list
     * @return Number of instances of CommandFreq command in commandSequences list
     */
    private int removeDuplicatesOf(CommandFreq mcf, ArrayList<CommandFreq> commandSequences, int listSize) {
        boolean deletedLast;

        int freq = 1;

        // Only increase counter if no object was removed, otherwise skipping one each time
        for (int a = 0; a < listSize; a += (deletedLast ? 0 : 1)) {
            CommandFreq mcf1 = commandSequences.get(a);

            deletedLast = false;

            // Removes all duplicates of mcf
            if (mcf.isSameAs(mcf1) && !mcf.equals(mcf1)) {
                freq++;
                deletedLast = true;
                commandSequences.remove(mcf1);
                listSize--;
            }
        }

        return freq;
    }
}
