package com.luglobal.contest.model.req;

/**
 * Created by lizehua035 on 2018/6/20.
 */

public class ImageExtInfoXY{
    private String x;
    private String y;

    public ImageExtInfoXY(){ }
    public ImageExtInfoXY(String flag){
        this.x = "0";
        this.y = "0";
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }
}
