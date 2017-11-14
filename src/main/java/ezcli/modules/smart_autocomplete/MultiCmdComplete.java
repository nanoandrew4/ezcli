package ezcli.modules.smart_autocomplete;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class MultiCmdComplete {

    // List containing all previously typed commands
    private List<String> commandHistory;

    // List containing all command sequences
    private LinkedList<MultiCmdFreq> commandSequences = new LinkedList<>();

    /**
     * Init class using command history file.
     *
     * @param pathToHistoryFile Path to command history file
     */
    MultiCmdComplete(String pathToHistoryFile) {

        try {
            commandHistory = Files.readAllLines(Paths.get(pathToHistoryFile));
        } catch (IOException e) {
            System.out.println("Error loading history file to MultiCmdCommand module");
            return;
        }

        populateList(2);
    }

    public LinkedList<MultiCmdFreq> getCommandSequences() {
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
        LinkedList<MultiCmdFreq> commandSequences = new LinkedList<>(this.commandSequences);
        if (elementsPerSeq == 2) {
            // Load all pairs of commands to list
            for (int i = 1; i < commandHistory.size(); i++) {
                this.commandSequences.add(
                        new MultiCmdFreq(i - 1, commandHistory.get(i - 1), commandHistory.get(i))
                );
            }
        } else {
            /*
             * For all command sequences not deleted in removeNonDuplicates call, increment
             * sequence by one and attempt to find sequences that match it.
             */
            for (MultiCmdFreq mcf : commandSequences) {
                if (mcf.getCommandSeq().size() + 1 == elementsPerSeq) {
                    MultiCmdFreq mcfNew = new MultiCmdFreq(mcf.getStartIndex());
                    mcfNew.setFreq(1);
                    if (mcfNew.getStartIndex() + elementsPerSeq <= commandHistory.size())
                        for (int i = mcfNew.getStartIndex(); i < mcfNew.getStartIndex() + elementsPerSeq; i++)
                            mcfNew.getCommandSeq().add(commandHistory.get(i));
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

        LinkedList<MultiCmdFreq> commandSequences = new LinkedList<>(this.commandSequences);

        boolean duplicates = false;

        for (MultiCmdFreq mcf : commandSequences) {

            if (mcf.getCommandSeq().size() != elementsPerSeq)
                continue;

            boolean delete = true;
            int freq = 1;

            for (MultiCmdFreq mcf1 : commandSequences) {
                if (mcf.isSameAs(mcf1) && !mcf.equals(mcf1)) {
                    freq++;
                    delete = false;
                    duplicates = true;
                } else if (!delete && mcf.isSameAs(mcf1) && !mcf.equals(mcf1)) {
                    freq++;
                    this.commandSequences.remove(mcf1);
                }
            }

            if (delete || freq < 5) {
                this.commandSequences.remove(mcf);
            }

            mcf.setFreq(freq);

            commandSequences = new LinkedList<>(this.commandSequences);
        }

        return duplicates;
    }
}
