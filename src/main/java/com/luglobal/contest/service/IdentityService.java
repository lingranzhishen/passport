package com.luglobal.contest.service;


import com.alibaba.fastjson.JSON;
import com.luglobal.contest.config.UserContext;
import com.luglobal.contest.dao.ImgInfoDao;
import com.luglobal.contest.dao.UserIdentityDao;
import com.luglobal.contest.enums.CompareType;
import com.luglobal.contest.enums.IdentityStatusEnums;
import com.luglobal.contest.enums.ImgTypeEnums;
import com.luglobal.contest.enums.ResultCode;
import com.luglobal.contest.exception.CommonException;
import com.luglobal.contest.gson.*;
import com.luglobal.contest.model.ImgInfoDTO;
import com.luglobal.contest.model.UserDTO;
import com.luglobal.contest.model.UserIdentityDTO;
import com.luglobal.contest.model.req.FaceCompareReq;
import com.luglobal.contest.utils.Tuple;
import org.apache.catalina.startup.UserConfig;
import org.apache.catalina.util.URLEncoder;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class IdentityService {
    @Autowired
    private UserIdentityDao userIdentityDao;
    @Autowired
    private FaceCompareService faceCompareService;
    @Autowired
    private HttpServletRequest servletRequest;



    public IntlResultGson addTask(String param) throws Exception{
        param= URLDecoder.decode(param);
        IntlResultGson gson = new IntlResultGson();
        IdentityReqGson req = JSON.parseObject(param, IdentityReqGson.class);
        UserDTO user = UserContext.getUser();
        if(ImgTypeEnums.HAND_IMG.name().equals(req.getType())) {
            FaceCompareReq faceCompareReq = new FaceCompareReq();
            faceCompareReq.setMediaId(String.valueOf(req.getImgId()));
            faceCompareReq.setType(CompareType.HAND_UPLOAD.getCode());
            Tuple.Tuple2<ResultCode, Object> compareResult = faceCompareService.hModelFaceCompare(faceCompareReq,req.getImgId(), servletRequest);
            if (!ResultCode.OK.equals(compareResult.getA())) {
                gson.setRes_code(ResultCode.FAIL.getCode());
                gson.setRes_msg("手持证件照和证件匹配失败，请重新上传！");
                return gson;
            }
        }
        UserIdentityDTO identityDto = userIdentityDao.selectByUserId(user.getUserId());
        if (identityDto == null) {
            identityDto = new UserIdentityDTO();
            identityDto.setStatus(IdentityStatusEnums.NEW.name());
            identityDto.setUserId(user.getUserId());
        }
        if (ImgTypeEnums.FACE.name().equals(req.getType())) {
            identityDto.setFaceImgId(req.getImgId());
        } else if (ImgTypeEnums.HAND_IMG.name().equals(req.getType())) {
            identityDto.setHandImgId(req.getImgId());
        } else if (ImgTypeEnums.PASSPORT.name().equals(req.getType())) {
            identityDto.setPassportImgId(req.getImgId());
        } else {
            throw new CommonException(ResultCode.BAD_DATA);
        }
        if (identityDto.getId() == null) {
            userIdentityDao.insert(identityDto);
        } else {
            userIdentityDao.updateSelective(identityDto);
        }
        return gson;
    }

    public IntlResultGson listTask(IdentityListReqGson reqGson) {
        IntlResultGson gson = new IntlResultGson();
        long totalCount = userIdentityDao.countUserIdentity();
        PaginationGson<UserIdentityDTO> paginationGson = new PaginationGson<UserIdentityDTO>(totalCount, reqGson.getPage());
        Map<String, Object> map = new HashMap<>();
        map.put("offset", paginationGson.getOffset());
        map.put("limit", paginationGson.getLimit());
        List<UserIdentityDTO> data = userIdentityDao.listIdentity(map);
        paginationGson.setData(data);
        gson.setData(paginationGson);
        return gson;
    }

    public IntlResultGson approveIdentity(IdentityApproveGson req) {
        IntlResultGson gson = new IntlResultGson();
        UserIdentityDTO identityDto = userIdentityDao.selectByUserId(req.getUserId());
        if (identityDto == null) {
            throw new CommonException(ResultCode.NOT_EXIST);
        }
        if (IdentityStatusEnums.PASS.name().equals(req.getResult())) {
            //TODO
        } else {

        }
        identityDto.setStatus(req.getResult());
        identityDto.setMemo(req.getMemo());
        identityDto.setCreatedBy(UserContext.getUser().getUsername());
        userIdentityDao.updateSelective(identityDto);
        return gson;
    }

    public IntlResultGson detail(Long id) {
        IntlResultGson gson = new IntlResultGson();
        UserIdentityDTO identityDto = userIdentityDao.selectByUserId(id);
        gson.setData(identityDto);
        return gson;
    }

}
