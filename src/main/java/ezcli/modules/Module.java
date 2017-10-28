package ezcli.modules;

public abstract class Module {

    final String MODULE_NAME;
    static String activeModule;


    public Module(String MODULE_NAME) {
        this.MODULE_NAME = MODULE_NAME;
    }

    protected void setActiveModule() {
        activeModule = MODULE_NAME;
    }

    public abstract void run();

    public abstract void parse(String command);

    public abstract void help();

    public abstract void tour();

}
