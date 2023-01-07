package zerobase.weather.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import zerobase.weather.domain.Diary;
import zerobase.weather.service.DiaryService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    //날씨 api 부르기
    //오늘 날짜는 이러한데, 오늘은 어땠다.
    //requestParam은 url에서 데이터를 받는다. requestBody는 body로 데이터를 받는다.
    //또한 post 요청을 브라우저로 검사할 시 웹은 빠르게 데이터를 보여주기 위해 캐싱하므로 결과값이 이상하게 나올 수 있다.
    //그리고 실제 운영망의 요청을 보고 싶을 대
    //(로컬)웹을 통한 검사보다 포스트맵 같은 테스트 api 플랫폼을 사용하는 것이 좋다.

    //swagger
    @ApiOperation(value = "Swagger 문서에서 해당 api에 대한 요약을 부여하는 어노테이션", notes = "얜 설명")
    @PostMapping("/create/diary")
    void createDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                     @RequestBody String text) {
        diaryService.createDiary(date, text);
    }

    @ApiOperation("value를 지정하지 않아도 default가 value다")
    @GetMapping("/read/diary")
    List<Diary> readDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return diaryService.readDiary(date);
    }

    //~부터 ~까지 일기를 달라
    @GetMapping("/read/diaries")
    List<Diary> readDiaried(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                            //swagger
                            @ApiParam(value="날짜 형식 : yyyy-MM-dd", example = "2020-02-02") LocalDate startDate,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return diaryService.readDiaries(startDate, endDate);
    }

    //수정하기
    @PutMapping("/update/diary")
    void upateDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                    @RequestBody String text) {
        diaryService.updateDiary(date, text);
    }

    @DeleteMapping("/delete/diary")
    void deleteDiary(@RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) LocalDate date) {
        diaryService.deleteDiary(date);
    }
}
