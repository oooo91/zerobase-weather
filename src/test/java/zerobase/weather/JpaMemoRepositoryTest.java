package zerobase.weather;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional //TEST에서 쓸 경우에는 다 롤백함... (실제 디비에 저장하면 안되니까)
public class JpaMemoRepositoryTest {

    /*
    @Autowired
    JpaMemoRepository jpaMemoRepository;

    @Test
    void insertMemoTest() {
        //given
        Memo newMemo = new Memo(10, "this is jpa memo");

        //when
        jpaMemoRepository.save(newMemo);

        //then
        List<Memo> memoList = jpaMemoRepository.findAll();
        assertTrue(memoList.size() > 0);
    }

    @Test
    void findByIdTest() {
        //given
        //여기서 중요한 점은, generatedValue.identity 와 같이 id는 데이터베이스에서 만들어진 것을 사용하므로,
        //지금 11과 같이 스프링 부트에서 만든 id는 의미가 없다.
        //즉 save를 할지라도, 막상 Memo memo = jpaMemoRepository.save(newMemo); 받으면 id는 5로 출력된다.
        //따라서 그냥 findById처럼 id값으로 찾으려는 경우 테스트가 통과되지 않으며 save한 반환값을 받아서 반환값의 id로 then 결과를 확인해야한다.
        //그러니까 테스트 시 id에 null 넣어도 된다.
        Memo newMemo = new Memo(11, "jpa");

        //when
        Memo memo = jpaMemoRepository.save(newMemo);

        //then
        Optional<Memo> result = jpaMemoRepository.findById(memo.getId());
        assertEquals(result.get().getText(), "jpa");
    }
     */
}
