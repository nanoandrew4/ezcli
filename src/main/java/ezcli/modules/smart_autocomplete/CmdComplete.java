package ezcli.modules.smart_autocomplete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CmdComplete {

    private ArrayList<CommandFreq> freqCommands = new ArrayList<>();

    CmdComplete(String pathToStoredCommands) {

    }

    public ArrayList<CommandFreq> getFreqCommands() {
        return freqCommands;
    }

    public void init(List<String> commands) {
        for (String s : commands)
            process(s);
    }

    public void process(String command) {
        store(command);
        compare(command);
    }

    private void store(String command) {
        boolean stored = false;
        for (CommandFreq cf : freqCommands) {
            if (cf.getCommand().equals(command)) {
                cf.incrementFreq();
                stored = true;
                break;
            }
        }

        if (!stored)
            freqCommands.add(new CommandFreq(command));
    }

    private void compare(String command) {
        String[] oCommand = command.split(" ");

        if (oCommand.length < 3)
            return;

        LinkedList<String> commonStrings = new LinkedList<>();
        HashMap<String, CommandFreq> freqPartialCommands = new HashMap<>();
        HashMap<String, LinkedList<String>> similarCommandsHashMap = new HashMap<>();

        for (CommandFreq cf : freqCommands) {
            int seqsInCommon = 0;
            String[] sCommand = cf.getCommand().split(" ");
            StringBuilder partsInCommon = new StringBuilder("");

            for (int i = 0; i < oCommand.length && i < sCommand.length; i++) {
                if (oCommand[i].equals(sCommand[i])) {
                    seqsInCommon++;
                    partsInCommon.append(sCommand[i]);
                }
            }

            double fitness = seqsInCommon / (double)sCommand.length;
            if (fitness > 0.7) {

                if (!commonStrings.contains(partsInCommon.toString()))
                    commonStrings.add(partsInCommon.toString());

                CommandFreq gcf = freqPartialCommands.get(partsInCommon.toString());
                if (gcf == null) {
                    freqPartialCommands.put(partsInCommon.toString(), new CommandFreq(partsInCommon.toString()));
                    LinkedList<String> newList = new LinkedList<>();
                    newList.add(cf.getCommand());
                    similarCommandsHashMap.put(partsInCommon.toString(), newList);
                } else {
                    gcf.incrementFreq();
                    LinkedList<String> simCommands = similarCommandsHashMap.get(partsInCommon.toString());
                    simCommands.add(cf.getCommand());
                }
            }
        }

        for (String s : commonStrings) {
            CommandFreq cf = freqPartialCommands.get(s);
            if (cf.getFreq() > 5) {
                LinkedList<String> similarCommands = similarCommandsHashMap.get(s);
                for (String simCmd : similarCommands) // vision of nightmare
                    for (CommandFreq cfs : freqCommands)
                        if (cfs.getCommand().startsWith(simCmd))
                            freqCommands.remove(cfs);
            }
        }
    }
}
