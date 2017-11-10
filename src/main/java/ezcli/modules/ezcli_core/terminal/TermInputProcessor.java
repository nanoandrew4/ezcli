package ezcli.modules.ezcli_core.terminal;

import ezcli.modules.ezcli_core.Ezcli;
import ezcli.modules.ezcli_core.global_io.*;
import ezcli.modules.ezcli_core.util.FileAutocomplete;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Input processor for terminal module.
 *
 * @see Terminal
 * @see TermKeyProcessor
 * @see TermArrowKeyProcessor
 */
public class TermInputProcessor extends InputHandler {

    private Terminal terminal;

    // Stores all entered commands
    private ArrayList<String> prevCommands = new ArrayList<>();

    private String command = "";

    // Stops autocomplete from reprinting command it completed if tab is pressed at the end of a complete file name
    private boolean lockTab = false;

    // Stops autocomplete from constantly erasing fileNames list when searching sub-directories
    private boolean blockClear = false;

    // For resetting all variables in FileAutocomplete once a key press other than a tab is registered
    private boolean resetVars = false;

    // for use in detecting arrow presses on Unix, see comment block near usage in Process()
    private long lastPress = System.currentTimeMillis();

    private int cursorPos = 0;

    TermInputProcessor(Terminal terminal) {
        super();
        this.terminal = terminal;
        keyHandler = new TermKeyProcessor(this);
        arrowKeyHandler = new TermArrowKeyProcessor(this);
        KeyHandler.initKeysMap();
    }

    protected TermKeyProcessor getKeyProcessor() {
        return (TermKeyProcessor) keyHandler;
    }

    protected TermArrowKeyProcessor getArrowKeyProcessor() {
        return (TermArrowKeyProcessor) arrowKeyHandler;
    }

    protected ArrayList<String> getPrevCommands() {
        return prevCommands;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    protected void setLockTab(boolean lockTab) {
        this.lockTab = lockTab;
    }

    protected void setBlockClear(boolean blockClear) {
        this.blockClear = blockClear;
    }

    protected void setResetVars(boolean resetVars) {
        this.resetVars = resetVars;
    }

    protected void increaseCursorPos() {
        cursorPos++;
    }

    protected void decreaseCursorPos() {
        cursorPos--;
    }

    protected int getCursorPos() {
        return cursorPos;
    }

    protected void setCursorPos(int cursorPos) {
        this.cursorPos = cursorPos;
    }

    /**
     * Calls appropriate method for handling input read from the input class.
     */
    @Override
    public void process(int input) {

        if (Ezcli.isWin) {
            ArrowKeys ak = arrowKeyHandler.process(ArrowKeyHandler.arrowKeyCheckWindows(input));
            if (ak != ArrowKeys.NONE)
                arrowKeyHandler.process(ak);
            if (ak != ArrowKeys.NONE)
                keyHandler.process(input);
        } else if (Ezcli.isUnix) {
            ArrowKeys ak = ArrowKeyHandler.arrowKeyCheckUnix(input);
            if (ak != ArrowKeys.NONE)
                arrowKeyHandler.process(ak);
            if (System.currentTimeMillis() - lastPress > 10 && input != 27)
                keyHandler.process(input);
        }

        lastPress = System.currentTimeMillis();
    }

    /**
     * Sends command to terminal class for parsing, source is the newlineEvent in the key processor
     */
    protected void parse() {
        terminal.parse(command);
    }

    /**
     * Moves the cursor from the end of the command to where it should be (if the user is using arrow keys)
     * Usually only used after modifying 'command'
     */
    protected void moveToCursorPos() {
        for (int i = command.length(); i > cursorPos; i--)
            System.out.print("\b");
    }

    /**
     * Autocompletes desired file name similar to how terminals do it.
     */
    protected void fileAutocomplete() {

        if (FileAutocomplete.getFiles() == null) {
            FileAutocomplete.init(disassembleCommand(command), blockClear, lockTab);
            resetVars = false;
        } else
            FileAutocomplete.fileAutocomplete();

        command = FileAutocomplete.getCommand();

        if (FileAutocomplete.isResetVars() || resetVars)
            FileAutocomplete.resetVars();

        // Get variables and set cursor position
        blockClear = FileAutocomplete.isBlockClear();
        lockTab = FileAutocomplete.isLockTab();
        setCursorPos(command.length());
    }

    /**
     * Splits a command into 3 parts for the autocomplete function to operate properly.
     * <br></br><br></br>
     * Elements 0 and 2 are the non-relevant part of the command to the autocomplete function
     * and are used when stitching the autocompleted command back together.
     * <br></br><br></br>
     * Element 1 is the portion of the command that needs completing, and the one on which
     * the autocomplete class will operate on.
     *
     * @param command Command to split
     * @return Returns disassembled string, with non relevant info in elements 0 and 2, and the string to autocomplete
     *         in element 1.
     */
    protected String[] disassembleCommand(String command) {

        if (!command.contains("&&"))
            return new String[]{"", command, ""};

        LinkedList<Integer> ampPos = new LinkedList<>();
        for (int i = 0; i < command.length() - 1; i++) {
            if (command.substring(i, i + 2).equals("&&")) {
                ampPos.add(i);
                if (cursorPos - i < 2 && cursorPos - i > 0)
                    return new String[]{"", command, ""};
            }
        }

        String[] splitCommand = new String[3];

        if (ampPos.size() > 1) {
            for (int i = 0; i < ampPos.size(); i++) {
                if (ampPos.get(i) > cursorPos) {
                    splitCommand[0] = command.substring(0, ampPos.get(i - 1) + 2) + " ";
                    splitCommand[1] = command.substring(ampPos.get(i - 1) + 2, cursorPos);
                    splitCommand[2] = " " + command.substring(cursorPos, command.length());
                } else if (i + 1 == ampPos.size()) {
                    splitCommand[0] = command.substring(0, ampPos.get(i) + 2) + " ";
                    splitCommand[1] = command.substring(ampPos.get(i) + 2, cursorPos);
                    splitCommand[2] = " " + command.substring(cursorPos, command.length());
                }
            }
        } else {
            if (cursorPos > ampPos.get(0)) {
                splitCommand[0] = command.substring(0, ampPos.get(0) + 2) + " ";
                splitCommand[1] = command.substring(ampPos.get(0) + 2, cursorPos);
                splitCommand[2] = command.substring(cursorPos, command.length());
            } else if (cursorPos < ampPos.get(0)){
                splitCommand[0] = "";
                splitCommand[1] = command.substring(0, cursorPos);
                splitCommand[2] = command.substring(cursorPos, command.length());
            } else {
                String[] split = command.split("&&");
                splitCommand[0] = split[0];
                splitCommand[1] = "";
                splitCommand[2] = "&&" + split[1];
            }
        }

        splitCommand[1] = Terminal.removeSpaces(splitCommand[1]);

        return splitCommand;
    }
}

