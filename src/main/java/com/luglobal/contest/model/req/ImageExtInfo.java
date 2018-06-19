package com.luglobal.contest.model.req;

import com.luglobal.contest.model.QualityTerminal;
import com.luglobal.contest.utils.ConstantHelper;

/**
 * Created by fengfei754 on 7/25/16.
 */
public class ImageExtInfo {
    private String category;
    private String mark;
    private LandMark landmark_terminal;
    private QualityTerminal quality_terminal;
    private String width;
    private String height;
    private String content_type;
    private String data;
    /**
     * H model  专用
     */
    // 是否需要去网纹
    private boolean eraseMark;




    public ImageExtInfo(String category, String data) {
        this.category = category;
        this.data = data;
        this.content_type = ConstantHelper.JPG;
    }

    public ImageExtInfo(){
    }

    public ImageExtInfo(String flag){
        this.category = "2";
        if("2".equals(flag)){
            this.mark = "0";
        }else {
            this.mark = "1";
        }
        this.width = "0";
        this.height = "0";
        this.content_type = "jpeg";
        this.landmark_terminal = new LandMark("1");
        this.quality_terminal = new QualityTerminal("1");
    }



    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public LandMark getLandmark_terminal() {
        return landmark_terminal;
    }

    public void setLandmark_terminal(LandMark landmark_terminal) {
        this.landmark_terminal = landmark_terminal;
    }

    public QualityTerminal getQuality_terminal() {
        return quality_terminal;
    }

    public void setQuality_terminal(QualityTerminal quality_terminal) {
        this.quality_terminal = quality_terminal;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isEraseMark() {
        return eraseMark;
    }

    public void setEraseMark(boolean eraseMark) {
        this.eraseMark = eraseMark;
    }

}
