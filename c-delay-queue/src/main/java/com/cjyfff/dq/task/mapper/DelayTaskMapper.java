package com.cjyfff.dq.task.mapper;

import java.util.List;

import com.cjyfff.dq.task.model.DelayTask;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DelayTaskMapper {
    int deleteByPrimaryKey(Long id);

    int insert(DelayTask record);

    int insertSelective(DelayTask record);

    DelayTask selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DelayTask record);

    int updateStatusByTaskIdAndOldStatus(@Param(value = "taskId") String taskId,
                                         @Param(value = "oldStatus") Integer oldStatus,
                                         @Param(value = "newStatus") Integer newStatus,
                                         @Param(value = "shardingId") Integer shardingId);

    DelayTask selectByTaskId(@Param(value = "taskId") String taskId);

    DelayTask selectByTaskIdForUpdate(@Param(value = "taskId") String taskId);

    DelayTask selectByTaskIdAndStatusForUpdate(@Param(value = "oldStatus") Integer oldStatus,
                                               @Param(value = "taskId") String taskId,
                                               @Param(value = "shardingId") Integer shardingId);

    List<DelayTask> selectByStatusAndExecuteTime(@Param(value = "oldStatus") Integer oldStatus,
                                                 @Param(value = "shardingId") Integer shardingId,
                                                 @Param(value = "executeTimeBegin") Long executeTimeBegin,
                                                 @Param(value = "executeTimeEnd") Long executeTimeEnd);

    List<DelayTask> selectByStatusForUpdate(@Param(value = "status") Integer status);

    List<DelayTask> selectByStatusAndShardingId(@Param(value = "status") Integer status,
                                                         @Param(value = "shardingId") Integer shardingId);

    int updateByPrimaryKey(DelayTask record);
}