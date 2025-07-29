package cn.thecoldworld.thecoldworldlib.utils;

import java.io.File;
import java.io.IOException;

public final class FileUtil {
    private FileUtil() {
    }

    public static boolean RecreateFile(File file) {
        try {
            if (!file.exists() || !file.isFile()) return false;
            if (!file.delete()) return false;
            else return file.createNewFile();
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static boolean CreateFile(File file) throws IOException {
        if (file.exists()) return false;
        else if (!file.getParentFile().exists()) {
            return file.getParentFile().mkdirs() && file.createNewFile();
        } else {
            return file.createNewFile();
        }
    }
}
