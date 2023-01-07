package zerobase.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.domain.Diary;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Integer> {
    List<Diary> findAllByDate(LocalDate date);

    //between
    List<Diary> findAllByDateBetween(LocalDate startDate, LocalDate endDate);

    //첫번째 일기 고치기
    Diary getFirstByDate(LocalDate date);

    @Transactional
    void deleteAllByDate(LocalDate date);
}
