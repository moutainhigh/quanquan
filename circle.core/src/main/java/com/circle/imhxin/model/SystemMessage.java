package com.circle.imhxin.model;

import com.circle.imhxin.model.type.MsgType;

import java.util.List;

/**
 * 系统消息-通信
 * @author Created by cxx on 15-7-24.
 */
public class SystemMessage<T> {

    public Msg create(){
        return new Msg();
    }
    public class Msg{
        private String type = MsgType.CMD;

        private String action;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getAction() {
            return action;
        }
        public void setAction(String action) {
            this.action = action;
        }
    }
    private String target_type;
    private List<String> target;
    private Msg msg;
    private T ext;

    public String getTarget_type() {
        return target_type;
    }

    public void setTarget_type(String target_type) {
        this.target_type = target_type;
    }

    public List<String> getTarget() {
        return target;
    }

    public void setTarget(List<String> target) {
        this.target = target;
    }

    public Msg getMsg() {
        return msg;
    }

    public void setMsg(Msg msg) {
        this.msg = msg;
    }

    public T getExt() {
        return ext;
    }

    public void setExt(T ext) {
        this.ext = ext;
    }
}
