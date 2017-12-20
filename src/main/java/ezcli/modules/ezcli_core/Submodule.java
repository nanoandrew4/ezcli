package ezcli.modules.ezcli_core;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

public abstract class Submodule {

    private Module parent;

    HashMap<Character, Method> methods;

    public Submodule(Module parent, String name) {
        this.parent = parent;
        methods = new HashMap<>();

        try {
            initSubmodule(name);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private void initSubmodule(String submoduleName) throws ClassNotFoundException, NoSuchMethodException {
        List<String> file;
        try {
            file = Files.readAllLines(Paths.get("submodules/" + submoduleName + ".smod"));
        } catch (IOException e) {
            System.err.println("Submodule \"" + submoduleName + "\" could not be loaded");
            return;
        }

        // remove spaces from sArr
        for (String s : file) {
            String[] sArr = s.split(" ");
            if (sArr.length >= 4 && "bind".equals(sArr[0])) {
                Class<?> c = Class.forName(sArr[2]);
                Method m;

                if (sArr.length <= 4)
                    m = c.getMethod(sArr[3]);
                else {
                    Class<?>[] params = new Class[sArr.length - 4];
                    for (int i = 4; i < sArr.length; i++)
                        params[i - 4] = Class.forName(sArr[i]);
                    m = c.getMethod(sArr[3], params);
                }
                if ("all".equals(sArr[1])) {
                    for (int i = 32; i < 126; i++)
                        methods.put((char) i, m);
                    methods.put('\t', m);
                    methods.put('\n', m);
                    methods.put('\b', m);
                } else
                    methods.put(sArr[1].charAt(0), m);
            }
        }
    }
}