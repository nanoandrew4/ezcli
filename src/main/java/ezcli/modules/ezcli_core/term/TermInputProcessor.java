package ezcli.modules.ezcli_core.term;

import ezcli.modules.ezcli_core.Ezcli;
import ezcli.modules.ezcli_core.global_io.*;
import ezcli.modules.ezcli_core.util.FileAutocomplete;

import java.util.ArrayList;

/**
 * input processor for terminal module.
 *
 * @see Terminal
 * @see TermKeyProcessor
 * @see TermArrowKeyProcessor
 */
public class TermInputProcessor extends InputHandler {


    private ArrayList<String> prevCommands = new ArrayList<>(); // stores all entered commands

    private String command = "";

    // Stops autocomplete from reprinting the text it completed if tab is pressed consecutively at the end of a complete file name
    private boolean lockTab = false;

    // Stops autocomplete from constantly erasing fileNames list when searching sub-directories
    private boolean blockClear = false;

    // for use in detecting arrow presses on Unix, see comment block near usage in Process()
    private long lastPress = System.currentTimeMillis();

    TermInputProcessor(Terminal terminal) {
        super(null, null); // pass null because cannot pass "this" before super() is called
        keyHandler = new TermKeyProcessor(terminal, this); // create straight after, passing "this"
        arrowKeyHandler = new TermArrowKeyProcessor(this); // create straight after, passing "this"
        KeyHandler.initKeysMap();
    }

    public TermKeyProcessor getKeyProcessor() {
        return (TermKeyProcessor) keyHandler;
    }

    public TermArrowKeyProcessor getArrowKeyProcessor() {
        return (TermArrowKeyProcessor) arrowKeyHandler;
    }

    protected ArrayList<String> getPrevCommands() {
        return prevCommands;
    }

    protected void setPrevCommands(ArrayList<String> prevCommands) {
        this.prevCommands = prevCommands;
    }

    protected String getCommand() {
        return command;
    }

    protected void setCommand(String command) {
        this.command = command;
    }

    protected KeyHandler getKeyHandler() {
        return keyHandler;
    }

    public ArrowKeyHandler getArrowKeyHandler() {
        return arrowKeyHandler;
    }

    protected void setLockTab(boolean lockTab) {
        this.lockTab = lockTab;
    }

    protected void setBlockClear(boolean blockClear) {
        this.blockClear = blockClear;
    }

    /**
     * Calls appropriate method for handling
     * input read from the input class, using
     * booleans in Ezcli class to determine
     * what OS the program is running on.
     * <br></br>
     */
    @Override
    public void process(int input) {

        // Process input for Windows systems
        if (Ezcli.isWin) {
            // If returns anything but ArrowKeys.NONE or ArrowKeys.MOD check keys
            ArrowKeys ak = arrowKeyHandler.process(ArrowKeyHandler.arrowKeyCheckWindows(input));
            if (ak != ArrowKeys.NONE && ak != ArrowKeys.MOD)
                keyHandler.process(input);
        } else if (Ezcli.isUnix) {
            ArrowKeys ak = ArrowKeyHandler.arrowKeyCheckUnix(input);
            if (ak != ArrowKeys.NONE && ak != ArrowKeys.MOD)
                arrowKeyHandler.process(ak);
            if (System.currentTimeMillis() - lastPress > 10 && input != 27)
                keyHandler.process(input);
        }

        lastPress = System.currentTimeMillis();
    }

    protected void fileAutocomplete(String currText) {
        if (!FileAutocomplete.isAvailable()) // if file autocompleting is currently in use, do not start new process
            return;

        // Autocomplete
        FileAutocomplete.init(command, currText, blockClear, lockTab);

        // Get variables from autocomplete method
        command = FileAutocomplete.getCommand();
        blockClear = FileAutocomplete.isBlockClear();
        lockTab = FileAutocomplete.isLockTab();
    }
}

