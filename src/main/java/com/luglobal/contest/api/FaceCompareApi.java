package com.luglobal.contest.api;

import com.alibaba.fastjson.JSON;
import com.luglobal.contest.annotation.LoginRequired;
import com.luglobal.contest.enums.ResultCode;
import com.luglobal.contest.gson.IntlResultGson;
import com.luglobal.contest.model.req.FaceCompareReq;
import com.luglobal.contest.service.FaceCompareService;
import com.luglobal.contest.service.ImgService;
import com.luglobal.contest.utils.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;


@RestController
@RequestMapping("/api/face")
public class FaceCompareApi {

    @Autowired
    private FaceCompareService faceCompareService;
    @Autowired
    private HttpServletRequest servletRequest;

    @LoginRequired
    @PostMapping("compare")
    public Object faceCompare(@RequestParam("param") String param) throws Exception{
        param= URLDecoder.decode(param);
        FaceCompareReq req = JSON.parseObject(param, FaceCompareReq.class);
        Tuple.Tuple2<ResultCode, Object>  result= faceCompareService.hModelFaceCompare(req, servletRequest);
        IntlResultGson<Object> rsGson = new IntlResultGson<>();
        rsGson.setRes_code(result.getA().getCode());
        rsGson.setRes_msg(result.getA().getDesc());
        rsGson.setData(result.getB());
        return rsGson;
    }

}
