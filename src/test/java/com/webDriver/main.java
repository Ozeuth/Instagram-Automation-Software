package com.webDriver;

import javax.swing.*;

public class main {

    public static void main(String[] args) {
        boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");

        new ui(isMac).go();


        botTest bot = new botTest(isMac);
        bot.startWebDriver();
    }
}
