package com.dorandoran.imgCachingServer.domain.img.dto;

import com.dorandoran.imgCachingServer.domain.img.dto.type.ImgType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Getter
@Setter
public class Img2CacheServerDto {
    ImgType imgType;
    String FileName; //확장자까지
    MultipartFile pic;


    public Img2CacheServerDto() {
    }
    @Builder
    public Img2CacheServerDto(ImgType imgType, String fileName, MultipartFile pic) {
        this.imgType = imgType;
        FileName = fileName;
        this.pic = pic;
    }
    public Img2ByteDto makeImg2ByteDto() throws IOException {
        return Img2ByteDto.builder().fileName(this.FileName).pic(this.pic.getBytes()).build();
    }
}
