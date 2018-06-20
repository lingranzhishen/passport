package com.luglobal.contest.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.luglobal.contest.config.UserContext;
import com.luglobal.contest.dao.UserIdentityDao;
import com.luglobal.contest.enums.CompareType;
import com.luglobal.contest.enums.ImageCategory;
import com.luglobal.contest.enums.ResultCode;
import com.luglobal.contest.model.*;
import com.luglobal.contest.model.req.*;
import com.luglobal.contest.model.resp.BioDetectResp;
import com.luglobal.contest.model.resp.HFaceCompareResp;
import com.luglobal.contest.utils.*;
import com.pingan.pama.protocol.SignUtils;
import com.pingan.pama.protocol.encrypt.AESUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * Created by ZHAOZENGJIE004 on 2018/4/17.
 */
@Service
public class FaceCompareService extends InvokeUtil {

    private Logger logger = LoggerFactory.getLogger(FaceCompareService.class);
    @Autowired
    private UserService userService;

    @Autowired
    private ImgService imgService;

    @Autowired
    private UserIdentityDao userIdentityDao;

    private String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDJwL/ViPsIfq+Dn5tKnfWErRWBQeLdpUzc78NHZ/QNpp/IpbpLQ6VbwfyswBEaHiP9fWKGGkyVtnlYo6eQMyiUEarJHP29ukSzXuqCGJRhJKPRJ/fN/bWEXd2i+sKvSpsH79eZBbyrPr4VtM0cHLuGrpE92+HiMn4ifc5wAH648tXPhBDcLdQffOgZ9wOzmePJBIpzXintbv6iva1+yEQElgolDklBzJVa3C8ENyztFZoSE2yfs0YVPSd/BXad45vsL3lf2JX1oWk6IIuZlNQsQ/DNnG+jfdIAUW5TiWXdarPXL8IqD3aHX5hsH8StHFb/2YQgrFUDPFID2VxABHcLAgMBAAECggEAQn9ue1Jf84fWD9sukySHRQjLmsP9o7KVKAk64zFHRqyR48+EHMSaHylMQCA6QtKGaEnIjIzki0AOtQGT/DpweIVuNkWA+OUJOdniD6lMVsx91jWHPvUNvCg4IjzmqGI9wRrzg/NZIVRX0M28ATUTNiwZoTr1M9oSqwmuXko7enex4s8JqhCE8Nu6zFQVmvqy55qvG9oLpRT/akjBeKJKwm2TKcDRc1CAUKSpkk6sAK/MvhKas+VdPwT3v7f5yQmNYTIv3KaXhrIoKG4rk8WW1FM62fUwq5+C8roD77yScZfBZuRplM1V/bdUzhqqHErSPBmqXOm5L6rHGlx82D4hCQKBgQDjwteGjbqxPpm3//dInG15sWT4SSWagOnJulDeXf2uYWRe3Wa7XYG8402yXgJcHX3EJ2Ake/FRhFik9kXKfpMDqBcdP5SYytaQZnwZXQEu8iLnR/JQxJK+VKRo8QSf+XqiFVoD4qyqmggtKhEXmiYNNdUe7I9gTFeC0biRMtFUtwKBgQDixGfYMSax/YYrzIEx+2b8JObTC2HMAZokXDLJuytScaPHO7Vn8TwMNUXDx/fBC1+YmYPI0LDpVpfw/fYce//3JogCo4Z19s3xIHVoh/X+BJ6lpiL5BRnb6EuzvoCfd2zE0ptSlWFdctq8se2Vrhu4YuyCbAnTpQjH4cTuaD3kTQKBgQC3Jgrh7D5gIRLDX1WeUgvGe9vRvCA86cUVrnkxFyvTegWYMzlVGZAmMDJeaDJFmIF1pqQtMm2PO3l8zXy5pKbbneNYZbQ8WX/IhmDslFlLQvLGQJieUjQeUToUGRu6+Iagp8LGtRRb65j/mIA+FnrazJsgTuHMchc83yAnwlGKaQKBgBCp/jeU5RMPXly5moThxJ+i1pOLspQbSgiB/fl1F6nnT/HEIYCR6ae8AnRwrXQyMcQ1A8ouC9IZ2vCKuRs+wYCYBEatXaQtjtS5XedglmMkU8Q4gTlrdosCdw2uYiVV0VhlYaSf9Ze4aPqU2LC3KrJSEq9C4TX8WrjVjnM2ldXBAoGBAJMWK2JQF4aGWgKVeOU8+TV6jFSR4H/sRbBkMbEKDCZzLQaAT+W0DLOAL7S8jQhrN7qdYnejnOzFhErSmbznwfNZnkt66TTOMwE4wuT/m1X1lTOmmck14K8/7PKm6GLiwDxqs1J2GDh1e6dh2Pz+7rS5ZWmaacyatfjShnv1Zfc+";
    private String eKey = "M1GAZvW+heTlGmrsq+5iKg==";
    private String pubKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlx2qSZJ1MfyooSDijmYbc+OM+mqZsg4AVl/VwhNtOpNHuh2gSO+kvC8SSMekeV9eVqbOPEw39I8rOqT3FrbwObIs6EdgQIrLcLc39ngdV7H342kApyKKS82YD/r4oAVAyoY+k2gqcIStAOC120A9oG/Ykud7as2LvpDM3gnXFWnoR7d+XYaFxSknASQOOuekoHQrQtDpILgRVxvGVyVmN6W5d6TFtG9QNjf7xIxIFIXa0iGD4yL/UPzbXPrER6W0+FRxmLcwLKwUXLn1xiOFjoFiCIMAuTsVr5Cq82gY8lLe5JwwSl/5MnXvfahHxvIeYMLth0/UNd+uNGHECHxCRQIDAQAB";
    private String url = "https://pama-open.pingan.com/api";




