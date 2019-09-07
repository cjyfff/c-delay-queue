package com.cjyfff.dq.task.handler.impl;

import com.alibaba.fastjson.JSON;

import com.cjyfff.dq.common.lock.ZkLockImpl.LockObject;
import com.cjyfff.election.config.ZooKeeperClient;
import com.cjyfff.dq.common.aop.UnlockAfterDbCommit;
import com.cjyfff.dq.common.lock.UnlockAfterDbCommitInfoHolder;
import com.cjyfff.dq.common.lock.ZkLock;
import com.cjyfff.dq.task.handler.HandlerResult;
import com.cjyfff.dq.task.handler.ITaskHandler;
import com.cjyfff.dq.task.handler.annotation.TaskHandler;
import com.cjyfff.dq.task.handler.vo.OrderAutoAuditHandlerParaVo;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 订单自动审核节点（demo，实际业务逻辑不应放在调度系统，而应该调用业务系统的接口）
 * 创建任务时传入订单id
 * 处理时，根据订单id取出订单
 * 自旋尝试获得锁，获得锁后再检查状态
 * 订单还是初始化成功状态的话则更改订单状态为审核完成
 * Created by jiashen on 18-12-4.
 */
@Slf4j
@Service
@TaskHandler(value = "orderAutoAuditHandler")
public class OrderAutoAuditHandler implements ITaskHandler {

    private static final Integer ORDER_INIT = 410;

    private static final Integer AUDIT_COMPLETED = 500;

    private static final String ORDER_LOCK_PAHT = "order_lock";

    @Autowired
    private ZkLock zkLock;

    @Autowired
    private ZooKeeperClient zooKeeperClient;

    @Override
    @UnlockAfterDbCommit
    @Transactional
    public HandlerResult run(String paras) {

        log.info("Run OrderAutoAuditHandler with paras: " + paras);

        OrderAutoAuditHandlerParaVo paraVo = JSON.parseObject(paras, OrderAutoAuditHandlerParaVo.class);

        String orderId = paraVo.getOrderId();

        if (StringUtils.isEmpty(orderId)) {
            return new HandlerResult(HandlerResult.DEFAULT_FAIL_CODE, "orderId不能为空");
        }
        LockObject lockObject = null;
        String lockKey = zkLock.getKeyLockKey(ORDER_LOCK_PAHT, orderId);

        try {
            // 一张订单同一时间只可能进行一个操作，因此直接用order id作为lock key，不用考虑状态
            lockObject = zkLock.tryLock(zooKeeperClient.getClient(), ORDER_LOCK_PAHT, orderId, 30);
            if (lockObject.isLockSuccess()) {
                if (checkOrderIsInitStatus(orderId)) {
                    updateOrderStatus(orderId, AUDIT_COMPLETED);
                    return new HandlerResult(HandlerResult.SUCCESS_CODE, String.format("订单%s自动客审成功", orderId));
                }

                return new HandlerResult(HandlerResult.SUCCESS_CODE,
                    String.format("订单%s已经不是初始化状态，应该已经被人工客审，或者被其他worker设置为客审成功", orderId));
            } else {
                return new HandlerResult(HandlerResult.DEFAULT_FAIL_CODE, String.format("订单%s无法获取到锁", orderId));
            }

        } catch (Exception e) {
            log.error("OrderAutoAuditHandler get error:", e);
            return new HandlerResult(HandlerResult.DEFAULT_FAIL_CODE, String.format("订单自动客审时发生错误：%s", e.getMessage()));
        } finally {
            UnlockAfterDbCommitInfoHolder.setInfo2Holder(lockObject, orderId);
        }
    }

    private void updateOrderStatus(String orderId, Integer status) {

    }

    private boolean checkOrderIsInitStatus(String orderId) {
        return true;
    }

    @Getter
    @Setter
    private static class Order {
        private String orderId;

        private Integer status;
    }
}
