package com.bayoumi.util;

import com.bayoumi.Launcher;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

/**
 * Helper methods
 */
public class Utility {

    public static void printAllRunningThreads() {
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        threadSet.forEach(thread -> System.out.println(thread.getName()));
    }

    public static String toUTF(String val) {
        return new String(val.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

    /**
     * this method is used in exiting the program
     */
    public static void exitProgramAction() {
//        printAllRunningThreads();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Long exitTime = System.currentTimeMillis();
            Logger.info("App closed - Used for "
                    + (exitTime - Launcher.startTime) + " ms\n");
        }));
        System.exit(0);
    }

    /**
     * copy text to Clipboard
     *
     * @param text to be copied
     */
    public static void copyToClipboard(String text) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(text), null);
    }

    /**
     * @param path where directory needed to be created
     */
    public static void createDirectory(String path) {
        try {
            Files.createDirectories(Paths.get(path));
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    /**
     * @param i number
     * @return number as String formatted as two digits
     */
    public static String formatIntToTwoDigit(int i) {
        return String.format("%02d", i);
    }

    public static double formatNum(double num) {
        double returnedVal = Double.parseDouble(String.format("%.2f", num));
        return Math.abs(returnedVal) == 0 ? 0 : returnedVal;
    }

}
