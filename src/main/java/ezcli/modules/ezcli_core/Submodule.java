package ezcli.modules.ezcli_core;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public abstract class Submodule {

    private static HashMap<Character, LinkedList<Method>> methods = new HashMap<>();

    protected static void init() {
        File submoduleFolder = new File("submodules/");
        File[] files = submoduleFolder.listFiles();

        if (files == null)
            return;

        for (File f : files) {
            try {
                initSubmodule(f.getName());
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    private static void initSubmodule(String submoduleName) throws ClassNotFoundException, NoSuchMethodException {
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
                LinkedList<Method> list;
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
                    for (int i = 32; i < 126; i++) {
                        list = methods.computeIfAbsent((char) i, k -> new LinkedList<>());
                        list.add(m);
                    }
                    char[] chars = {'\t', '\n', '\b'};
                    for (char ch : chars) {
                        list = methods.computeIfAbsent(ch, k -> new LinkedList<>());
                        list.add(m);
                    }
                } else {
                    list = methods.computeIfAbsent(sArr[1].charAt(0), k -> new LinkedList<>());
                    list.add(m);
                }
            }
        }
    }


}