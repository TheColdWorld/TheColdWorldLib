package cn.thecoldworld.thecoldworldlib.events;

import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;

import java.util.function.Consumer;

public class FrozenableEvent<T> extends Event<T> {
    protected volatile boolean frozen = false;

    public boolean isFrozen() {
        return frozen;
    }

    public void freeze() {
        frozen = true;
    }

    @Override
    public void Invoke(T item) {
        super.Invoke(item);
    }

    public void assertFrozen() {
        if (frozen) try {
            throw new IllegalStateException("Event " + this.getClass().getName() + " is frozen");
        } catch (IllegalStateException e) {
            throw new CrashException(new CrashReport(e.getMessage(), e));
        }
    }

    @Override
    public void add(Consumer<T> invoker) {
        assertFrozen();
        super.add(invoker);
    }
}
