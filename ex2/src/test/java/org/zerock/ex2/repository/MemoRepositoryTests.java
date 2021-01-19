package org.zerock.ex2.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.zerock.ex2.entity.Memo;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
public class MemoRepositoryTests {

    @Autowired
    Memorepository memoRepository;

//    @Test
    public void testClass(){

        System.out.println(memoRepository.getClass().getName());
    }

//    @Test
    public void testInsertDummies() {

        IntStream.rangeClosed(1,100).forEach(i -> {
            Memo memo = Memo.builder().memoText("Sample..."+i).build();
            memoRepository.save(memo);
        });
    }

//    @Test
    public void testSelect() {

        // 데이터베이스에 존재하는 mno
        Long mno = 100L;

        Optional<Memo> result = memoRepository.findById(mno);

        System.out.println("===================== findById ========================");

        if(result.isPresent()) {
            Memo memo = result.get();
            System.out.println(memo);
        }
    }

    @Transactional
//    @Test
    public void testSelect2() {

        // 데이터베이스에 존재하는 mno
        Long mno = 100L;

        Memo memo = memoRepository.getOne(mno);

        System.out.println("====================== getOne =======================");

        System.out.println(memo);
    }

//    @Test
    public void testUpdate() {

        Memo memo = Memo.builder().mno(100L).memoText("Update Text").build();

        System.out.println(memoRepository.save(memo));

    }

//    @Test
    public void testDelete() {
        Long mno = 100L;
        memoRepository.deleteById(mno);
    }

//    @Test
    public void testPageDefault() {
        // 1페이지 10개
        Pageable pageable = PageRequest.of(0,10);

        Page<Memo> result = memoRepository.findAll(pageable);

        System.out.println(result);

        System.out.println("=============================================");

        System.out.println("Total Pages: "+result.getTotalPages()); // 총 몇 페이지

        System.out.println("Total Count: "+result.getTotalElements()); // 전체 갯수

        System.out.println("Page Number: "+result.getNumber()); // 현재 페이지 번호 (0부터 시작)

        System.out.println("Page Size: "+result.getSize()); // 페이지당 데이터 개수

        System.out.println("has next page?: "+result.hasNext()); // 다음 페이지 존재 여부

        System.out.println("first page?: "+result.isFirst()); // 시작 페이지(0) 여부

        System.out.println("---------------------------------------------");

        for(Memo memo : result.getContent()) {
            System.out.println(memo);
        }
    }

//    @Test
    public void testSort() {

        Sort sort1 = Sort.by("mno").descending();
//        Sort sort1 = Sort.by("mno1").descending(); // 없는 column을 쓸 경우
//           에러 발생: org.springframework.data.mapping.PropertyReferenceException: No property mno1 found for type Memo! Did you mean 'mno'?
        Sort sort2 = Sort.by("memoText").ascending();
        Sort sortAll = sort1.and(sort2); // and를 이용한 연결

        Pageable pageable = PageRequest.of(0, 10, sortAll);

        Page<Memo> result = memoRepository.findAll(pageable);

        result.get().forEach(memo -> {
            System.out.println(memo);
        });
    }

//    @Test
    public void testQueryMethods() {

        List<Memo> list = memoRepository.findByMnoBetweenOrderByMnoDesc(70L, 80L);

        for (Memo memo : list) {
            System.out.println(memo);
        }
    }

//    @Test
    public void testQueryMethodsWithPagable() {

        Pageable pageable = PageRequest.of(0, 10, Sort.by("mno").descending());

        Page<Memo> result = memoRepository.findByMnoBetween(70L, 80L, pageable);

        result.get().forEach(memo -> System.out.println(memo));
    }

    @Commit
    @Transactional
//    @Test
    public void testDeleteQueyMethods() {
        memoRepository.deleteMemoByMnoLessThan(10L);
    }

    @Transactional
    @Commit
//    @Test
    public void testQueryAnnotation() {
        // getListDesc Test
        List<Memo> list = memoRepository.getListDesc();
        for (Memo memo : list) {
            System.out.println(memo);
        }

        // updateMemoText(Long mno, String memoText) Test
        int result = memoRepository.updateMemoText(10L, "new UpdateText");
        System.out.println(result);

        // updateMemoText(Memo memo) Test
        Memo memo = new Memo(11L, "new UpdateText2")
        int result1 = memoRepository.updateMemoText(memo);
        System.out.println(result1);

        // getListWithQuery Test: paing 처리하는 방법
        int page = 0, size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by("mno").descending());
        Page<Memo> result2 = memoRepository.getListWithQuery(70L, pageable);
        result2.get().forEach(memo2 -> System.out.println(memo2));
        while(result2.hasNext()) {
            System.out.println("result has Next!!!");
            page++;
            pageable = PageRequest.of(page, size, Sort.by("mno").descending());
            result2 = memoRepository.getListWithQuery(70L, pageable);
            result2.get().forEach(memo2 -> System.out.println(memo2));
        }

        // getListWithQueryObject Test
        Pageable pageable2 = PageRequest.of(0, 10, Sort.by("mno").descending());
        Page<Object[]> result3 = memoRepository.getListWithQueryObject(70L, pageable2);
        result3.get().forEach(object -> System.out.println(object[0] + ", " + object[1] + ", " + object[2]));

        // getNativeResult Test
        List<Object[]> result4 = memoRepository.getNativeResult();
        result4.forEach(object -> System.out.println(object[0] + ", " + object[1]));
    }
}
