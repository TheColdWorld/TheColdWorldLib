package cn.thecoldworld.thecoldworldlib.exceptions;

import cn.thecoldworld.thecoldworldlib.utils.FileUtil;
import cn.thecoldworld.thecoldworldlib.utils.IterUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.SystemDetails;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

public final class SerializedException {
    public static final Codec<SerializedException> CODEC = RecordCodecBuilder.create(exceptionInstance -> exceptionInstance.group(
            Codec.STRING.fieldOf("n").forGetter(e -> e.className),
            Codec.STRING.fieldOf("m").forGetter(e -> e.message),
            StackFrame.CODEC.listOf().fieldOf("s").forGetter(e -> e.stackTrace),
            Codecs.optional(SubException.CODEC).optionalFieldOf("c", Optional.empty()).forGetter(e -> Optional.ofNullable(e.cause)),
            Codec.STRING.listOf().fieldOf("nd").forGetter(e -> e.classNameDict),
            Codec.STRING.listOf().fieldOf("fd").forGetter(e -> e.fileNameDict),
            Codec.LONG.fieldOf("t").forGetter(e -> e.occurredTime),
            Codec.STRING.fieldOf("si").forGetter(e -> e.SystemDetailsString)
    ).apply(exceptionInstance, SerializedException::new));
    public final String className;
    public final String message;
    final List<StackFrame> stackTrace;
    final @Nullable SubException cause;
    final List<String> classNameDict;
    final List<String> fileNameDict;
    final long occurredTime;//Epoch Second
    final String SystemDetailsString;

    private SerializedException(final String className, final String message, final List<StackFrame> stackTrace, final Optional<SubException> cause, final List<String> classNameDict, final List<String> fileNameDict, long occurredTime, String systemDetailsString) {
        this.className = className;
        this.message = message;
        this.stackTrace = stackTrace;
        this.cause = cause.orElse(null);
        this.classNameDict = classNameDict;
        this.fileNameDict = fileNameDict;
        this.occurredTime = occurredTime;
        SystemDetailsString = systemDetailsString;
    }

    public SerializedException(Throwable exception, long occurredTime) {
        this.occurredTime = occurredTime;
        className = exception.getClass().getName();
        message = exception.getMessage();
        stackTrace = new ArrayList<>(exception.getStackTrace().length);
        classNameDict = new LinkedList<>();
        fileNameDict = new LinkedList<>();
        for (StackTraceElement stackTraceElement : exception.getStackTrace()) {
            if (stackTraceElement.isNativeMethod()) {
                stackTrace.add(new StackFrame(
                        IterUtil.indexofOrAdd(classNameDict, stackTraceElement.getClassName()),
                        IterUtil.indexofOrAdd(fileNameDict, "Native Method"),
                        stackTraceElement.getMethodName(),
                        stackTraceElement.getLineNumber()));
            } else stackTrace.add(new StackFrame(
                    IterUtil.indexofOrAdd(classNameDict, stackTraceElement.getClassName()),
                    IterUtil.indexofOrAdd(fileNameDict, Objects.requireNonNullElse(stackTraceElement.getFileName(), "Unknown Source")),
                    stackTraceElement.getMethodName(),
                    stackTraceElement.getLineNumber()));
        }
        cause = exception.getCause() == null
                ? null
                : new SubException(exception.getCause(), classNameDict, fileNameDict);
        StringBuilder sb = new StringBuilder(3072);
        new SystemDetails().writeTo(sb);
        SystemDetailsString = sb.toString();
    }


