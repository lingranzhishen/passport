package com.luglobal.contest.service;


import com.luglobal.contest.dao.ImgInfoDao;
import com.luglobal.contest.enums.ResultCode;
import com.luglobal.contest.exception.CommonException;
import com.luglobal.contest.gson.IntlResultGson;
import com.luglobal.contest.model.ImgInfoDTO;
import com.luglobal.contest.utils.ConstantHelper;
import com.luglobal.contest.utils.PinganFileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class ImgService {

    private Logger logger = LoggerFactory.getLogger(ImgService.class);
    @Autowired
    private ImgInfoDao imgInfoDao;

    public ImgInfoDTO insertImg(ImgInfoDTO img) {
        imgInfoDao.insert(img);
        return img;
    }


    public IntlResultGson uploadImg(MultipartFile file) throws Exception {
        IntlResultGson<ImgInfoDTO> gson=new IntlResultGson();
        ImgInfoDTO imgInfoDTO=new ImgInfoDTO();
        if (!file.isEmpty()) {
            String contentType = file.getContentType();
            String[] imgTypes = {"image/jpeg", "image/gif", "image/jpeg", "image/png"};
            if (StringUtils.endsWithAny(contentType, imgTypes)) {
                String originalFilename = file.getOriginalFilename();
                String suffix = originalFilename.substring(originalFilename.lastIndexOf("."), originalFilename.length());
                String dateStr = LocalDate.now().toString();
                imgInfoDTO.setOriginName(originalFilename);
                String fileRelativePath = ConstantHelper.ROOT_DIR + dateStr + "/";
                String fileName=UUID.randomUUID().toString() + suffix;
                imgInfoDTO.setPath(fileRelativePath + fileName);
                imgInfoDTO.setPicSize(file.getSize());
                BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
                imgInfoDTO.setHeight(Long.valueOf(bufferedImage.getHeight()));
                imgInfoDTO.setWidth(Long.valueOf(bufferedImage.getWidth()));
                File targetDir = new File(fileRelativePath);
                if (!targetDir.exists()) {
                    targetDir.mkdirs();
                }
                try {
                    Files.copy(file.getInputStream(), Paths.get(imgInfoDTO.getPath()));
                } catch (IOException | RuntimeException e) {
                    throw new CommonException(ResultCode.UNKNOWN_ERROR);
                }

                String pinganImgId = PinganFileUtils.uploadFile(file.getBytes(), fileName);
                imgInfoDTO.setPinganId(pinganImgId);

            }
            insertImg(imgInfoDTO);
            gson.setData(imgInfoDTO);
        }

        return gson;

    }



    public ImgInfoDTO selectImg(Long id) {
        return imgInfoDao.selectById(id);
    }

    public IntlResultGson uploadBase64Img(String file) {
        IntlResultGson<ImgInfoDTO> gson=new IntlResultGson();
        ImgInfoDTO imgInfoDTO=new ImgInfoDTO();
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] b={};
        String ext="";
        try {
            // 解密
            b = decoder.decodeBuffer(file);
            // 处理数据
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            ext=getFileExtendName(b);
                String dateStr = LocalDate.now().toString();
                String fileRelativePath = ConstantHelper.ROOT_DIR + dateStr + "/";
            String fileName=UUID.randomUUID().toString() + ext;
                imgInfoDTO.setPath(fileRelativePath + fileName);
                imgInfoDTO.setPicSize(Long.valueOf(b.length));
                File targetDir = new File(fileRelativePath);
                if (!targetDir.exists()) {
                    targetDir.mkdirs();
                }
            Files.copy(new ByteArrayInputStream(b), Paths.get(imgInfoDTO.getPath()));

            String pinganImgId = PinganFileUtils.uploadFile(b, fileName);
            imgInfoDTO.setPinganId(pinganImgId);
            insertImg(imgInfoDTO);
            gson.setData(imgInfoDTO);
        }catch (Exception e){

        }

        return gson;
    }

    /**
     * 根据得到图片字节，获得图片后缀
     *
     * @param photoByte 图片字节
     * @return 图片后缀
     */
    public static String getFileExtendName(byte[] photoByte) {
        String strFileExtendName = ".jpg";
        if ((photoByte[0] == 71) && (photoByte[1] == 73) && (photoByte[2] == 70)
                && (photoByte[3] == 56) && ((photoByte[4] == 55) || (photoByte[4] == 57))
                && (photoByte[5] == 97)) {
            strFileExtendName = ".gif";
        } else if ((photoByte[6] == 74) && (photoByte[7] == 70) && (photoByte[8] == 73)
                && (photoByte[9] == 70)) {
            strFileExtendName = ".jpg";
        } else if ((photoByte[0] == 66) && (photoByte[1] == 77)) {
            strFileExtendName = ".bmp";
        } else if ((photoByte[1] == 80) && (photoByte[2] == 78) && (photoByte[3] == 71)) {
            strFileExtendName = ".png";
        }
        return strFileExtendName;
    }


}
