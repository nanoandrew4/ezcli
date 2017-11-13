package ezcli.modules.smart_autocomplete;

public class CommandFreq {

    private String command;
    private int freq;

    CommandFreq(String command) {
        this.command = command;
        freq = 1;
    }

    public String getCommand() {
        return command;
    }

    public void incrementFreq() {
        freq++;
    }

    public int getFreq() {
        return freq;
    }
}
