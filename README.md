# c-delay-queue
一个基于zookeeper和spring boot的分布式延时队列服务系统

## 系统目标
1. 任务需要分布式处理，分配给各个节点，需要对数据分片
2. 需要从节点中选举出一个master，负责数据分片，管理集群信息
3. 需要对任务数据进行持久化，使得任务进度可以跟踪
4. 集群架构的每一个部分都要确保高可用，保证任何一个节点失效不会丢失任务、不会导致服务不可用
5. 保证任务必须执行，且只执行一次


## 依赖组件
1. mysql 5.6+
2. zookeeper 3.4 不支持3.5

## 运行、部署步骤
1. 下载选举模块（https://github.com/cjyfff/l-election.git），安装到本地mvn仓库
2. 根据本项目的`scripts/db.sql`创建数据库和数据表
3. 修改本项目中`src/main/resources/application.properties.example`，重命名为`application.properties`，并修改相关配置。主要需要修改的项为：
* `l_election.specified_local_ip`，指定本机ip，不指定的话将自动检测，自动检测时假如本机拥有多个ip可能会获取到错误的ip
* `l_election.zk_host`，zookeeper地址以及端口，假如是集群的话使用逗号分隔，如`192.168.43.9:2181,192.168.43.42:2181,192.168.43.241:2181`
* `jdbc.write.url`和`jdbc.read.url`，写库以及读库uri，假如没有读写分类数据库的话可以指定为同一个数据库
4. 接下来就可以编译运行本项目


## 接口调用方法（待完善）
1. 新增任务接口
2. 监控接口

## 自定义任务方法
延时任务服务的目的就是让系统可以在指定时间调用指定的服务逻辑，因此定义自己服务逻辑类十分必要。实现的方式可以参考`com.cjyfff.dq.task.handler.impl.ExampleHandler`。有以下几个注意点：
* 服务逻辑类需要继承`ITaskHandler`接口，并实现`run`方法，系统到达指定时间的时候就会运行`run`方法里的逻辑。
* 服务逻辑类需要添加`TaskHandler`注解，并且在注解的参数`value`中指定服务名称，调用新增任务接口时是就是通过这个名称找到对应的服务。同时服务逻辑类也需要定义为spring bean使得spring ioc容器能发现这个类。
* `run`方法中`paras`参数的值对应调用新增任务接口时传入的`paras`值。可以用于任务调用方与任务消费方的参数传递。

## 集群部署方法（待完善）
