package com.jweb.busi.service.compute;

import com.jweb.busi.entity.compute.Image;
import com.jweb.busi.service.sync.SyncBeanService;
import com.jweb.common.exception.BusiException;
import com.jweb.common.persistent.model.Expression;
import com.jweb.common.persistent.model.Where;
import com.jweb.common.service.BeanService;
import com.jweb.common.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationHome;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImageService {
    @Autowired
    SyncBeanService syncBeanService;
    @Autowired
    BeanService beanService;

    public Image create(Image image) throws BusiException {
        try{
            image.setMetadataJson(JsonUtil.beanToJson(image.getMetadata()));
            image = syncBeanService.create(image);
        }catch (Exception e){
            throw new BusiException(e.getMessage());
        }
        return image;
    }
    public Image update(Image image) throws BusiException {
        try{
            image.setMetadataJson(JsonUtil.beanToJson(image.getMetadata()));
            image = syncBeanService.update(image);
        }catch (Exception e){
            throw new BusiException(e.getMessage());
        }
        return image;
    }
    public void remove(String imageId) throws BusiException{
        try{
            syncBeanService.remove(Image.class,imageId);
        }catch (Exception e){
            throw new BusiException(e.getMessage());
        }
    }
    public void uploadImageFile(MultipartFile imageFile) throws BusiException{
        try{
            if(null == imageFile) throw new BusiException("文件不能为空");
            ApplicationHome home = new ApplicationHome(this.getClass());
            File jarDir = home.getSource().getParentFile();
            File imageDir = new File(jarDir.getAbsolutePath()+File.separator+"images");
            if(!imageDir.exists())
                imageDir.mkdirs();

            File targetFile = new File(imageDir+File.separator+imageFile.getOriginalFilename());
            if(targetFile.exists())
                throw new Exception("文件已经存在");
            imageFile.transferTo(targetFile);
        }catch (Exception e){
            throw new BusiException(e.getMessage());
        }
    }
    public void removeImageFile(String fileName) throws BusiException{
        try{
            //先判断有没有镜像依赖该文件，若有提示先删除镜像才能删除该文件
            List<Image> images = beanService.list(Image.class,
                    Where.create("fileName",Expression.eq,fileName));
            if(images!=null && images.size()>0){
                String msg = "";
                for(Image image : images) {
                    if(msg.length()>0) msg+=",";
                    msg += image.getName();
                }
                throw new BusiException("镜像"+msg+"依赖该文件，请先删除镜像");
            }
            File imageFile = this.getImageFile(fileName);
            if(imageFile.exists())
                imageFile.delete();
        }catch (Exception e){
            throw new BusiException("删除文件["+fileName+"]失败,"+e.getMessage());
        }
    }
    public List<String> listImageFile(){
        List<String> imageFiles = new ArrayList<>();
        try{
            ApplicationHome home = new ApplicationHome(this.getClass());
            File jarDir = home.getSource().getParentFile();
            File imageDir = new File(jarDir.getAbsolutePath()+File.separator+"images");
            File[] files = imageDir.listFiles();
            if(null!=files) {
                for (File file : files) {
                    if (file.isFile()) {
                        imageFiles.add(file.getName());
                    }
                }
            }
        }catch (Exception e){e.printStackTrace();}
        return imageFiles;
    }

    public  File getImageFile(String fileName){
        ApplicationHome home = new ApplicationHome(this.getClass());
        File jarDir = home.getSource().getParentFile();
        File imageFile = new File(jarDir.getAbsolutePath()+File.separator+"images"+
        File.separator+fileName);
        return imageFile;
    }
}
