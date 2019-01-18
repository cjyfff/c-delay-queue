package com.cjyfff.dq.task.mapper;

import com.cjyfff.dq.task.model.TaskResult;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskResultMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TaskResult record);

    int insertSelective(TaskResult record);

    TaskResult selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TaskResult record);

    int updateByPrimaryKeyWithBLOBs(TaskResult record);

    int updateByPrimaryKey(TaskResult record);
}