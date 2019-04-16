package com.ibigsea.rpc.zookeeper;

import java.util.List;
import java.util.Map;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import com.ibigsea.rpc.config.ProviderConfig;
import com.ibigsea.rpc.proxy.ProviderProxyFactory;
import com.ibigsea.rpc.proxy.ProviderProxyNettyFactory;

/**
 * 提供者端服务znode维护类
 */
public class ZKServerService {
    private ZooKeeper zooKeeper;

    public ZKServerService(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    //生成所有注册的服务znode
    public void createServerService(ProviderConfig config, Map<String,String> serviceMap) throws KeeperException, InterruptedException {
        ZKTempZnodes zkTempZnodes=new ZKTempZnodes(zooKeeper);
        String host=config.getIp()+ ":" +config.getPort();
        System.out.println("host:"+host);
        for (Map.Entry<String,String> entry:serviceMap.entrySet()){
            //获取配置中设置的IP设置为IP顺序节点的值 默认127.0.0.1:8888
            zkTempZnodes.createTempZnode(ZKConst.rootPath+ZKConst.servicePath+"/"+entry.getValue()+ZKConst.providersPath+"/"+host,null);
            //创建连接数节点 首次增加时连接数为0
//            zkTempZnodes.createTempZnode(ZKConst.rootPath+ZKConst.balancePath+"/"+entry.getKey()+"/"+ip,0+"");
        }
    }

    //获得这个服务所有的提供者 包含监听注册
    public List<String> getAllServiceIP(String serviceName) throws KeeperException, InterruptedException {
        ZKTempZnodes zkTempZnodes=new ZKTempZnodes(zooKeeper);
        IPWatcher ipWatcher=new IPWatcher(zooKeeper);
        return zkTempZnodes.getPathChildren(ZKConst.rootPath+ZKConst.servicePath+"/"+serviceName+ZKConst.providersPath,ipWatcher);
    }

    //初始化根节点及服务提供者节点 均为持久节点
    public void initZnode(Map<String,String> serviceMap) throws KeeperException, InterruptedException {
        ZKTempZnodes zkTempZnodes=new ZKTempZnodes(zooKeeper);
        StringBuilder pathBuilder=new StringBuilder(ZKConst.rootPath);
//        String balancePath=ZKConst.rootPath;
        zkTempZnodes.createSimpleZnode(pathBuilder.toString(),null);
//        balancePath=balancePath+ZKConst.balancePath;
//        zkTempZnodes.createSimpleZnode(balancePath,null);
        pathBuilder.append(ZKConst.servicePath);
        zkTempZnodes.createSimpleZnode(pathBuilder.toString(),null);
        for (Map.Entry<String,String> entry:serviceMap.entrySet()){
//            zkTempZnodes.createSimpleZnode(balancePath+"/"+entry.getKey(),null);
            StringBuilder serviceBuilder=new StringBuilder(pathBuilder.toString());
            serviceBuilder.append("/");
            System.out.println("service name:"+entry.getValue());
            serviceBuilder.append(entry.getValue());
            zkTempZnodes.createSimpleZnode(serviceBuilder.toString(),null);
            serviceBuilder.append(ZKConst.providersPath);
            zkTempZnodes.createSimpleZnode(serviceBuilder.toString(),null);
        }
    }

}
