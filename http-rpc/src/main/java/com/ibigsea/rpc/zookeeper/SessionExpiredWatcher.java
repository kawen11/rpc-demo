package com.ibigsea.rpc.zookeeper;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

/**
 * 监听session过期 用于段先重连
 */
public class SessionExpiredWatcher implements Watcher {

    private String host;
    private CountDownLatch countDownLatch;
    private boolean reconnect=false;
    private ZooKeeper reconnectZk;

    public SessionExpiredWatcher(String host,CountDownLatch countDownLatch) {
        this.host = host;
        this.countDownLatch=countDownLatch;
    }
    public SessionExpiredWatcher() {
    }

    public void process(WatchedEvent watchedEvent) {
    	
    }
}
