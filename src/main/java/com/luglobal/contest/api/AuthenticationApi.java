package com.luglobal.contest.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.luglobal.contest.annotation.LoginRequired;
import com.luglobal.contest.enums.CompareType;
import com.luglobal.contest.enums.ImgTypeEnums;
import com.luglobal.contest.enums.ResultCode;
import com.luglobal.contest.gson.FaceLoginReqGson;
import com.luglobal.contest.gson.IntlResultGson;
import com.luglobal.contest.model.UserDTO;
import com.luglobal.contest.model.req.FaceCompareReq;
import com.luglobal.contest.service.AuthenticationService;
import com.luglobal.contest.service.FaceCompareService;
import com.luglobal.contest.service.UserService;
import com.luglobal.contest.utils.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;

@RestController
@RequestMapping("/api/")
public class AuthenticationApi {
    private AuthenticationService authenticationService;
    private UserService userService;
    @Autowired
    private FaceCompareService faceCompareService;
    @Autowired
    private HttpServletRequest servletRequest;
    @Autowired
    public AuthenticationApi(AuthenticationService authenticationService, UserService userService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    @PostMapping("authentication")
    public Object loginV2(@RequestBody String param) {
        UserDTO user=new UserDTO();
        if (param != null) {
            param= URLDecoder.decode(param);
            user = JSON.parseObject(param, UserDTO.class);
        }
        UserDTO userInDataBase = userService.findByName(user.getUsername());
        JSONObject jsonObject = new JSONObject();
        if (userInDataBase == null) {
            jsonObject.put("error", "用户不存在");
        } else if (!userService.comparePassword(user, userInDataBase)) {
            jsonObject.put("error", "密码不正确");
        } else {
            String token = authenticationService.getToken(userInDataBase);
            jsonObject.put("token", token);
            userInDataBase.setPassword(null);
            jsonObject.put("user", userInDataBase);
        }
        IntlResultGson resultGson = new IntlResultGson();
        resultGson.setData(jsonObject);
        return resultGson;
    }

    @PostMapping("authenticationV2")
    public Object login(@RequestParam(value = "param", required = false) String param) {
        UserDTO user=new UserDTO();
        if (param != null) {
            param= URLDecoder.decode(param);
            user = JSON.parseObject(param, UserDTO.class);
        }
        UserDTO userInDataBase = userService.findByName(user.getUsername());
        JSONObject jsonObject = new JSONObject();
        if (userInDataBase == null) {
            jsonObject.put("error", "用户不存在");
        } else if (!userService.comparePassword(user, userInDataBase)) {
            jsonObject.put("error", "密码不正确");
        } else {
            String token = authenticationService.getToken(userInDataBase);
            jsonObject.put("token", token);
            userInDataBase.setPassword(null);
            jsonObject.put("user", userInDataBase);
        }
        IntlResultGson resultGson = new IntlResultGson();
        resultGson.setData(jsonObject);
        return resultGson;
    }

    @PostMapping("face-authentication")
    public Object faceAuthentication(@RequestParam(value = "param", required = false) String param) throws Exception {
        FaceLoginReqGson req = JSON.parseObject(param, FaceLoginReqGson.class);
        UserDTO userInDataBase = userService.findByName(req.getUsername());
        JSONObject jsonObject = new JSONObject();
        if (userInDataBase == null) {
            jsonObject.put("error", "用户不存在");
        } else {
                FaceCompareReq faceCompareReq = new FaceCompareReq();
                faceCompareReq.setMediaId(String.valueOf(req.getImgId()));
                faceCompareReq.setType(CompareType.LOGON.getCode());
                Tuple.Tuple2<ResultCode, Object> compareResult = faceCompareService.hModelFaceCompare(faceCompareReq, servletRequest);
                if (!ResultCode.OK.equals(compareResult.getA())) {
                    jsonObject.put("error", "人脸识别不通过！");
                }
            //TODO 图片识别
            String token = authenticationService.getToken(userInDataBase);
            jsonObject.put("token", token);
            userInDataBase.setPassword(null);
            jsonObject.put("user", userInDataBase);
        }
        IntlResultGson resultGson = new IntlResultGson();
        resultGson.setData(jsonObject);
        return resultGson;
    }
}
