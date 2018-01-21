package ezcli.modules.ezcli_core.global_io.output;

/**
 * Interface for program output. Allows anyone to write their own
 * output system, and put it to use easily by assigning the implementation
 * to Ezcli.ezcliOutput, which is the standard output object for the whole program.
 *
 * @see ezcli.modules.ezcli_core.Ezcli
 */
public interface Output {

    void print(char c, String type);
    
    void print(int i, String type);
    
    void print(boolean b, String type);
    
    void print(long l, String type);
    
    void print(float f, String type);
    
    void print(double d, String type);
    
    void print(char[] c, String type);
    
    void print(String s, String type);
    
    void println(char c, String type);
    
    void println(int i, String type);
    
    void println(boolean b, String type);
    
    void println(long l, String type);
    
    void println(float f, String type);
    
    void println(double d, String type);
    
    void println(char[] c, String type);
    
    void println(String s, String type);

    void println();
}
