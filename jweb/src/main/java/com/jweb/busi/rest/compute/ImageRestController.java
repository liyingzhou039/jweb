package com.jweb.busi.rest.compute;

import com.jweb.busi.entity.compute.Image;
import com.jweb.busi.service.compute.ImageService;
import com.jweb.common.exception.BusiException;
import com.jweb.common.service.BeanService;
import com.jweb.common.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;

@RestController
@RequestMapping("/rest/compute/image")
public class ImageRestController {
    @Autowired
    protected ImageService imageService;
    @Autowired
    BeanService beanService;

    @RequestMapping(value = "",method = RequestMethod.POST)
    @ResponseBody
    public Object create(
            @RequestBody Image image
    ){
        Result<?> r=new Result<>();
        try {
            imageService.create(image);
            r.setOk(true);
        } catch (Exception e) {
            r.setOk(false);
            r.setMsg(e.getMessage());
        }
        return r;
    }

    @RequestMapping(value = "",method = RequestMethod.PUT)
    @ResponseBody
    public Object update(
            @RequestBody Image image
    ){
        Result<?> r=new Result<>();
        try {
            imageService.update(image);
            r.setOk(true);
        } catch (Exception e) {
            r.setOk(false);
            r.setMsg(e.getMessage());
        }
        return r;
    }

    @RequestMapping(value = "/{imageId}",method = RequestMethod.DELETE)
    @ResponseBody
    public Object remove(@PathVariable String imageId){
        Result<?> r = new Result<>();
        try {
            imageService.remove(imageId);
            r.setOk(true);
            r.setMsg("删除成功");
        }catch(Exception e) {
            r.setOk(false);
            r.setMsg(e.getMessage());
        }
        return r;
    }

    @RequestMapping(value = "/uploadImageFile",method = RequestMethod.POST)
    @ResponseBody
    public Object uploadImageFile(MultipartFile imageFile){
        Result<?> r = new Result<>();
        try {
            if(null==imageFile) {
                throw new BusiException("文件不能为空");
            }
            imageService.uploadImageFile(imageFile);
            r.setOk(true);
            r.setMsg("上传成功");
        }catch(Exception e) {
            r.setOk(false);
            r.setMsg(e.getMessage());
        }
        return r;
    }

    @RequestMapping(value = "/removeImageFile/{fileName:.+}",method = RequestMethod.DELETE)
    @ResponseBody
    public Object removeImageFile(@PathVariable String fileName){
        Result<?> r = new Result<>();
        try {
            fileName = URLDecoder.decode(fileName,"UTF-8");
            imageService.removeImageFile(fileName);
            r.setOk(true);
            r.setMsg("删除成功");
        }catch(Exception e) {
            r.setOk(false);
            r.setMsg(e.getMessage());
        }
        return r;
    }

    @RequestMapping(value = "/listImageFile",method = RequestMethod.GET)
    @ResponseBody
    public Object listImageFile(){
        return imageService.listImageFile();
    }

    @RequestMapping(value= "/downloadImageFile/{imageId}",method = RequestMethod.GET)
    public void downloadImageFile(HttpServletResponse res, @PathVariable String imageId){
        BufferedInputStream bis = null;
        OutputStream os = null;
        try {
            Image image = beanService.getById(Image.class,imageId);
            res.setHeader("content-type", "application/octet-stream");
            res.setContentType("application/octet-stream");
            res.setHeader("Content-Disposition", "attachment;filename=" + image.getFileName());
            os = res.getOutputStream();
            bis = new BufferedInputStream(new FileInputStream(imageService.getImageFile(image.getFileName())));
            byte[] buff = new byte[1024*1024*10];
            while (bis.read(buff) != -1) {
                os.write(buff, 0, buff.length);
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
