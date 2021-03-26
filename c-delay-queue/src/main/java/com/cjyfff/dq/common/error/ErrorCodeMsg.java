package com.cjyfff.dq.common.error;

/**
 * Created by jiashen on 19-1-2.
 */
public class ErrorCodeMsg {
    public static String SYSTEM_ERROR_CODE = "-1";
    public static String SYSTEM_ERROR_MSG = "system error";

    public static String TASK_IS_PROCESSING_CODE = "-800";
    public static String TASK_IS_PROCESSING_MSG = "This task is processing...";

    public static String PARAM_VALIDATE_ERROR_CODE = "-100";
    public static String PARAM_VALIDATE_ERROR_MSG = "Parameters validate get error";

    public static String IS_NOT_MY_TASK_CODE = "-101";
    public static String IS_NOT_MY_TASK_MSG = "This task is not my task.";

    public static String ELECTION_NOT_FINISHED_CODE = "-102";
    public static String ELECTION_NOT_FINISHED_MSG = "Election is not finished yet, request reject...";

    public static String TASK_ID_EXIST_CODE = "-103";
    public static String TASK_ID_EXIST_MSG = "A same task id is exist, task can not create.";

    public static String CAN_NOT_GET_SHARDING_INFO_CODE = "-104";
    public static String CAN_NOT_GET_SHARDING_INFO_MSG = "Can not get sharding info.";

    public static String CAN_NOT_FIND_HANDLER_BY_SPECIFIED_FUNCTION_NAME_CODE = "-105";
    public static String CAN_NOT_FIND_HANDLER_BY_SPECIFIED_FUNCTION_NAME_MSG = "Can not find handler by specified function name.";

    public static String SEND_INNER_TASK_GET_ERROR_CODE = "-106";
    public static String SEND_INNER_TASK_GET_ERROR_MSG = "Send inner task get error.";

    public static String CAN_NOT_FIND_TASK_ERROR_CODE = "-107";
    public static String CAN_NOT_FIND_TASK_ERROR_MSG = "Can not find task.";

    public static String REACH_ACCESS_LIMIT_ERROR_CODE = "-108";
    public static String REACH_ACCESS_LIMIT_ERROR_MSG = "Reach access limit.";
}
