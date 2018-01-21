package ezcli.modules.ezcli_core.global_io.output;

/**
 * Standard output for ezcli, passes all output on to by displayed by
 * System.out.
 */
public class ConsoleOutput implements Output {
    @Override
    public void print(char c, String type) {
        System.out.println(c);
    }

    @Override
    public void print(int i, String type) {
        System.out.print(i);
    }

    @Override
    public void print(boolean b, String type) {
        System.out.print(b);
    }

    @Override
    public void print(long l, String type) {
        System.out.print(l);
    }

    @Override
    public void print(float f, String type) {
        System.out.print(f);
    }

    @Override
    public void print(double d, String type) {
        System.out.print(d);
    }

    @Override
    public void print(char[] c, String type) {
        System.out.print(c);
    }

    @Override
    public void print(String s, String type) {
        System.out.print(s);
    }

    @Override
    public void println(char c, String type) {
        System.out.println(c);
    }

    @Override
    public void println(int i, String type) {
        System.out.println(i);
    }

    @Override
    public void println(boolean b, String type) {
        System.out.println(b);
    }

    @Override
    public void println(long l, String type) {
        System.out.println(l);
    }

    @Override
    public void println(float f, String type) {
        System.out.println(f);
    }

    @Override
    public void println(double d, String type) {
        System.out.println(d);
    }

    @Override
    public void println(char[] c, String type) {
        System.out.println(c);
    }

    @Override
    public void println(String s, String type) {
        System.out.println(s);
    }

    @Override
    public void println() {
        System.out.println();
    }
}
