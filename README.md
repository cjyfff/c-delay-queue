## 原理简述
本文简述一下此系统的工作原理

### 0.总述
本系统可以以分布式集群的方式部署，当系统的每个节点启动时，都会触发选举逻辑，系统通过Zookeeper进行选举。选举出来的master负责统计节点的信息，并且给每个节点分配一个节点id。任务将根据任务的task id对节点数目取模再和节点id进行匹配来决定。系统的每一个节点，当接收到一个任务后，都会判断一下是不是应该自己处理，不是的话，会转发给应该处理的节点。
假设集群包括两个处理节点，系统的架构图如下：

![总体架构](https://github.com/cjyfff/c-delay-queue/blob/master/principle/img/%E6%80%BB%E4%BD%93%E6%9E%B6%E6%9E%84.png)


### 1. 接收任务流程
接收任务的流程如下图。主要的判断逻辑是：“判断选举是否完成”-->“判断是否自己处理的任务”-->“判断是否需要马上放入队列”。
说明一下“判断是否需要马上放入队列”这一步。由于任务有可能是指定很长的时间之后才执行，为了避免这些远未达到执行时间任务放在延时队列（jdk自带的DelayQueue）里耗费内存资源，系统有一个指定时间（delay_queue.critical_polling_time），在这个时间内会被执行的任务才会放到DelayQueue中。假如不是在这个时间内会被执行的任务，只会保存在数据库中，且任务状态设为“待轮询”，由定时任务检测，等到该任务会在delay_queue.critical_polling_time指定的时间内执行时，才被加载到DelayQueue中。

![接收任务流程](https://github.com/cjyfff/c-delay-queue/blob/master/principle/img/%E6%8E%A5%E6%94%B6%E4%BB%BB%E5%8A%A1%E6%B5%81%E7%A8%8B.png)

### 2. 定时任务流程
正如上文所说，定时任务的主要作用是把delay_queue.critical_polling_time时间内的、“待轮询”状态的任务加载到DelayQueue，详细流程如下图。

![定时任务](https://github.com/cjyfff/c-delay-queue/blob/master/principle/img/%E5%AE%9A%E6%97%B6%E4%BB%BB%E5%8A%A1%E6%B5%81%E7%A8%8B.png)

### 3. DelayQueue消费流程
加载到DelayQueue中的任务，当时间到达时，会执行以下逻辑，进行任务消费。

![队列消费](https://github.com/cjyfff/c-delay-queue/blob/master/principle/img/%E9%98%9F%E5%88%97%E6%B6%88%E8%B4%B9%E6%B5%81%E7%A8%8B.png)

### 4.节点变更逻辑
当集群的节点发生改变，预示着任务和节点的对应关系已经不准确，并且有可能节点的主从身份也发生了变化，因此系统需要重新判断是否需要重新选主，并且master需要对数据库中未处理的任务，进行重新分片，分派新的节点进行处理。具体流程如下图，master和slave身份的节点各自执行不同的逻辑。

![节点变更](https://github.com/cjyfff/c-delay-queue/blob/master/principle/img/%E8%8A%82%E7%82%B9%E5%8F%98%E6%9B%B4%E6%B5%81%E7%A8%8B.png)

### 5.节点间通信逻辑
5.1 每个节点都会作为Netty服务端，在节点初始化时监听指定端口。
5.2 每个节点在每次选举完后都会连接其他节点，使得所有节点都能互相通信。具体逻辑是：选举完毕后，每个节点都能拿到所有节点的ip信息，每个节点去连接比自身的node id大的节点，node id排在最后的节点不用再主动连接。例如集群有3个节点，node id分别是1、2、3。那么主动连接的逻辑就是：1->2; 1->3; 2->3。
5.3 节点间建立连接之后，作为客户端的节点会把自身node id返回给服务端，同时客户端把node id与Netty channel的对应关系保存起来，服务端也把node id与Netty channel的对应关系保存。这样所有节点都有一份node id与Netty channel的对应关系，可以根据node id向指定节点发送消息。
5.4 由于节点间的任务转发消息采用Netty的异步模式，无法在发送消息后马上知道消息是否接收。为了避免网络抖动等原因造成消息没有成功投放，系统加入了消息确认机制。节点在发送消息时，会把消息的发送时间等信息存放到一个集合里，当对方节点收到消息并响应时，节点会把消息记录从该集合里删除。有一个定时任务会定时检查集合里的消息记录，假如某个消息超过指定时间仍然没有被确认，该消息会被重新发送。
