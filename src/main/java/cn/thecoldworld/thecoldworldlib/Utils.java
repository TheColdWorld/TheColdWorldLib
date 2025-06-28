package cn.thecoldworld.thecoldworldlib;

import com.mojang.serialization.DataResult;
import org.jetbrains.annotations.NotNull;

public final class Utils {
    private Utils() {
    }

    public static <T> @NotNull DataResult<T> CodecOnException(Throwable e) {
        return DataResult.error(() -> String.format("Exception(%s) occored:%s", e.getClass().getCanonicalName(), e.getMessage()));
    }
}
