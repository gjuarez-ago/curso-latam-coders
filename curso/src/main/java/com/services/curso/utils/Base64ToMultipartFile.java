package com.services.curso.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Base64.Decoder;

import org.springframework.web.multipart.MultipartFile;

public class Base64ToMultipartFile  implements MultipartFile  {


    private final byte[] imgContent;
    private final String header;

    private Base64ToMultipartFile(byte[] imgContent, String header) {
        this.imgContent = imgContent;
        this.header = header.split(";")[0];
    }

    @Override
    public String getName() {
        return System.currentTimeMillis() + Math.random() + "." + header.split("/")[1];
    }

    @Override
    public String getOriginalFilename() {
        return System.currentTimeMillis() + (int) (Math.random() * 10000) + "." + header.split("/")[1];
    }

    @Override
    public String getContentType() {
        return header.split(":")[1];
    }

    @Override
    public boolean isEmpty() {
        return imgContent == null || imgContent.length == 0;
    }

    @Override
    public long getSize() {
        return imgContent.length;
    }

    @Override
    public byte[] getBytes() {
        return imgContent;
    }

    @Override

    public InputStream getInputStream() {
        return new ByteArrayInputStream(imgContent);
    }

    @SuppressWarnings("resource")
	@Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        new FileOutputStream(dest).write(imgContent);
    }


    public static MultipartFile base64Convert(String base64) {

        String[] baseStrs = base64.split(",");
        Decoder decoder = Base64.getDecoder();
        byte[] b = decoder.decode(baseStrs[1]);

        for (int i = 0; i < b.length; ++i) {
            if (b[i] < 0) {
                b[i] += 256;
            }
        }
        return new Base64ToMultipartFile(b, baseStrs[0]);
    }
    
    
}