package com.zw.common.core.plus.thread;

public abstract class AbstractRunable implements Runnable {
    public volatile boolean run = false;

    @Override
    public abstract void run();
}
