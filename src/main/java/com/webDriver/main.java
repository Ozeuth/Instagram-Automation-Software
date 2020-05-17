package com.webDriver;

public class main {

    public static void main(String[] args) {
        boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");
        new ui(isMac).go();
        bot bot = new bot(isMac, args);
    }
}
