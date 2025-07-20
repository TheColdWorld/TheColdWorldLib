package cn.thecoldworld.thecoldworldlib.exceptions;

import cn.thecoldworld.thecoldworldlib.utils.IterUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class SerializedException {
    public static final Codec<SerializedException> CODEC = RecordCodecBuilder.create(exceptionInstance -> exceptionInstance.group(
            Codec.STRING.fieldOf("n").forGetter(e -> e.className),
            Codec.STRING.fieldOf("m").forGetter(e -> e.message),
            StackFrame.CODEC.listOf().fieldOf("s").forGetter(e -> e.stackTrace),
            SubException.CODEC.optionalFieldOf("c", null).forGetter(e -> e.cause),
            Codec.STRING.listOf().fieldOf("nd").forGetter(e -> e.classNameDict),
            Codec.STRING.listOf().fieldOf("fd").forGetter(e -> e.fileNameDict)
    ).apply(exceptionInstance, SerializedException::new));
    public final String className;
    public final String message;
    final List<StackFrame> stackTrace;
    final @Nullable SubException cause;
    final List<String> classNameDict;
    final List<String> fileNameDict;

    private SerializedException(final String className, final String message, final List<StackFrame> stackTrace, final @Nullable SubException cause, final List<String> classNameDict, final List<String> fileNameDict) {
        this.className = className;
        this.message = message;
        this.stackTrace = stackTrace;
        this.cause = cause;
        this.classNameDict = classNameDict;
        this.fileNameDict = fileNameDict;
    }

    public SerializedException(Throwable exception) {
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


}