    public Tuple.Tuple2<ResultCode, Object>  hModelFaceCompare(FaceCompareReq req, HttpServletRequest servletRequest) throws Exception{
        return hModelFaceCompare(req,null,servletRequest);
    }

    /**
     * H-MODEL人脸比对
     */
    public Tuple.Tuple2<ResultCode, Object>  hModelFaceCompare(FaceCompareReq req,Long handImgId, HttpServletRequest servletRequest) throws Exception{

        Map<String, Object> resultMap = new HashMap<String, Object>();
        //1.用户检查
        UserDTO userDTO = UserContext.getUser();
        if (userDTO == null){
            userDTO = userService.findByName(req.getUsername());
        }
        if (userDTO == null){
            return Tuple.tuple(ResultCode.FAIL, resultMap);
        }

        //2、检查开关

        //3、 todo 准备所有调用接口的参数， 图片尺寸等
        FaceCompareParamDto faceCompareParamDto = this.getTerminalParam(req, servletRequest);

        faceCompareParamDto.setEntityAuthCode("8889");
        faceCompareParamDto.setEntityAuthDate(DateUtils.getDate());
        faceCompareParamDto.setOperationType("R0200W05");
        faceCompareParamDto.setPlatId("8889");
        faceCompareParamDto.setChannelBizNo(UUID.randomUUID().toString());
        faceCompareParamDto.setChannelDate(DateUtils.getDate());
        faceCompareParamDto.setIdType("1");//todo 不是身份证，需要确定传什么
        faceCompareParamDto.setIdNumber("411628198909202214");
        faceCompareParamDto.setCustName("杨明洋");
        //4.获取手机自拍照(人脸快照)
        ImgInfoDTO imgInfoDTO = imgService.selectImg(Long.parseLong(req.getMediaId()));
        if (imgInfoDTO == null){
            return Tuple.tuple(ResultCode.FAIL, resultMap);
        }
        //人脸1
        faceCompareParamDto.setFile_image1(imgInfoDTO.getPinganId());
        faceCompareParamDto.setImage1Category("1");//自拍照片
        faceCompareParamDto.setImage1CrossMark("0");//无网文
        faceCompareParamDto.setQualityTerminal1(JSON.toJSONString(new QualityTerminal()));
        faceCompareParamDto.setImage1Type("jpg");
//        faceCompareParamDto.setImage1Mark();

        //5、获取需要比对的图片 image2（baseImage)
        UserIdentityDTO identityDto= userIdentityDao.selectByUserId(userDTO.getUserId());
        if(handImgId==null) {
            handImgId = identityDto.getHandImgId();
        }
        Long passportImgId = identityDto.getPassportImgId();
        String pinganHandImgId = imgService.selectImg(handImgId).getPinganId();
        String pinganPassportImgId = imgService.selectImg(passportImgId).getPinganId();

        if (CompareType.IDENTITY.getCode().equals(req.getType()) || CompareType.LOGON.getCode().equals(req.getType())){
            BioDetectRemoteReq remoteReq = new BioDetectRemoteReq();
            remoteReq.setChannel("666701");
            remoteReq.setChannelSecond("8889");
            remoteReq.setChannelJnlNo("790904095345220786910698");
            remoteReq.setChannelDate(DateUtils.getDate());
            remoteReq.setIdentityType("R0200W12");

            BioDetectParamDetail extData = new BioDetectParamDetail();
            extData.setTerminalType("PC");
            extData.setTerminalSDK("o236");
            extData.setTerminalBrowser("ie");
            extData.setTerminalSystem("ie");
            extData.setIdNo("430204199702087719");
            extData.setChannelBizNo(UUID.randomUUID().toString());
            extData.setImageFileId1(imgInfoDTO.getPinganId());
            extData.setImage1Type("jpg");
            extData.setImage1Category(ImageCategory.PHONE_IMG.getCode());
            remoteReq.setExtDataStr(JSON.toJSONString(extData));
            if(!bioDetect(remoteReq)){
                return Tuple.tuple(ResultCode.USER_FACE_COMPARE_NOT_ALIVE, resultMap);
            }
        }
        String image2 = null;
        if (CompareType.IDENTITY.getCode().equals(req.getType())){
            image2 = pinganHandImgId;
        } else if (CompareType.LOGON.getCode().equals(req.getType())){
            image2 = imgService.selectImg(identityDto.getBaseImgId()).getPinganId();
        } else if (CompareType.HAND_UPLOAD.getCode().equals(req.getType())){
            image2 = imgService.selectImg(identityDto.getPassportImgId()).getPinganId();
        }
        //网文1
        faceCompareParamDto.setFile_image2(image2);
      String imge2Cat = CompareType.HAND_UPLOAD.getCode().equals(req.getType()) ? ImageCategory.PASSPORT_IMG.getCode():ImageCategory.ID_IMG.getCode();
        faceCompareParamDto.setImage2Category(imge2Cat);//自拍照片
        faceCompareParamDto.setImage2CrossMark("0");//无网文
        faceCompareParamDto.setQualityTerminal2(JSON.toJSONString(new QualityTerminal()));
        faceCompareParamDto.setImage2Type("jpg");
        faceCompareParamDto.setImage1Mark("1");
        faceCompareParamDto.setImage2Mark("1");
        faceCompareParamDto.setQualityTerminal2(JSON.toJSONString(new QualityTerminal()));
        if(req.getMessage()!=null) {
            faceCompareParamDto.setLandmarkTerminal1(JSON.toJSONString(req.getMessage().getLandmark_terminal()));
        }else{
            faceCompareParamDto.setLandmarkTerminal1(JSON.toJSONString(new LandMark()));
        }
        faceCompareParamDto.setLandmarkTerminal2(JSON.toJSONString(new LandMark()));

        if (StringUtils.isBlank(pinganHandImgId) || StringUtils.isBlank(pinganPassportImgId)){
            return Tuple.tuple(ResultCode.FAIL, resultMap);
        }

        //6、调用科技H模型人脸比对接口
        Tuple.Tuple2<ResultCode, Object> compareResult = this.invoikefaceCompareApi(faceCompareParamDto);
        if (!ResultCode.OK.equals(compareResult.getA())){
            logger.info("比对失败"+JSON.toJSONString(compareResult.getA()));
            return Tuple.tuple(compareResult.getA(), resultMap);
        }
        //7、保存成功的人脸图片，以供下次人脸比对使用
        if(!CompareType.HAND_UPLOAD.getCode().equals(req.getType())) {
            this.saveSuccessFaceImage(identityDto.getId(), req.getMediaId(), req.getType());
        }


        return Tuple.tuple(ResultCode.OK, resultMap);
    }



