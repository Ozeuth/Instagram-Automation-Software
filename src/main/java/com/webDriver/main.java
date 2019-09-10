package com.webDriver;

public class main {

    public static void main(String[] args) {
        // arg1: user, arg2: password, arg3: tags, arg4: 1/2/3 (1 like, 2 comment, 3 follow)
        boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");

        if (args.length != 4) {
            new ui(isMac).go();
        }

        botTest bot = new botTest(isMac, args);
    }
}
