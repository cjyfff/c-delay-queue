# c-delay-queue
一个基于zookeeper和spring boot的分布式延时队列服务系统

## 系统目标
1. 任务需要分布式处理，分配给各个节点，需要对数据分片
2. 需要从节点中选举出一个master，负责数据分片，管理集群信息
3. 需要对任务数据进行持久化，使得任务进度可以跟踪
4. 集群架构的每一个部分都要确保高可用，保证任何一个节点失效不会丢失任务、不会导致服务不可用
5. 保证任务必须执行，且只执行一次

