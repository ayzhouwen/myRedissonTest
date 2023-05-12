package com.zw.common.core.plus;

/**
 * 小插件初始化,停止,运行
 */
public interface SmallPlug {
    /**
     * 初始化
     */
    void initPlus();

    /**
     * 启动插件
     */
    void startPlus();

    /**
     * 停止插件
     */
    void stopPlus();
}
