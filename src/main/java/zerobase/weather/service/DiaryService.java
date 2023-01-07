package zerobase.weather.service;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.WeatherApplication;
import zerobase.weather.domain.DateWeather;
import zerobase.weather.domain.Diary;
import zerobase.weather.error.InvalidDate;
import zerobase.weather.repository.DateWeatherRepository;
import zerobase.weather.repository.DiaryRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//스케쥴링/캐싱 = 매 요청마다 api를 요청하지 않고, 서버 캐싱 즉 서버에 저장했다가 서버로 요청받는다.

@Service
@RequiredArgsConstructor
public class DiaryService {

    //실무 개발 시 환경이 매번 다르므로 환경마다 다르게 properties가 변한다.
    //따라서 데이터는 properties에 저장하고, @Value로 꺼낸다.
    @Value("${openweathermap.key}")
    private String apiKey;

    private final DiaryRepository diaryRepository;
    private final DateWeatherRepository dateWeatherRepository;
    //전체를 대상으로 log를 쌓을 것이다.
    private static final Logger logger = LoggerFactory.getLogger(WeatherApplication.class);

    //매 새벽 1시마다 오늘날의 날씨를 저장한다. (캐싱한다)
    //테스트는 5초마다 실행되도록 한다. "0/5 * * * * *"
    @Transactional
    @Scheduled(cron = "0 0 1 * * *")
    public void saveWeatherDate() {
        dateWeatherRepository.save(getWeatherFormApi());
    }

    //날씨 작성하기
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createDiary(LocalDate date, String text) {

        //로그 쌓기
        logger.info("started to create diary");

        //날씨 데이터 가져오기 (API에서 가져와 DB에 저장된 데이터 -> 가져오기)
        DateWeather dateWeather = getDateWeather(date);

        //3.db 넣기
        Diary nowDiary = new Diary();
        nowDiary.setDateWeather(dateWeather);
        nowDiary.setText(text);
        diaryRepository.save(nowDiary);

        //로그 쌓기
        logger.info("end to create diary");
    }

    //API에서 가져와 DB에 저장된 데이터 -> 가져오기
    private DateWeather getDateWeather(LocalDate date) {
        List<DateWeather> dateWeatherListFormDB = dateWeatherRepository.findAllByDate(date);
        if(dateWeatherListFormDB.size() == 0) {
            //새로 api에서 날짜 정보를 가져와야한다.
            //정책상 현재 날씨를 가져오거나 or 날씨 없이 일기를 쓰게 한다. (오늘날의 날씨만 알 수 있으니까)
            return getWeatherFormApi();
        }
        return dateWeatherListFormDB.get(0);
    }


    //API에서 받은 데이터 DB에 저장하기
    private DateWeather getWeatherFormApi() {
        //1.API
        String weatherData = getWeatherString();

        //2.파싱
        Map<String, Object> map = parseWeather(weatherData);

        //3.db 저장하기
        DateWeather dateWeather = new DateWeather();
        dateWeather.setDate(LocalDate.now());
        dateWeather.setWeather(map.get("main").toString());
        dateWeather.setIcon(map.get("icon").toString());
        dateWeather.setTemperature((Double)map.get("temp"));
        return dateWeather;
    }

    //API에서 데이터 받기
    private String getWeatherString() {
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + apiKey;
        //System.out.println(apiUrl); apiUrl을 타고 들어가면 json형태로 받아온 데이터를 확인할 수 있다.

        try {
            URL url = new URL(apiUrl); //String을 URL 형식으로 변경한다.

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();

            BufferedReader br;
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }

            br.close();
            return response.toString();

        } catch (Exception e) {
            return "failed to get response";
        }
    }

    //API에서 받은 데이터 파싱하기
    private Map<String, Object> parseWeather(String jsonString) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;

        //weather > main,icon > main 안에 temp, feel_like 등등 있다.
        try {
            jsonObject = (JSONObject) jsonParser.parse(jsonString);
        } catch (ParseException e) { //파싱할 때 발생하는 예외
            throw new RuntimeException(e);
        }
        Map<String, Object> resultMap = new HashMap<>();

        //temp 찾기
        JSONObject mainData = (JSONObject) jsonObject.get("main");
        resultMap.put("temp", mainData.get("temp"));

        //main, icon 찾기
        //단 weather은 array 형식이므로 JSONARRAY를 사용한다. (데이터 한 묶음 밖에 없으면서 왜 어레이를)
        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
        JSONObject weatherData = (JSONObject) weatherArray.get(0);
        resultMap.put("main", weatherData.get("main"));
        resultMap.put("icon", weatherData.get("icon"));
        return resultMap;
    }

    @Transactional(readOnly = true)
    public List<Diary> readDiary(LocalDate date) {
        /*
        if(date.isAfter(LocalDate.ofYearDay(3050,1))) {
            throw new InvalidDate();
        }
        */
        //ExceptionHandler를 지정하면 굳이 위의 상황처럼 일일이 예외를 보고하지 않아도 알아서 Handler가 받아서
        //예외를 처리한다.

        logger.debug("read diary"); //로그 쌓기
        return diaryRepository.findAllByDate(date);
    }

    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findAllByDateBetween(startDate, endDate);
    }

    public void updateDiary(LocalDate date, String text) {
        Diary nowDiary = diaryRepository.getFirstByDate(date);
        nowDiary.setText(text);
        diaryRepository.save(nowDiary);
    }

    public void deleteDiary(LocalDate date) {
        diaryRepository.deleteAllByDate(date);
    }
}
