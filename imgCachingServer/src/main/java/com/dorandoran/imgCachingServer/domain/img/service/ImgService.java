package com.dorandoran.imgCachingServer.domain.img.service;

import com.dorandoran.imgCachingServer.domain.img.dto.Img2ByteDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ImgService {
    @Value("${background.cnt}")
    Integer backgroundPicCnt;
    public boolean isWithinBackgroundCnt(int cnt) {
        return backgroundPicCnt >= cnt && cnt > 0;
    }

    public boolean hasImg2ByteDto(Img2ByteDto img2ByteDto) {
        return img2ByteDto != null;
    }
}
