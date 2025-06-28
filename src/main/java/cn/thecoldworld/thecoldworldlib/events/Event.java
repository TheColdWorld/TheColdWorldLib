package cn.thecoldworld.thecoldworldlib.events;

import java.util.LinkedList;
import java.util.function.Consumer;

public class Event<T> {
    protected final LinkedList<Consumer<T>> invokers = new LinkedList<>();

    public void Invoke(T item) {
        for (Consumer<T> invoker : invokers) {
            invoker.accept(item);
        }
    }

    public void add(Consumer<T> invoker) {
        synchronized (this.invokers) {
            invokers.add(invoker);
        }
    }
}
