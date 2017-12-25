package ezcli.modules.ezcli_core;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public abstract class Submodules {

    private static HashMap<Character, LinkedList<MethodObj>> methods = new HashMap<>();

    public abstract void init();

    protected static void initSubmodules() {
        File submoduleFolder = new File("/home/nanoandrew4/IdeaProjects/ezcli/submodules/");
        File[] files = submoduleFolder.listFiles();

        if (files == null)
            return;

        for (File f : files) {
            if (f.isFile()) {
                try {
                    initSubmodule(f.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                    //System.err.println("Failed to initialize submodule: " + f.getName());
                }
            }
        }
    }

    private static void initSubmodule(String submoduleFileName) throws Exception {
        List<String> file;
        try {
            System.out.println(submoduleFileName);
            file = Files.readAllLines(
                    Paths.get("/home/nanoandrew4/IdeaProjects/ezcli/submodules/" + submoduleFileName));
        } catch (IOException e) {
            System.err.println("Submodule \"" + submoduleFileName + "\" could not be loaded");
            return;
        }

        boolean inMethod = false;
        boolean inSubmodule = false;
        String submoduleName = "";
        String methodName = "";
        Character [] binds = null;
        LinkedList<String> params = new LinkedList<>();
        LinkedList<String> initParams = new LinkedList<>();

        // remove spaces from sArr
        for (String s : file) {
            s = s.trim();
            if (s.startsWith("#"))
                continue;

            if (s.startsWith("_")) {
                if (!inSubmodule) {
                    inSubmodule = true;
                    submoduleName = s.substring(1);
                } else {
                    inMethod = true;
                    methodName = s.substring(1);
                }
            } else if (s.endsWith("_")) {
                if (inMethod) {
                    String[] split = methodName.split("-");
                    MethodObj obj = new MethodObj(split[0], split[1], params);
                    for (char c : binds) {
                        LinkedList<MethodObj> list = methods.computeIfAbsent(c, k -> new LinkedList<>());
                        list.add(obj);
                    }

                    params.clear();
                    binds = null;
                    inMethod = false;
                    methodName = "";
                } else {
                    MethodObj obj = new MethodObj(submoduleName, "init", null);
                    obj.invoke();
                    initParams.clear();
                    binds = null;
                    inSubmodule = false;
                }
            }

            if (s.startsWith("params")) {
                String[] split = s.split(" ");
                for (int i = 1; i < split.length; i++) {
                    if (split[i].startsWith("#"))
                        break;
                    else if (inMethod)
                        params.add(split[i]);
                    else if (inSubmodule)
                        initParams.add(split[i]);
                }
            }

            if (s.startsWith("bind")) {
                String requestedBinds = s.split(" ")[1];
                LinkedList<Character> chars = new LinkedList<>();

                if ("all".equals(requestedBinds)) {
                    for (int i = 30; i < 126; i++)
                        chars.add((char)i);
                    chars.add('\t');
                    chars.add('\n');
                    chars.add('\b');

                    binds = Arrays.copyOf(chars.toArray(), chars.size(), Character[].class);
                } else {
                    for (int i = 0; i < requestedBinds.length(); i++) {
                        if (requestedBinds.charAt(i) != '\\') // no support for '\t' and other special characters
                            chars.add(requestedBinds.charAt(i));
                    }
                }
            }
        }
    }

    public static void charEvent(char c) {

        LinkedList<MethodObj> list = methods.get(c);
        if (list == null)
            return;

        for (MethodObj m : list)
            m.invoke();
    }
}

class MethodObj {

    private String className;
    private String methodName;
    private LinkedList<String> params;

    MethodObj(String className, String methodName, LinkedList<String> params) {
        this.className = className;
        this.methodName = methodName;
        this.params = params;
    }

    public String getMethodName() {
        return methodName;
    }

    public LinkedList<String> getParams() {
        return params;
    }

    public void invoke() {
        try {
            LinkedList<Class<?>> classes = new LinkedList<>();
            LinkedList<Object> currentParams = new LinkedList<>();

            if (params != null) {
                for (String s : params) {
                    String[] arr = s.split("-");
                    classes.add(Class.forName(arr[0]).getDeclaredMethod("get" + arr[1]).getReturnType());
                    currentParams.add(Class.forName(arr[0]).getDeclaredMethod("get" + arr[1]).invoke(null));
                }
            }

            Method m = Class.forName("ezcli.submodules." + className).getDeclaredMethod(methodName, (Class[]) classes.toArray());

            m.invoke(null, currentParams.toArray());
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}