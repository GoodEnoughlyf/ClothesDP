package com.liyifu.clothesdp.common;

/**
 * 返回工具类
 */
public class ResultUtils {
    /**
     * 返回成功1
     * @param data 返回数据
     * @return
     * @param <T>
     */
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(200,data,"ok");
    }

    /**
     * 返回成功2
     * @return
     * @param <T>
     */
    public static <T> BaseResponse<T> success(){
        return new BaseResponse<>(200,null,"ok");
    }


    /**
     * 返回失败
     * @param code
     * @param message
     * @return
     * @param <T>
     */
    public static <T> BaseResponse<T> error(int code,String message){
        return new BaseResponse<>(code,null,message);
    }
}