    public void saveSuccessFaceImage(Long id, String faceImageId, String type){
        String status = "USER_MESSAGE".equals(type) ? "1" : "0";
        UserIdentityDTO identityDTO = new UserIdentityDTO();
        identityDTO.setId(id);
        identityDTO.setFaceImgId(Long.parseLong(faceImageId));
        identityDTO.setBaseImgId(Long.parseLong(faceImageId));
        userIdentityDao.updateSelective(identityDTO);
    }


    /**
     * 获取终端信息
     * @param req
     * @return
     */
    private FaceCompareParamDto getTerminalParam(FaceCompareReq req, HttpServletRequest servletRequest){
/*
        HttpRequestInfoDto requestInfo = HttpRequestUtils.getMobileHttpRequestInfo(servletRequest);
*/
        FaceCompareParamDto faceCompareTerminal = new FaceCompareParamDto();
        faceCompareTerminal.setTerminalSDK("o236");
        faceCompareTerminal.setTerminalType("android");
        faceCompareTerminal.setTerminalSystem("android");
        faceCompareTerminal.setTerminalBrowser("ie");
        //faceCompareTerminal.setua(ua);

        return faceCompareTerminal;
    }



    public Tuple.Tuple2<ResultCode, Object> invoikefaceCompareApi(FaceCompareParamDto faceCompareParamDto) throws Exception {
        String service = "pama_std_fep.identityAuth";
        Map resultMap = new HashMap();
        Map<String, Object> param = MapUtils.toMap(faceCompareParamDto);

        Map<String, Object> map = SignUtils.makeSignedMsgMapV2(param, "M070000008", "PRD21800000409", service, "1.0", eKey, "AES", "1.0", privateKey, "SHA256WithRSA");

        String res = sendJson(url, JSON.toJSONString(map));
        JSONObject obj = new JSONObject();
        HashMap params = obj.parseObject(res, HashMap.class);
        String resString = AESUtils.decrypt(params.get("bizContent").toString(), eKey);
        HashMap biz = obj.parseObject(resString, HashMap.class);
        logger.info(biz.get("bizContent").toString());
        HFaceCompareResp hFaceCompareResp = JSON.parseObject(biz.get("bizContent").toString(), HFaceCompareResp.class);
        boolean isSuccess = identityAuthQuery(hFaceCompareResp.getChannelBizNo(),hFaceCompareResp.getAuthNo());
        if (!isSuccess){
            return Tuple.tuple(ResultCode.FAIL, resultMap);
        }

        return Tuple.tuple(ResultCode.OK, resultMap);

    }


