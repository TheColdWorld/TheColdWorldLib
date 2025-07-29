package cn.thecoldworld.thecoldworldlib.exceptions;

import java.io.File;

public class InvalidFileException extends RuntimeException {
    public InvalidFileException(File file, String message) {
        super(String.format("read file '%s' failed,message:%s", file.getAbsoluteFile(), message));
    }

    public InvalidFileException(File file, Throwable superException) {
        super(String.format("read file '%s' failed because %s", file.getAbsoluteFile(), superException.getClass().getName()), superException);
    }
}
