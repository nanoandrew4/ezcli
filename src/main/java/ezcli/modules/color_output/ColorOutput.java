package ezcli.modules.color_output;

public class ColorOutput {

    public final static String DEFAULT_COLOR = (char)27 + "[0m";
    public final static String PRETTY_BLUE = (char)27 + "[38;5;45m";

    private String[] customColors;

    private String selectedColor = DEFAULT_COLOR;
    private String prevColor = DEFAULT_COLOR;

    public ColorOutput() {

    }

    public ColorOutput(String... colors) {
        customColors = colors;
    }

    public void print(char c) {
        print(c, selectedColor);
    }

    public void print(char c, String color) {
        setColor(color);
        System.out.print(c);
        resetColor();
    }

    public void print(String s) {
        print(s, selectedColor);
    }

    public void print(String s, String color) {
        setColor(color);
        System.out.print(s);
        resetColor();
    }

    public void print(int i) {
        print(i, selectedColor);
    }

    public void print(int i, String color) {
        setColor(color);
        System.out.print(i);
        resetColor();
    }

    public void printLine(String s) {
        printLine(s, selectedColor);
    }

    public void printLine(String s, String color) {
        setColor(color);
        System.out.println(s);
        resetColor();
    }

    private void selectColor(String color) {
        prevColor = selectedColor;
        selectedColor = color;
    }

    private void selectCustomColor(int color) {
        prevColor = selectedColor;
        selectedColor = customColors[color];
    }

    private void selectDefaultColor() {
        prevColor = selectedColor;
        selectedColor = DEFAULT_COLOR;
    }

    private void setColor(String color) {
        System.out.print(color);
    }

    private void resetColor() {
        setColor(prevColor);
    }

    private void resetColor(String color) {
        setColor(color);
    }
}
