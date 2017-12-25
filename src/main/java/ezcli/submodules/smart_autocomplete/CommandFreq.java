package ezcli.submodules.smart_autocomplete;

import java.util.Arrays;
import java.util.LinkedList;

public class CommandFreq {

    private String command;

    // Sequence of commands this class represents (only used by MultiCmdComplete class)
    private LinkedList<String> commandSeq;

    // Starting index of sequence of commands in commandHistory list (for MultiCmdComplete class)
    private int startIndex = -1;

    // Frequency of this command or sequence of commands in commandHistory list
    private int freq = -1;

    CommandFreq(String command) {
        this.command = command;
        freq = 1;
    }

    CommandFreq(int startIndex, String... commands) {
        commandSeq = new LinkedList<>(Arrays.asList(commands));
        this.startIndex = startIndex;
    }

    public String getCommand() {
        return command;
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

    protected LinkedList<String> getCommandSeq() {
        return commandSeq;
    }

    /**
     * Compares two MultiCmdFreq objects.
     *
     * @param mcf MultiCmdFreq object to compare this object to
     * @return True if both objects represent the same sequence of strings
     */
    protected boolean isSameAs(CommandFreq mcf) {
        if (this.commandSeq.size() == mcf.commandSeq.size()) {
            for (int i = 0; i < this.commandSeq.size(); i++)
                if (!this.commandSeq.get(i).equals(mcf.commandSeq.get(i)))
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
    protected String getCommandSequence() {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < commandSeq.size(); i++)
            sb.append(commandSeq.get(i)).append((i < commandSeq.size() - 1) ? " && " : " ");

        return sb.toString();
    }
}
