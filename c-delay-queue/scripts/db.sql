create database delay_queue default charset utf8 COLLATE utf8_general_ci;

use delay_queue;

CREATE TABLE `delay_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `task_id` varchar(50) NOT NULL COMMENT '任务id',
  `function_name` varchar(100) NOT NULL COMMENT '任务方法名称',
  `params` varchar(1000) NOT NULL COMMENT '任务方法参数',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '状态 0:已接收 50:轮询任务处理中 60: 队列中 100: 执行中 101: 转发中 150: 重试中 200: 执行成功 400：执行失败 410: 重试失败',
  `delay_time` bigint(20) NOT NULL COMMENT '延时时间，单位秒',
  `execute_time` bigint(20) DEFAULT NULL COMMENT '执行时间，时间戳，单位秒',
  `retry_interval` int(11) DEFAULT NULL COMMENT '重试间隔，单位秒',
  `retry_time` bigint(20) DEFAULT NULL COMMENT '重试时间，时间戳，单位秒',
  `retry_count` tinyint(4) DEFAULT '0' COMMENT '重试次数',
  `already_retry_count` tinyint(4) DEFAULT '0' COMMENT '已经重试次数',
  `sharding_id` int(11) NOT NULL COMMENT '分片id',
  `created_at` datetime DEFAULT NULL COMMENT '创建时间',
  `modified_at` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `delay_task_task_id_IDX` (`task_id`) USING BTREE,
  KEY `delay_task_sharding_id_IDX` (`sharding_id`) USING BTREE,
  KEY `delay_task_execute_time_IDX` (`execute_time`) USING BTREE,
  KEY `delay_task_retry_time_IDX` (`retry_time`) USING BTREE,
  KEY `status_and_sharding_id` (`status`, `sharding_id`)
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8;

CREATE TABLE `delay_queue_exec_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `task_id` varchar(50) NOT NULL COMMENT '任务id',
  `status` int(11) DEFAULT '0' COMMENT '状态 0:已接收 50:轮询任务处理中 60: 队列中 100: 执行中 101: 转发中 150: 重试中 200: 执行成功 400：执行失败 410: 重试失败',
  `sharding` int(11) NOT NULL COMMENT '分片id',
  `function_name` varchar(100) NOT NULL COMMENT '任务方法名',
  `params` varchar(1000) NOT NULL COMMENT '任务参数',
  `msg` varchar(500) NOT NULL COMMENT '执行信息',
  `task_result_id` bigint(20) DEFAULT NULL COMMENT '任务结果id，可为空',
  `created_at` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `delay_queue_exec_log_task_id_IDX` (`task_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='任务执行日志';

CREATE TABLE `task_result` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `result` text COMMENT '结果',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='任务结果表';