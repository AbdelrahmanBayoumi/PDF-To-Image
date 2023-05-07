package com.bayoumi.util;

import java.io.File;

public class FileUtils {
    public static String getFileExtension(File file) {
        if (file == null)
            return "";
        final String name = file.getName();
        final int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return name.substring(lastIndexOf).toLowerCase();
    }
}
