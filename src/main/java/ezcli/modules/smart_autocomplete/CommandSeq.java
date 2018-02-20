package ezcli.modules.smart_autocomplete;

import java.util.Arrays;
import java.util.LinkedList;

public class CommandSeq {

    // Sequence of commands this class represents (only used by MultiCmdComplete class)
    private LinkedList<String> commandSeq;

    private LinkedList<Integer> locations;

    // Frequency of this command or sequence of commands in commandHistory list
    private int freq;

    CommandSeq(int startIndex, String command) {
        commandSeq = new LinkedList<>();
        locations = new LinkedList<>();

        commandSeq.add(command);
        locations.add(startIndex);
        freq = 1;
    }

    CommandSeq(int startIndex, String... commands) {
        commandSeq = new LinkedList<>(Arrays.asList(commands));
        locations = new LinkedList<>();

        locations.add(startIndex);
        freq = 1;
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

    public void add(String cmd) {
        commandSeq.add(cmd);
    }

    protected void incrementFreq() {
        freq++;
    }

    public int getFreq() {
        return freq;
    }

    protected void setFreq(int freq) {
        this.freq = freq;
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

    protected boolean isSameAs(String[] cs) {
        if (this.commandSeq.size() != cs.length)
            return false;

        for (int i = 0; i < this.commandSeq.size(); i++)
            if (!this.commandSeq.get(i).trim().equals(cs[i].trim()))
                return false;
        return true;
    }
}
