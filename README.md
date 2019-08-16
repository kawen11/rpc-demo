# 从零写一个分布式RPC

##大概用到的技术：
Spring、多线程等
动态代理、反射
序列化（ Kryo、Hessian、Java原生）
网络传输（http、tcp ）
Zookeeper
负载均衡（权重、轮训、最小活跃、一致性哈希、自定义策略）

##模块
###Rpc-server：
RPC服务端,启动RPC服务,扫描Provider中的所有可以提供的服务列表并保存，接受RPC客户端的消息并且通过反射调用具体的方法响应RPC客户端,把方法执行结果返回到RPC客户端。
RPC客户端,通过网络通信往RPC服务端发送请求调用消息，接受服务端的响应消息，配置动态代理类,所有的方法调用都通过网络调用发送到RPC服务端。
###demo-Consumer：
通过Spring的配置产生代理对象,服务消费者可以直接通过@Resource注解引用到该对象,通过http-rpc框架调用到服务消费者Provider。
###demo-Provider：
通过Spring的配置启动SpringContext, 可以让RPCServer识别到该服务，启动服务。
###demo-Façade：接口定义

##结构图
