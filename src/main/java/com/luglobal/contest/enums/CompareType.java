package com.luglobal.contest.enums;

/**
 * 人脸识别类型
 * Created by yangmingyang040 on 12/10/17.
 */
public enum CompareType {

    /**
     * 默认为1
     */
    LOGON("LOGON", "登陆"),


    IDENTITY("USER_MESSAGE", "开户"),

    HAND_UPLOAD("HAND_UPLOAD", "上传手持证件"),

    ;

    private String code;

    /**
     * 充值上限
     */
    private String desc;


    CompareType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static CompareType getByCode(String code){
        for(CompareType contentType : CompareType.values()){
            if(contentType.getCode().equalsIgnoreCase(code)) {
                return contentType;
            }
        }
        return null;
    }

}
