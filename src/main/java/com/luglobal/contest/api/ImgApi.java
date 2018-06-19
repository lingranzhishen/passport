package com.luglobal.contest.api;


import com.luglobal.contest.enums.ResultCode;
import com.luglobal.contest.exception.CommonException;
import com.luglobal.contest.gson.IntlResultGson;
import com.luglobal.contest.model.ImgInfoDTO;
import com.luglobal.contest.service.ImgService;
import com.luglobal.contest.utils.ConstantHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsFileUploadSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

@RestController
@RequestMapping("/api/img")
public class ImgApi {


    @Autowired
    private ImgService imgService;

    @PostMapping("uploadV2")
    public Object upload(@RequestParam("file") MultipartFile file) throws Exception{
        IntlResultGson result= imgService.uploadImg(file);
        return result;
    }

    @PostMapping("upload")
    public Object uploadBase(@RequestParam("param") String param) throws Exception{
        IntlResultGson result= imgService.uploadBase64Img(param);
        return result;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id:.+}")
    public void getFile(@PathVariable Long id, HttpServletResponse httpServletResponse) {
        try {
            ImgInfoDTO dto=imgService.selectImg(id);
            if(dto==null){
                throw new CommonException(ResultCode.BAD_DATA);
            }
            File image = new File(dto.getPath());
            FileInputStream inputStream = new FileInputStream(image);
            int length = inputStream.available();
            byte data[] = new byte[length];
            httpServletResponse.setContentLength(length);
            String fileName = image.getName();
            String fileType = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            httpServletResponse.setContentType(ConstantHelper.getContentTypeByExpansion(fileType));
            inputStream.read(data);
            OutputStream toClient = httpServletResponse.getOutputStream();
            toClient.write(data);
            toClient.flush();
        } catch (Exception e) {
            throw new CommonException(ResultCode.BAD_DATA);
        }
    }
}
