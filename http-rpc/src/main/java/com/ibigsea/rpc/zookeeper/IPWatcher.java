package com.ibigsea.rpc.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

/**
 * Created by jiang
 * 服务提供者和调用者的IP监控器 即监听服务的可用性
 */
public class IPWatcher implements Watcher{

    private ZooKeeper zooKeeper;

    public IPWatcher(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    public void process(WatchedEvent watchedEvent) {

    	
    }
}
