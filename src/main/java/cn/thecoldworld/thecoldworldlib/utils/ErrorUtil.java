package cn.thecoldworld.thecoldworldlib.utils;

import com.mojang.serialization.DataResult;
import org.jetbrains.annotations.NotNull;

public final class ErrorUtil {
    private ErrorUtil() {
    }

    public static <T> @NotNull DataResult<T> CodecOnException(Throwable e) {
        return DataResult.error(() -> String.format("Exception(%s) occurred:%s", e.getClass().getCanonicalName(), e.getMessage()));
    }

    public static String createFailString(Throwable e) {
        StringBuilder sb = new StringBuilder();
        sb.append("Exception occurred:");
        sb.append(e.getClass().getCanonicalName());
        sb.append("\nStarkTrace:");
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            sb.append(stackTraceElement.toString()).append("\n");
        }
        return sb.toString();
    }
}
