package com.bizmate.project.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
public class PageResponseDTO<E> {
    //<E> << 타입 파라미터를 // 타입 변수

    private List<E> dtoList;
    // 제네릭 클래스로 받은 <E> <<을 반환해주기 위한 필드
    private List<Integer> pageNumList;
    // 페이징 처리를 위한 pageNumList;
    private PageRequestDTO pageRequestDTO;
    private boolean prev;
    // 10개씩 페이징처리해서 렌더링 하는데 이전으로 넘어가기 ( 9페이지
    private boolean next;
    // 10개씩 페이징처리해서 렌더링 하는데 다음으로 넘어가기 ( 11페이지
    private int totalCount;
    private int prevPage;
    private int nextPage;
    private int totalPage;
    private int current;

    @Builder(builderMethodName = "withAll")
    // 빌더 메서드 이름을 builder() 대신 withAll()로 변경
    // ex) Member m = Member.withAll() <<로 builder 대신 사용
    public PageResponseDTO(List<E> dtoList, PageRequestDTO pageRequestDTO, long totalCount) {

        this.dtoList = dtoList;

        this.pageRequestDTO = pageRequestDTO;
        // page, size 필드 생성한 pageRequestDTO
        this.totalCount = (int) totalCount;

        int end = (int) (Math.ceil(pageRequestDTO.getPage() / 10.0)) * 10;
        // Math.ceil(...) = 소수점 올림 처리
        // Math 는 double로 반환하기에 int로 형변환
        // 현재 페이지가 속한 10단위 그룹의 마지막 페이지 번호를 구하는 방식
        int start = end - 9;
        // 10페이지씩 잘라서 페이징처리 하니 end -9로
        // ex) 30 -9 = 21 식으로 페이징처리 중 첫번째 페이지 구하기
        int last = (int) Math.ceil(totalCount / (double) pageRequestDTO.getSize());
        // 총 페이지 구하는 공식

        end = end > last ? last : end;
        // end가 실제 마지막 페이지를 넘어가지 않도록 조정

        this.prev = start > 1;
        // ture면 "이전"버튼 활성화
        // 즉 시작번호가 1보타 크면 이전 그룹이 존재한다는 뜻
        this.next = totalCount > end * pageRequestDTO.getSize();
        // true면 "다음"버튼 활성화
        // 현재 그룹의 끝(end)페이지까지 보여주되 데이터 남아있으면 "다음"이 있다
        this.pageNumList = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
        //start ~ end까지 번호를 리스트로 만들어 UI에 뿌림
        //예: start=21, end=30 → [21,22,23,24,25,26,27,28,29,30]

        if (prev) {
            this.prevPage = start - 1;
        }
        if (next) {
            this.nextPage = end + 1;
        }
        this.totalPage = this.pageNumList.size();
        this.current = pageRequestDTO.getPage();

    }


}
