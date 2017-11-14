package ezcli.modules.smart_autocomplete;

import java.util.Arrays;
import java.util.LinkedList;

public class MultiCmdFreq {

    // Sequence of commands this class represents
    private LinkedList<String> commandSeq;

    // Starting index of sequence of commands in commandHistory list (in MultiCmdComplete class)
    private int startIndex = -1;

    // Frequency of this sequence of commands in commandHistory list (in MultiCmdComplete class)
    private int freq = -1;

    MultiCmdFreq(int startIndex, String... commands) {
        commandSeq = new LinkedList<>();
        commandSeq.addAll(Arrays.asList(commands));
        this.startIndex = startIndex;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public LinkedList<String> getCommandSeq() {
        return commandSeq;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    /**
     * Compares two MultiCmdFreq objects.
     *
     * @param mcf MultiCmdFreq object to compare this object to
     * @return True if both objects represent the same sequence of strings
     */
    public boolean isSameAs(MultiCmdFreq mcf) {
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
    public String getCommandSequence() {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < commandSeq.size(); i++)
            sb.append(commandSeq.get(i)).append((i < commandSeq.size() - 1) ? " && " : " ");

        return sb.toString();
    }
}
