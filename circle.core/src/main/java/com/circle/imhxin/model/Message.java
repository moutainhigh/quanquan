package com.circle.imhxin.model;

/**
 * 系统消息-通信
 * @author Created by cxx on 15-7-24.
 */
public class Message<T> extends SystemMessage<T>{
    private String from;

    public Msg create(){
        return new Msg();
    }

    public class Msg extends SystemMessage.Msg{
        private String msg;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
