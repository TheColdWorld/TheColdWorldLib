package cn.thecoldworld.thecoldworldlib.utils;

import it.unimi.dsi.fastutil.shorts.ShortPredicate;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class IterUtil {
    private IterUtil() {
    }

    /**
     * return {@code  true} if only one element matches the condition
     *
     * @return {@code true} if only one element matches the condition ,otherwise {@code  false}
     */
    public static <T> boolean oneMatch(final @NotNull Stream<T> stream, final Predicate<T> condition) {
        return stream.filter(condition).count() == 1;
    }

    /**
     * return {@code true} if only one element matches the condition
     *
     * @return {@code true} if only one element matches the condition ,otherwise {@code false}
     */
    public static <T> boolean oneMatch(final @NotNull Iterable<T> iterable, final Predicate<T> condition) {
        int count = 0;
        for (T t : iterable) {
            if (condition.test(t)) count++;
        }
        return count == 1;
    }

    /**
     * return {@code true} if only one element matches the condition
     *
     * @return {@code true} if only one element matches the condition ,otherwise {@code false}
     */
    public static <T> boolean oneMatch(final @NotNull T[] array, final Predicate<T> condition) {
        int count = 0;
        for (T t : array) {
            if (condition.test(t)) count++;
        }
        return count == 1;
    }

    /**
     * return {@code true} if only one element matches the condition
     *
     * @return {@code true} if only one element matches the condition ,otherwise {@code false}
     */
    public static boolean oneMatch(final int @NotNull [] array, final IntPredicate condition) {
        int count = 0;
        for (int t : array) {
            if (condition.test(t)) count++;
        }
        return count == 1;
    }

    /**
     * return {@code true} if only one element matches the condition
     *
     * @return {@code true} if only one element matches the condition ,otherwise {@code false}
     */
    public static boolean oneMatch(final short @NotNull [] array, final ShortPredicate condition) {
        int count = 0;
        for (short t : array) {
            if (condition.test(t)) count++;
        }
        return count == 1;
    }

    /**
     * return {@code true} if only one element matches the condition
     *
     * @return {@code true} if only one element matches the condition ,otherwise {@code false}
     */
    public static boolean oneMatch(final long @NotNull [] array, final LongPredicate condition) {
        int count = 0;
        for (long t : array) {
            if (condition.test(t)) count++;
        }
        return count == 1;
    }

    public static <T> int getIndex(T[] array, T object) {
        for (int i = 0; i < array.length; i++) {
            if (Objects.deepEquals(array[i], object)) return i;
        }
        return -1;
    }

    public static int getIndex(int[] array, int object) {
        for (int i = 0; i < array.length; i++) {
            if (Objects.deepEquals(array[i], object)) return i;
        }
        return -1;
    }

    public static int getIndex(short[] array, short object) {
        for (int i = 0; i < array.length; i++) {
            if (Objects.deepEquals(array[i], object)) return i;
        }
        return -1;
    }

    public static int getIndex(long[] array, long object) {
        for (int i = 0; i < array.length; i++) {
            if (Objects.deepEquals(array[i], object)) return i;
        }
        return -1;
    }

    public static int getIndex(boolean[] array, boolean object) {
        for (int i = 0; i < array.length; i++) {
            if (Objects.deepEquals(array[i], object)) return i;
        }
        return -1;
    }

    public static <T> int indexofOrAdd(final List<T> list, final T value) {
        int index = list.indexOf(value);
        if (index == -1) {
            index = list.size();
            list.add(value);
        }
        return index;
    }
}
