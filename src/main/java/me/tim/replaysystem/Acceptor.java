package me.tim.replaysystem;

import java.util.function.Consumer;

public abstract class Acceptor<T> implements Runnable{

    private final Consumer<T> consumer;

    protected Acceptor(Consumer<T> consumer) {
        this.consumer = consumer;
    }

    public abstract T getValue();

    @Override
    public void run() {
        consumer.accept(getValue());
    }
}