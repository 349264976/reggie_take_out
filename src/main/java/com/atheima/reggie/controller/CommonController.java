package com.atheima.reggie.controller;
import com.atheima.reggie.common.R;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String BasePath;
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        log.info("Uploading"+file);

        //原始文件名字
        String originalFilename = file.getOriginalFilename();
        //使用UUID重新生成文件名防止文件名称重复
        String Uuidstring = UUID.randomUUID().toString();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newfilename=Uuidstring+suffix;
        /**
         * 判断存储图片的目录在不在不在就创建
         */
        File dir = new File(BasePath);
        if (!dir.exists()){
            dir.mkdirs();
        }
        try {
            file.transferTo(new File(BasePath+newfilename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(newfilename);
    }
    /**
     * 文件下载
     * @param name
     * @param httpServletResponse
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse httpServletResponse){
        FileInputStream fileInputStream=null;
        ServletOutputStream outputStream=null;
        //输入流通过输入流读取文件内容
        try {
            fileInputStream=new FileInputStream(BasePath+name);
            outputStream = httpServletResponse.getOutputStream();
            httpServletResponse.setContentType("image/jpeg");
            int lens=0;
            byte[] bytes = new byte[1024];
            while ((lens=fileInputStream.read(bytes))!=-1) {
                outputStream.write(bytes,0,lens);
                outputStream.flush();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            try {
                outputStream.close();
                fileInputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        //输出流 通过输出流将文件协会浏览器
    }
}
