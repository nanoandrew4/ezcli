package ezcli.modules.smart_autocomplete;

import java.util.ArrayList;
import java.util.List;

public class MultiCmdComplete {

    // List containing all previously typed commands
    private ArrayList<String> commandHistory;

    private final static int COMMAND_SEQ_SIZE_LIMIT = 5;
    private final static int MIN_COMMAND_FREQ = 4;

    // List containing all command sequences
    private ArrayList<CommandSeq> freqCmdSeqs;

    /**
     * Init class using command history file.
     */
    MultiCmdComplete(List<String> commandHistory) {

        this.commandHistory = new ArrayList<>(commandHistory);
        freqCmdSeqs = new ArrayList<>();

        long start = System.currentTimeMillis();

        generateSequences();
        SmartAutocomplete.sort(0, freqCmdSeqs.size() - 1, freqCmdSeqs);

//        System.out.println("Total load time: " + (System.currentTimeMillis() - start) + "ms");
    }

    /**
     * Returns a sorted list, from most frequent to least frequent, of command sequences frequently used by the user.
     * This list is generated through analysis of the commandHistory list in TermInputProcessor. See generateSequences()
     * for more info on how the sequences are generated.
     * 
     * @return Sorted list, from most frequent to least frequent, of command sequences frequently used by the user
     */
    public ArrayList<CommandSeq> getFreqCmdSeqs() {
        return freqCmdSeqs;
    }

    /**
     * Controls the analysis and generation of the sequences of commands that will later be recommended to
     * the user. This method and the ones it calls all rely on the commandHistory list for the generation.
     */
    private void generateSequences() {
        /*
         * Instead of simply adding the commands to one list, they are divided based on how many components their
         * sequences have. This allows for faster comparisons, since elements are frequently checked against
         * the whole list, and the only relevant comparisons are those between elements with the same number of elements
         * in a sequence.
         *
         * Element 0 is an empty list. It holds nothing, since single command frequency is dealt with in
         * SmartAutocomplete
         */
        ArrayList<CommandSeq>[] tmpCmdSeqs = new ArrayList[COMMAND_SEQ_SIZE_LIMIT];

        for (int i = 0; i < tmpCmdSeqs.length; i++)
            tmpCmdSeqs[i] = new ArrayList<>();

        populateWithPairs(tmpCmdSeqs);

        // Start analyzing groups of 3 sequences, all the way up to and including COMMAND_SEQ_SIZE_LIMIT
        for (int cmdsInSeq = 2; cmdsInSeq < tmpCmdSeqs.length; cmdsInSeq++)
            generateLongerSequences(tmpCmdSeqs, cmdsInSeq);

        // Only add commands that meet the required frequency to the master list of frequent command sequences
        for (ArrayList<CommandSeq> tmpCmdSeq : tmpCmdSeqs)
            for (CommandSeq aTmpCmdSeq : tmpCmdSeq)
                if (aTmpCmdSeq.getFreq() >= MIN_COMMAND_FREQ)
                    freqCmdSeqs.add(aTmpCmdSeq);
    }

    /**
     * Populates the tmpCmdSeqs array with pairs of commands, which are generated from the commandHistory list.
     *
     * @param tmpCmdSeqs Array of ArrayLists to store all the pairs of commands in
     */
    private void populateWithPairs(ArrayList<CommandSeq>[] tmpCmdSeqs) {
        for (int i = 1; i < commandHistory.size(); i++) {
            // Generate array representing pair of commands
            String[] pair = {commandHistory.get(i - 1).trim(), commandHistory.get(i).trim()};
            boolean sequenceExists = false;

            /*
             * If a matching pair is found in the list, increment frequency counter and add the location it was found
             * at in the commandHistory list.
             */
            for (CommandSeq cs : tmpCmdSeqs[1]) {
                if (cs.isSameAs(pair)) {
                    sequenceExists = true;
                    cs.incrementFreq();
                    cs.getLocations().add(i - 1);
                    break;
                }
            }

            // If pair was not found, add it to the list
            if (!sequenceExists)
                tmpCmdSeqs[1].add(new CommandSeq(i - 1, pair[0], pair[1]));
        }
    }

    /**
     * For each occurrence of a sequence of commands in commandHistory that is frequently used, this method creates
     * a new sequence with the command that would follow the sequence in commandHistory list.
     *
     * @param tmpCmdSeqs Array of ArrayLists in which to store the resulting sequences. They will be stored in the
     *                   ArrayList array index corresponding to the number of commands in the sequence, minus one
     * @param cmdsInSeq Number of commands that composes the sequence minus one (to appease arrays)
     */
    private void generateLongerSequences(ArrayList<CommandSeq>[] tmpCmdSeqs, int cmdsInSeq) {
        for (int csIndex = 0; csIndex < tmpCmdSeqs[cmdsInSeq - 1].size(); csIndex++) {
            // Get the sequence of commands from the previous array index
            CommandSeq cs = tmpCmdSeqs[cmdsInSeq - 1].get(csIndex);

            // If the frequency of the sequence was not good last time around, it won't be any better this time
            if (cs.getFreq() < MIN_COMMAND_FREQ)
                continue;

            /*
             * CommandSeq stores a condensed version of a sequence of commands, that might be appear multiple times
             * in the commandHistory list. The following loop iterates through the various positions of the given
             * sequence, and adds the command that follows each individual sequence, to a new instance of CommandSeq,
             * if the new sequence was not found anywhere on the list. If the new sequence was found, simply increase
             * the frequency counter and add the location of that sequence in the commandHistory list to the list
             * in the CommandSeq instance.
             */
            for (int locPos = 0; locPos < cs.getLocations().size()
                    && cs.getLocation(locPos) + cmdsInSeq < commandHistory.size(); locPos++) {

                CommandSeq masterCS = null;

                String[] tmpCS = new String[cs.getSize() + 1];
                for (int i = 0; i < cmdsInSeq; i++)
                    tmpCS[i] = cs.getCommandSeq().get(i);

                tmpCS[tmpCS.length - 1] = commandHistory.get(cs.getLocation(locPos) + cs.getSize());
                for (CommandSeq cs1 : tmpCmdSeqs[cmdsInSeq]) {
                    if (cs1.isSameAs(tmpCS)) {
                        masterCS = cs1;
                        break;
                    }
                }

                if (masterCS != null) {
                    masterCS.incrementFreq();
                    masterCS.getLocations().add(cs.getLocation(locPos));
                } else
                    tmpCmdSeqs[cmdsInSeq].add(new CommandSeq(cs.getLocations().get(0), tmpCS));
            }
        }
    }
}