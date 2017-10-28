package ezcli.modules.ezcli_core.term;

import ezcli.modules.Module;

import java.io.IOException;
import java.util.Scanner;

public class Terminal extends Module {

    static boolean parse;
    static boolean exit;

    private TermInputProcessor inputProcessor;

    public Terminal() {
        super("TERMINAL");
        inputProcessor = new TermInputProcessor();
        setActiveModule();
    }

    @Override
    public void run() {
        while (!exit) {
            inputProcessor.process();
            if (parse)
                parse(TermInputProcessor.command);
        }
    }

    @Override
    public void parse(String command) {
        if (command.equalsIgnoreCase("exit")) {
            exit = true;
            return;
        }

        System.out.println(command);

        Runtime r = Runtime.getRuntime();
        Process p = null;
        try {
            p = r.exec(command);
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Parsing command failed");
        }

        if (p != null) {
            Scanner in = new Scanner(p.getInputStream());
            String input;
            while (p.isAlive() && (input = in.nextLine()) != null)
                System.out.println(input);
        }

        parse = false;
        TermInputProcessor.command = "";
    }

    @Override
    public void help() {

    }

    @Override
    public void tour() {

    }
}
