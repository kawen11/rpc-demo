package com.ibigsea.rpc.zookeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooKeeper;

public class ZkConnect {

	private ZooKeeperConfig zkConfig;
	private ZooKeeper zk;
	 //获取到的所有提供服务的服务器列表
    private volatile List<String> dataList=new ArrayList<String>();

	public ZkConnect(ZooKeeperConfig zkConfig) {
		this.zkConfig = zkConfig;
	}

	public ZooKeeper getZkConnection() throws IOException, InterruptedException {
//		CountDownLatch countDownLatch = new CountDownLatch(1);
		zk = new ZooKeeper(zkConfig.getHost(), 20000, new SessionExpiredWatcher(){
			@Override
			public void process(WatchedEvent watchedEvent) {
				if(watchedEvent.getType()==Event.EventType.NodeChildrenChanged){
                    //监听zkServer的服务器列表变化
                    watchNode();
                }
			}
		});
//		countDownLatch.await();
		return zk;
	}
	// 从dataList列表随机获取一个可用的服务端的地址信息给rpc-client
    public String discover(){
        int size=dataList.size();
        if(size>0){
            int index= new Random().nextInt(size);
            return dataList.get(index);
        }
        throw new RuntimeException("没有找到对应的服务器");
    }
	
	//监听服务端的列表信息
    private void watchNode(){
    	try{
            //获取子节点信息
            List<String> nodeList = zk.getChildren(ZKConst.rootPath, true);
            List<String> dataList=new ArrayList<String>();
            for (String node : nodeList) {
                byte[] bytes = zk.getData(ZKConst.rootPath + "/" + node, false, null);
                dataList.add(new String(bytes));
            }
            this.dataList=dataList;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
	public static void main(String[] args) {
		ZooKeeperConfig zkConfig = new ZooKeeperConfig("127.0.0.1:2181");
		ZooKeeper zk = null; 
		try {
			zk = new ZkConnect(zkConfig).getZkConnection();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(zk.getState());
	}
}
