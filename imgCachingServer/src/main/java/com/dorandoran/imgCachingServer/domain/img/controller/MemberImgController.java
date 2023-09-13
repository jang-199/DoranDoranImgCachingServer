package com.dorandoran.imgCachingServer.domain.img.controller;

import com.dorandoran.imgCachingServer.domain.img.dto.Img2ByteDto;
import com.dorandoran.imgCachingServer.domain.img.service.ImgService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberImgController {
    private final RedisTemplate<String, Img2ByteDto> userPicRedisTemplate;
    private final RedisTemplate<Integer, Img2ByteDto> defaultRedisTemplate;
    private final ImgService imgService;
    private final String DEFAULT_PIC_URL = "http://116.44.231.155:8080/api/pic/default/";
    private final String DEFAULT_MEMBER_PIC_URL = "http://116.44.231.155:8080/api/pic/member/";

    @GetMapping("/pic/member/{picName}")
    ResponseEntity<?> getUserImg(@PathVariable String picName) throws IOException {
        Img2ByteDto img2ByteDto = userPicRedisTemplate.opsForValue().get(picName);

        if (imgService.hasImg2ByteDto(img2ByteDto)) {
            userPicRedisTemplate.expire(picName, 15, TimeUnit.MINUTES);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + Objects.requireNonNull(img2ByteDto).getFileName() + "\"")
                    .body(new ByteArrayResource(img2ByteDto.getPic()));
        }else {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders httpHeaders = new HttpHeaders();
            HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);

            UriComponents url = UriComponentsBuilder.fromHttpUrl(DEFAULT_MEMBER_PIC_URL +picName).build(false);
            ResponseEntity<Resource> fromMainServer;
            try {
                fromMainServer = restTemplate.exchange(url.toUri(), HttpMethod.GET, httpEntity, Resource.class);
            } catch (Exception e) {
                LinkedHashMap<String, String> responseDto = new LinkedHashMap<>();
                responseDto.put("code",HttpStatus.INTERNAL_SERVER_ERROR.toString());
                responseDto.put("message",e.getMessage());
                return ResponseEntity.internalServerError().body(responseDto);
            }

            String filename = fromMainServer.getHeaders().getContentDisposition().getFilename();
            String picNameWithoutExtension = Objects.requireNonNull(filename).split("[.]")[0];
            Img2ByteDto byteDto = Img2ByteDto.builder().fileName(filename).pic(Objects.requireNonNull(fromMainServer.getBody()).getContentAsByteArray()).build();
            userPicRedisTemplate.opsForValue().set(picNameWithoutExtension, byteDto);
            userPicRedisTemplate.expire(picNameWithoutExtension, 15, TimeUnit.MINUTES);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + byteDto.getFileName() + "\"")
                    .body(new ByteArrayResource(byteDto.getPic()));

        }
    }

    @GetMapping("/pic/default/{picName}")
    ResponseEntity<?> getDefaultImg(@PathVariable Integer picName) throws IOException {
        if (!imgService.isWithinBackgroundCnt(picName)) {
            return ResponseEntity.badRequest().body(Map.of("code", HttpStatus.BAD_REQUEST,"message","기본 제공 사진 범위를 초과했습니다."));
        }

        Img2ByteDto img2ByteDto = defaultRedisTemplate.opsForValue().get(picName);

        if (imgService.hasImg2ByteDto(img2ByteDto)) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + Objects.requireNonNull(img2ByteDto).getFileName() + "\"")
                    .body(new ByteArrayResource(img2ByteDto.getPic()));

        }else {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders httpHeaders = new HttpHeaders();
            HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);

            UriComponents url = UriComponentsBuilder.fromHttpUrl(DEFAULT_PIC_URL+picName).build(false);
            ResponseEntity<Resource> fromMainServer;
            try {
                fromMainServer = restTemplate.exchange(url.toUri(), HttpMethod.GET, httpEntity, Resource.class);
            } catch (Exception e) {
                LinkedHashMap<String, String> responseDto = new LinkedHashMap<>();
                responseDto.put("code",HttpStatus.INTERNAL_SERVER_ERROR.toString());
                responseDto.put("message",e.getMessage());
                return ResponseEntity.internalServerError().body(responseDto);
            }

            String filename = fromMainServer.getHeaders().getContentDisposition().getFilename();
            Img2ByteDto byteDto = Img2ByteDto.builder().fileName(filename).pic(Objects.requireNonNull(fromMainServer.getBody()).getContentAsByteArray()).build();
            defaultRedisTemplate.opsForValue().set(Integer.parseInt(Objects.requireNonNull(filename).split("[.]")[0]), byteDto);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + byteDto.getFileName() + "\"")
                    .body(new ByteArrayResource(byteDto.getPic()));

        }
    }
}
