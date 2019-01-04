# c-delay-queue
一个基于zookeeper和spring boot的分布式延时队列服务系统

## 系统目标
1. 服务部署在多个节点上，通过zookeeper实现节点选举。
2. 对任务数据进行分片，各个节点均衡地处理分配给自己的任务
3. 集群系统保证高可用，任一节点的宕机不影响系统运作
4. 保证任务会被执行，并且只执行一次
5. 突发大量请求时，服务降级


## 依赖组件
1. mysql 5.6+
2. zookeeper 3.4 不支持3.5

## 运行、部署步骤
1. 下载[l-election选举sdk](https://github.com/cjyfff/l-election.git)，安装到本地mvn仓库
2. 根据本项目的`scripts/db.sql`创建数据库和数据表
3. 修改本项目中`src/main/resources/application.properties.example`，重命名为`application.properties`，并修改相关配置。主要需要修改的项为：
* `l_election.specified_local_ip`，指定本机ip，不指定的话将自动检测，自动检测时假如本机拥有多个ip可能会获取到错误的ip
* `l_election.specified_port`，指定本机服务端口，一般与`server.port`一致
* `l_election.zk_host`，zookeeper地址以及端口，假如是集群的话使用逗号分隔，如`192.168.43.9:2181,192.168.43.42:2181,192.168.43.241:2181`
* `jdbc.write.url`和`jdbc.read.url`，写库以及读库uri，假如没有读写分类数据库的话可以指定为同一个数据库
4. 接下来就可以编译运行本项目


## 接口调用方法
1. 新增任务接口
调用`/dq/acceptMsg`接口进行新增任务，参数使用json传输，具体参数如下：

| 参数名称 | 类型 | 是否必填 | 长度限制 | 说明 |
| :------: | :------: | :------: | :------: | :-----: |
| taskId | str |  Y | 32 | 任务识别id，必须保证唯一，建议使用uuid生成 |
| functionName | str | Y | 1~100 | 调用的服务逻辑类名称，与下文介绍中的`TaskHandler`注解的`value`参数一致 |
| params | int | N | 0~1000 | 调用服务逻辑的参数字符串 |
| retryCount | int | N | 0~10 | 任务执行失败时的重试次数，不指定代表不重试 |
| retryInterval | int | N | 1~60 | 任务执行失败时的重试间隔，不指定的话设为1，单位秒 |
| delayTime | int | Y | >1 | 延时时间，单位秒 |
| nonceStr | str | Y | 32 | 随机字符串，用于接口幂等，当接口请求失败，调用方对接口进行重试时，要带上相同的值 |

调用例子：
```
{
	"taskId": "d610ec9031674206a359f6a1f5afb4a9",
	"functionName": "exampleHandler",
	"params": "{}",
	"retryCount": 0,
	"retryInterval": 1,
	"delayTime": 10,
	"nonceStr": "37140de3f8634ffb98a0eff55b18d37c"
}
```
2. 监控接口
调用`/monitor/nodeInfo`接口可以查看服务的选举状态以及分片信息，具体例子如下：
```
{
    // 集群分片信息map，key为分片节点id，value为分片节点的ip以及端口
    "shardingMap": {
        "0": "192.168.43.221:8888"
    },
    // 本机分片id
    "nodeId": 0,
    // 本机选举状态，1代表完成，0代表选举未完成
    "electionStatus": 1,
    // 本机是否集群的master
    "leader": true
}
```

## 自定义任务方法
延时任务服务的目的就是让系统可以在指定时间调用指定的服务逻辑，因此定义自己服务逻辑类十分必要。实现的方式可以参考`com.cjyfff.dq.task.handler.impl.ExampleHandler`。有以下几个注意点：
* 服务逻辑类需要继承`ITaskHandler`接口，并实现`run`方法，系统到达指定时间的时候就会运行`run`方法里的逻辑。
* 服务逻辑类需要添加`TaskHandler`注解，并且在注解的参数`value`中指定服务名称，调用新增任务接口时是就是通过这个名称找到对应的服务。同时服务逻辑类也需要定义为spring bean使得spring ioc容器能发现这个类。
* `run`方法中`paras`参数的值对应调用新增任务接口时传入的`paras`值。可以用于任务调用方与任务消费方的参数传递。

## 集群部署方法（待完善）
