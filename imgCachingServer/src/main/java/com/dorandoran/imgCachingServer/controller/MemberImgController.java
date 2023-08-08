package com.dorandoran.imgCachingServer.controller;

import com.dorandoran.imgCachingServer.dto.Img2ByteDto;
import com.dorandoran.imgCachingServer.dto.Img2CacheServerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberImgController {
    private final RedisTemplate<String, Img2ByteDto> stringredisTemplate;
    private final RedisTemplate<Integer, Img2ByteDto> integerredisTemplate;

    @PostMapping("/pic")
    ResponseEntity<?> saveMemberImg(Img2CacheServerDto dto) {
        Img2ByteDto img2ByteDto;
        try {
            img2ByteDto = dto.makeImg2ByteDto();
//            log.info(img2ByteDto.getFileName());
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        switch (dto.getImgType()) {
            case UserUpload -> stringredisTemplate.opsForValue().set(dto.getFileName().split("[.]")[0],img2ByteDto);
            case DefaultBackground -> integerredisTemplate.opsForValue().set(Integer.parseInt(dto.getFileName().split("[.]")[0]),img2ByteDto);
        }
        return ResponseEntity.ok().build();
    }



    @GetMapping("/pic/member/{picName}")
    ResponseEntity<?> getMemberImg(@PathVariable String picName) {
        Img2ByteDto img2ByteDto = stringredisTemplate.opsForValue().get(picName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + img2ByteDto.getFileName() + "\"")
                .body(new ByteArrayResource(img2ByteDto.getPic()));
    }

    @GetMapping("/pic/default/{picName}")
    ResponseEntity<?> getDefaultImg(@PathVariable Integer picName) {
        Img2ByteDto img2ByteDto = integerredisTemplate.opsForValue().get(picName);
        log.info(integerredisTemplate.opsForValue().get(picName).getFileName());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + img2ByteDto.getFileName() + "\"")
                .body(new ByteArrayResource(img2ByteDto.getPic()));
    }
}
