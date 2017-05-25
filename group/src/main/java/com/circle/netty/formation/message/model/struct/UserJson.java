package com.circle.netty.formation.message.model.struct;


import com.circle.netty.formation.message.model.User;
import com.circle.netty.http.BackModel;
import org.apache.commons.lang.StringUtils;

/**
 * @author Created by cxx on 2015/7/16.
 */
public class UserJson {
    private static UserJson userJson;
    private UserJson(){
    }

    public static UserJson create(){
        if (userJson == null) {
            userJson = new UserJson();
        }
        return userJson;
    }

    public BackModel simple_rob_user_info(User user) {
        BackModel model = new BackModel();
        model.add(UserStruct.uid,user.uid());
        model.add(UserStruct.nn,user.getName());//个人说明
        model.add(UserStruct.uurl, StringUtils.isEmpty(user.getUurl())?"":user.getUurl());
        model.add(UserStruct.sex,user.getSex());//性别
        model.add(UserStruct.isava,user.getIsava());//头像验证
        model.add(UserStruct.iscom,user.getIscom());//公司验证
        model.add(UserStruct.ascore, user.getAscore());//回答评价得分数
        model.add(UserStruct.anum, user.getAnum());//回答次数
        model.add(UserStruct.mobile, user.getMobile());//手机号
        return model;
    }
}
