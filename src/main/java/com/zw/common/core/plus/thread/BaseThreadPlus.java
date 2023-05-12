package com.zw.common.core.plus.thread;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReflectUtil;
import com.zw.common.core.plus.SmallPlug;


/**
 * 插件线程基础类
 */
public class BaseThreadPlus implements SmallPlug {
    private String threadName = "";
    private Thread selfThread = null;
    private AbstractRunable selfRunable = null;

    public BaseThreadPlus(String threadName, AbstractRunable runnable) {
        this.threadName = threadName + "-" + DateUtil.now();
        this.selfRunable = runnable;
    }

    //无构造函数初始化线程
    public static BaseThreadPlus loadBaseThreadPlus(String threadName, Class classs) {
        AbstractRunable runableObj = (AbstractRunable) ReflectUtil.newInstance(classs);
        BaseThreadPlus plus = ReflectUtil.newInstance(BaseThreadPlus.class, threadName, runableObj);
        return plus;
    }

    //有构造函数初始化线程
    public static BaseThreadPlus loadBaseThreadPlus(String threadName, Class classs, Object... param) {
        AbstractRunable runableObj = (AbstractRunable) ReflectUtil.newInstance(classs, param);
        BaseThreadPlus plus = ReflectUtil.newInstance(BaseThreadPlus.class, threadName, runableObj);
        return plus;
    }

    public String getThreadName() {
        return threadName;
    }

    public Thread getSelfThread() {
        return selfThread;
    }

    public AbstractRunable getSelfRunable() {
        return selfRunable;
    }

    @Override
    public void initPlus() {

    }

    @Override
    public void startPlus() {
        Thread t = new Thread(selfRunable);
        t.setName(threadName);
        selfThread = t;
        selfRunable.run = true;
        t.start();
    }

    @Override
    public void stopPlus() {
        selfRunable.run = false;
        selfThread.interrupt();
    }
}