    public record StackFrame(int classIndex, int fileNameIndex, String methodName, int lineNumber) {
        public static final Codec<StackFrame> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("c").forGetter(StackFrame::classIndex),
                Codec.INT.fieldOf("f").forGetter(StackFrame::fileNameIndex),
                Codec.STRING.fieldOf("m").forGetter(StackFrame::methodName),
                Codec.INT.fieldOf("l").forGetter(StackFrame::lineNumber)
        ).apply(instance, StackFrame::new));
    }

    public static class SubException {
        public static final Codec<SubException> CODEC = Codec.recursive("SubException", selfCodec -> RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("n").forGetter(SubException::getClassName),
                Codec.STRING.fieldOf("m").forGetter(SubException::getMessage),
                StackFrame.CODEC.listOf().fieldOf("s").forGetter(SubException::getStackTrace),
                Codecs.optional(selfCodec).optionalFieldOf("c", Optional.empty()).forGetter(e -> Optional.ofNullable(e.cause))//selfCodec.optionalFieldOf("c", null).forGetter(e -> (e != null) ? e.getCause() : null)
        ).apply(instance, SubException::new)));
        final String className;
        final String message;
        final List<StackFrame> stackTrace;
        final @Nullable SubException cause;

        private SubException(final String className, final String message, final List<StackFrame> stackTrace, Optional<SubException> cause) {
            this.className = className;
            this.message = message;
            this.stackTrace = stackTrace;
            this.cause = cause.orElse(null);
        }

        public SubException(Throwable cause, final List<String> classNameDict, final List<String> fileNameDict) {
            className = cause.getClass().getName();
            message = cause.getMessage();
            stackTrace = new ArrayList<>(cause.getStackTrace().length);
            for (StackTraceElement stackTraceElement : cause.getStackTrace()) {
                if (stackTraceElement.isNativeMethod()) {
                    stackTrace.add(new StackFrame(
                            IterUtil.indexofOrAdd(classNameDict, stackTraceElement.getClassName()),
                            IterUtil.indexofOrAdd(fileNameDict, "Native Method"),
                            stackTraceElement.getMethodName(),
                            stackTraceElement.getLineNumber()));
                } else stackTrace.add(new StackFrame(
                        IterUtil.indexofOrAdd(classNameDict, stackTraceElement.getClassName()),
                        IterUtil.indexofOrAdd(fileNameDict, Objects.requireNonNullElse(stackTraceElement.getFileName(), "Unknown Source")),
                        stackTraceElement.getMethodName(),
                        stackTraceElement.getLineNumber()));
            }
            this.cause = cause.getCause() == null
                    ? null
                    : new SubException(cause.getCause(), classNameDict, fileNameDict);
        }

        public String getClassName() {
            return className;
        }

        public String getMessage() {
            return message;
        }

        public List<StackFrame> getStackTrace() {
            return stackTrace;
        }

        public @Nullable SubException getCause() {
            return cause;
        }
    }

    /**
     * @return "{@code className}:{@code message}"
     * @see #getAsString() detail exception report
     */
    @Override
    public String toString() {
        return String.format("%s:%s", className, message);
    }

    /**
     * write the serialized exception to a file
     *
     * @see #writeToFile(File, boolean) real implementation method
     */
    public void writeToFile(Path path) throws IOException {
        writeToFile(path, false);
    }

    /**
     * write the serialized exception to a file
     *
     * @see #writeToFile(File, boolean) real implementation method
     */
    public void writeToFile(Path path, boolean override) throws IOException {
        writeToFile(path.toFile(), override);
    }

    /**
     * write the serialized exception to a file
     *
     * @see #writeToFile(File, boolean) real implementation method
     */
    public void writeToFile(File file) throws IOException {
        writeToFile(file, false);
    }

    /**
     * write the serialized exception to a file
     *
     * @see #writeToFile(FileOutputStream) will input content
     */
    public void writeToFile(File file, boolean override) throws IOException {
        if (file.exists()) {
            if (file.isFile()) {
                if (override) {
                    if (!FileUtil.RecreateFile(file)) throw new InvalidFileException(file, "cannot recreate file");
                    try (FileOutputStream stream = new FileOutputStream(file, false)) {
                        writeToFile(stream);
                    }
                } else {
                    try (FileOutputStream stream = new FileOutputStream(file, true)) {
                        writeToFile(stream);
                    }
                }
            } else throw new InvalidFileException(file, "is a directory");
        } else {
            if (!FileUtil.CreateFile(file)) throw new InvalidFileException(file, "cannot create file");
            try (FileOutputStream stream = new FileOutputStream(file, false)) {
                writeToFile(stream);
            }
        }
    }

    /**
     * write the serialized exception to a file
     *
     * @see #getAsString()
     */
    public void writeToFile(@NotNull FileOutputStream stream) throws IOException {
        byte[] lineSeparator = System.lineSeparator().getBytes(StandardCharsets.UTF_8);
        stream.write("---- TheColdWorldLib Packet Exception Report ----".getBytes(StandardCharsets.UTF_8));
        stream.write(lineSeparator);
        stream.write("Time: ".getBytes(StandardCharsets.UTF_8));
        stream.write(Instant.ofEpochSecond(occurredTime).atZone(ZoneId.systemDefault()).toString().getBytes(StandardCharsets.UTF_8));
        stream.write("(Callback packet create time)".getBytes(StandardCharsets.UTF_8));
        stream.write(lineSeparator);
        stream.write(lineSeparator);
        stream.write(className.getBytes(StandardCharsets.UTF_8));
        stream.write(": ".getBytes(StandardCharsets.UTF_8));
        stream.write(message.getBytes(StandardCharsets.UTF_8));
        stream.write(lineSeparator);
        for (StackFrame frame : stackTrace) {
            if (frame.lineNumber < 0) {
                stream.write("\tat ".getBytes(StandardCharsets.UTF_8));
                stream.write(classNameDict.get(frame.classIndex).getBytes(StandardCharsets.UTF_8));
                stream.write("(".getBytes(StandardCharsets.UTF_8));
                stream.write(fileNameDict.get(frame.fileNameIndex).getBytes(StandardCharsets.UTF_8));
                stream.write(")".getBytes(StandardCharsets.UTF_8));
                stream.write(lineSeparator);
            } else {
                stream.write("\tat ".getBytes(StandardCharsets.UTF_8));
                stream.write(classNameDict.get(frame.classIndex).getBytes(StandardCharsets.UTF_8));
                stream.write("(".getBytes(StandardCharsets.UTF_8));
                stream.write(fileNameDict.get(frame.fileNameIndex).getBytes(StandardCharsets.UTF_8));
                stream.write(":".getBytes(StandardCharsets.UTF_8));
                stream.write(String.valueOf(frame.lineNumber).getBytes(StandardCharsets.UTF_8));
                stream.write(")".getBytes(StandardCharsets.UTF_8));
                stream.write(lineSeparator);
            }
        }
        for (SubException subException = this.cause; subException != null; subException = subException.cause) {
            stream.write("Caused by: ".getBytes(StandardCharsets.UTF_8));
            stream.write(subException.className.getBytes(StandardCharsets.UTF_8));
            stream.write(": ".getBytes(StandardCharsets.UTF_8));
            stream.write(subException.message.getBytes(StandardCharsets.UTF_8));
            stream.write(lineSeparator);
            for (StackFrame frame : subException.stackTrace) {
                if (frame.lineNumber < 0) {
                    stream.write("\tat ".getBytes(StandardCharsets.UTF_8));
                    stream.write(classNameDict.get(frame.classIndex).getBytes(StandardCharsets.UTF_8));
                    stream.write("(".getBytes(StandardCharsets.UTF_8));
                    stream.write(fileNameDict.get(frame.fileNameIndex).getBytes(StandardCharsets.UTF_8));
                    stream.write(")".getBytes(StandardCharsets.UTF_8));
                    stream.write(lineSeparator);
                } else {
                    stream.write("\tat ".getBytes(StandardCharsets.UTF_8));
                    stream.write(classNameDict.get(frame.classIndex).getBytes(StandardCharsets.UTF_8));
                    stream.write("(".getBytes(StandardCharsets.UTF_8));
                    stream.write(fileNameDict.get(frame.fileNameIndex).getBytes(StandardCharsets.UTF_8));
                    stream.write(":".getBytes(StandardCharsets.UTF_8));
                    stream.write(String.valueOf(frame.lineNumber).getBytes(StandardCharsets.UTF_8));
                    stream.write(")".getBytes(StandardCharsets.UTF_8));
                    stream.write(lineSeparator);
                }
            }
        }
        stream.write(lineSeparator);
        stream.write(SystemDetailsString.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * generate a full Exception Report
     *
     * @return a full Exception Report
     */
    public String getAsString() {
        String lineSeparator = System.lineSeparator();
        StringBuilder builder = new StringBuilder(classNameDict.size() * 10 + fileNameDict.size() * 5 + 100);
        builder.append("---- TheColdWorldLib Packet Exception Report ----").append(lineSeparator);
        builder.append("Time: ").append(Instant.ofEpochSecond(occurredTime).atZone(ZoneId.systemDefault())).append("(Callback packet create time)").append(lineSeparator).append(lineSeparator);
        builder.append(className).append(": ").append(message).append(lineSeparator);
        for (StackFrame frame : stackTrace) {
            if (frame.lineNumber < 0) {
                builder.append("\tat ").append(classNameDict.get(frame.classIndex)).append("(").append(fileNameDict.get(frame.fileNameIndex)).append(")").append(lineSeparator);
            } else {
                builder.append("\tat ").append(classNameDict.get(frame.classIndex)).append("(").append(fileNameDict.get(frame.fileNameIndex)).append(":").append(frame.lineNumber).append(")").append(lineSeparator);
            }
        }
        for (SubException subException = this.cause; subException != null; subException = subException.cause) {
            builder.append("Caused by: ").append(subException.className).append(": ").append(subException.message).append(lineSeparator);
            for (StackFrame frame : subException.stackTrace) {
                if (frame.lineNumber < 0) {
                    builder.append("\tat ").append(classNameDict.get(frame.classIndex)).append("(").append(fileNameDict.get(frame.fileNameIndex)).append(")").append(lineSeparator);
                } else {
                    builder.append("\tat ").append(classNameDict.get(frame.classIndex)).append("(").append(fileNameDict.get(frame.fileNameIndex)).append(":").append(frame.lineNumber).append(")").append(lineSeparator);
                }
            }
        }
        builder.append("-- System Details --").append(lineSeparator).append(this.SystemDetailsString);
        return builder.toString();
    }
}
