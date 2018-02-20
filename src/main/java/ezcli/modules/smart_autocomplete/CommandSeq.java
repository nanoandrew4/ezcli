package ezcli.modules.smart_autocomplete;

import java.util.Arrays;
import java.util.LinkedList;

public class CommandSeq {

    /*
     * List of commands that make up this sequence
     */
    private LinkedList<String> commandSeq;

    /*
     * List of locations this sequence can be found at in the commandHistory file (in MultiCmcComplete,
     * SmartAutocomplete has no need for the locations)
     * These locations are used to generate longer sequences of commands in MultiCmdComplete. In order to simplify
     * analysis, all occurrences of the same sequence in the commandHistory list are condensed and the positions stored
     * here.
     */
    private LinkedList<Integer> locations;

    // Frequency of this command or sequence of commands in commandHistory list
    private int freq = 1;

    CommandSeq(int startIndex, String command) {
        commandSeq = new LinkedList<>();
        locations = new LinkedList<>();

        commandSeq.add(command);
        locations.add(startIndex);
    }

    CommandSeq(int startIndex, String... commands) {
        commandSeq = new LinkedList<>(Arrays.asList(commands));
        locations = new LinkedList<>();

        locations.add(startIndex);
    }

    protected LinkedList<String> getCommandSeq() {
        return commandSeq;
    }

    public LinkedList<Integer> getLocations() {
        return locations;
    }

    public Integer getLocation(int position) {
        return locations.get(position);
    }

    protected void incrementFreq() {
        freq++;
    }

    public int getFreq() {
        return freq;
    }

    public int getSize() {
        return commandSeq.size();
    }

    /**
     * Returns sequence of strings this object represents.
     *
     * @return Sequence of strings represented by this object with double ampersands between each string
     */
    protected String getCommand() {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < commandSeq.size(); i++)
            sb.append(commandSeq.get(i)).append((i < commandSeq.size() - 1) ? " && " : "");

        return sb.toString();
    }

    /**
     * Compares two CommandSeq objects.
     *
     * @param cs MultiCmdFreq object to compare this object to
     * @return True if both objects represent the same sequence of strings
     */
    protected boolean isSameAs(CommandSeq cs) {
        if (this.commandSeq.size() != cs.commandSeq.size())
            return false;

        for (int i = 0; i < this.commandSeq.size(); i++)
            if (!this.commandSeq.get(i).trim().equals(cs.commandSeq.get(i).trim()))
                return false;
        return true;
    }

    /**
     * Compares this CommandSeq instance to an array of strings.
     *
     * @param cs Array of strings to compare this instance of CommandSeq to
     * @return True if both this instance and the array represent the same sequence, false otherwise
     */
    protected boolean isSameAs(String... cs) {
        if (this.commandSeq.size() != cs.length)
            return false;

        for (int i = 0; i < this.commandSeq.size(); i++)
            if (!this.commandSeq.get(i).trim().equals(cs[i].trim()))
                return false;
        return true;
    }
}
