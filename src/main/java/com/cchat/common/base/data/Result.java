package com.cchat.common.base.data;

public class Result {

    private int code = -1;
    private String msg;
    
    //{"data":{},"code":0,"msg":"OK"}
    public Result() {
        // TODO Auto-generated constructor stub
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    
}
