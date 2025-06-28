package cn.thecoldworld.thecoldworldlib.events;

import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;

import java.util.function.Consumer;

public class OneShotEvent<T> extends FrozenableEvent<T> {
    protected volatile boolean abandoned = false;

    public boolean isAbandoned() {
        return abandoned;
    }

    public void abandon() {
        abandoned = true;
    }

    public void assertAbandoned() {
        if (abandoned) try {
            throw new IllegalStateException("Event " + this.getClass().getName() + " is abandoned");
        } catch (IllegalStateException e) {
            throw new CrashException(new CrashReport(e.getMessage(), e));
        }
        assertFrozen();
    }

    @Override
    public void Invoke(T item) {
        assertAbandoned();
        super.Invoke(item);
        this.abandon();
    }

    @Override
    public void add(Consumer<T> invoker) {
        assertAbandoned();
        ((Event<T>) this).add(invoker);
    }
}
