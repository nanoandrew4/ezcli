package ezcli.modules.smart_autocomplete;

import java.util.Arrays;
import java.util.LinkedList;

public class CommandSeq {

    // Sequence of commands this class represents (only used by MultiCmdComplete class)
    private LinkedList<String> commandSeq;

    // Starting index of sequence of commands in commandHistory list (for MultiCmdComplete class)
    private int startIndex = -1;

    // Frequency of this command or sequence of commands in commandHistory list
    private int freq = -1;

    private boolean recurring = false;

    CommandSeq(String command) {
        commandSeq = new LinkedList<>();
        commandSeq.add(command);
        freq = 1;
    }

    CommandSeq(int startIndex, String... commands) {
        commandSeq = new LinkedList<>(Arrays.asList(commands));
        this.startIndex = startIndex;
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

    protected int getStartIndex() {
        return startIndex;
    }

    public void add(String cmd) {
        commandSeq.add(cmd);
    }

    public int size() {
        return commandSeq.size();
    }

    public void setRecurring() {
        recurring = true;
    }

    public boolean isRecurring() {
        return recurring;
    }

    /**
     * Compares two MultiCmdFreq objects.
     *
     * @param mcf MultiCmdFreq object to compare this object to
     * @return True if both objects represent the same sequence of strings
     */
    protected boolean isSameAs(CommandSeq mcf) {
        if (this.commandSeq.size() == mcf.commandSeq.size()) {
            for (int i = 0; i < this.commandSeq.size(); i++)
                if (!this.commandSeq.get(i).trim().equals(mcf.commandSeq.get(i).trim()))
                    return false;
        } else
            return false;

        return true;
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
}
