package com.dorandoran.imgCachingServer.domain.img.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;


@Getter
public class Img2ByteDto {
    String fileName;
    byte[] pic;

    public Img2ByteDto() {
    }

    @Builder
    public Img2ByteDto(String fileName, byte[] pic) {
        this.fileName = fileName;
        this.pic = pic;
    }

}