    public boolean identityAuthQuery(String channelBizNo,String authNo) throws Exception {

       for (int i=0; i<20;i++) {
           String res = callAuthQuery(channelBizNo,authNo);
           if ("0".equals(res)){
               Thread.sleep(500);
               continue;
           } else {
               return "1".equals(res);
           }
       }
       return false;
    }

    public String callAuthQuery(String channelBizNo,String authNo)throws Exception{
        String service = "pama_pauth.identityAuthQuery";
        Map resultMap = new HashMap();
        Map<String, Object> param = new HashMap<>();
        param.put("channelBizNo", channelBizNo);
        param.put("authNo", authNo);
        param.put("channel","666701");
        param.put("channelSecond","666701");
        Map<String, Object> map = SignUtils.makeSignedMsgMapV2(param, "M070000008", "PRD21800000409", service, "1.0", eKey, "AES", "1.0", privateKey, "SHA256WithRSA");

        String res = sendJson(url, JSON.toJSONString(map));
        JSONObject obj = new JSONObject();
        HashMap params = obj.parseObject(res, HashMap.class);
        String resString = AESUtils.decrypt(params.get("bizContent").toString(), eKey);
        params = obj.parseObject(resString, HashMap.class);
        resString = params.get("bizContent").toString();
        logger.info(resString);
        HFaceCompareResp hFaceCompareResp = JSON.parseObject(resString, HFaceCompareResp.class);
        return hFaceCompareResp.getAuthResult();
    }


    public boolean bioDetect(BioDetectRemoteReq remoteReq) throws Exception {

        String service = "pama_core_ident_verify.identityVerity";

        Map<String, Object> param = MapUtils.toMap(remoteReq);
        Map<String, Object> map = SignUtils.makeSignedMsgMapV2(param, "M070000008", "PRD11100000006", service, "1.0", eKey, "AES", "1.0", privateKey, "SHA256WithRSA");
        logger.info("pama_core_ident_verify.identityVerity_param="+JSON.toJSONString(param));

        String res = sendJson(url, JSON.toJSONString(map));

        if(StringUtils.isBlank(res)){
            return true;
        }
        JSONObject obj = new JSONObject();
        HashMap params = obj.parseObject(res, HashMap.class);
        String resString = AESUtils.decrypt(params.get("bizContent").toString(), eKey);
        logger.info("pama_core_ident_verify.identityVerity"+resString);
        BioDetectResp bioDetectResp = JSON.parseObject(resString, BioDetectResp.class);
        if (bioDetectResp != null && "0".equals(bioDetectResp.getResponseCode()) && bioDetectResp.getResultData() != null && "Y".equals(bioDetectResp.getResultData().getIsAlive())){
            return true;
        }
        return true;
    }
}
