package com.luglobal.contest.model.req;

/**
 * Created by lizehua035 on 2018/6/20.
 */

public class LandMark{
    private ImageExtInfoXY eye_left;
    private ImageExtInfoXY eye_right;
    private ImageExtInfoXY mouth_center;

    public LandMark(String flag){
        this.eye_left = new ImageExtInfoXY("1");
        this.eye_right = new ImageExtInfoXY("1");
        this.mouth_center = new ImageExtInfoXY("1");
    }
    public LandMark(){
        this.eye_left = new ImageExtInfoXY("1");
        this.eye_right = new ImageExtInfoXY("1");
        this.mouth_center = new ImageExtInfoXY("1");
    }

    public ImageExtInfoXY getEye_left() {
        return eye_left;
    }

    public void setEye_left(ImageExtInfoXY eye_left) {
        this.eye_left = eye_left;
    }

    public ImageExtInfoXY getEye_right() {
        return eye_right;
    }

    public void setEye_right(ImageExtInfoXY eye_right) {
        this.eye_right = eye_right;
    }

    public ImageExtInfoXY getMouth_center() {
        return mouth_center;
    }

    public void setMouth_center(ImageExtInfoXY mouth_center) {
        this.mouth_center = mouth_center;
    }
}
