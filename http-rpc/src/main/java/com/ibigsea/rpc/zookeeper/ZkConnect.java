package com.ibigsea.rpc.zookeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

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
    		System.out.println("Zookeeper重新获取服务结点。。。");
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
		try {
			final ZooKeeper zk = new ZooKeeper("127.0.0.1:2181", 4000, new Watcher() {
				public void process(WatchedEvent event) {
					System.out.println("默认事件： " + event.getType());
					if (Event.KeeperState.SyncConnected == event.getState()) {
						// 如果收到了服务端的响应事件，连接成功
						System.out.println("Connected!");
					}
				}
			});
			String perNode = "/test2/per1";
			//创建持久结点
			zk.create(perNode, "111".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			//创建临时结点
			String temNode = "/test2/temp1";
			zk.create(temNode,"222".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
//			// 通过exists绑定事件
			Stat stat = zk.exists(perNode, new Watcher() {
				public void process(WatchedEvent event) {
					System.out.println(event.getType() + "->" + event.getPath());
					 try {
						 //再一次去绑定事件 ,但是这个走的是默认事件
						 zk.exists(event.getPath(),true);
					 } catch (KeeperException e) {
						 e.printStackTrace();
					 } catch (InterruptedException e) {
						 e.printStackTrace();
					 }
				}
			});

			// 通过修改的事务类型操作来触发监听事件
			stat = zk.setData(perNode, "2".getBytes(), stat.getVersion());

			zk.getData(perNode, new Watcher() {
				public void process(WatchedEvent event) {
					System.out.println("2222222222");
				}

			}, stat);
			stat = zk.setData(perNode, "3".getBytes(), stat.getVersion());

			Thread.sleep(1000);

			zk.getChildren(perNode, new Watcher() {
				public void process(WatchedEvent event) {
					System.out.println("33333333333");
				}
			});
			//删除结点
			zk.delete(perNode, stat.getVersion());

			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
