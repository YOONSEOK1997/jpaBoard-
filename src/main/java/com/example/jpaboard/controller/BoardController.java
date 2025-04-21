package com.example.jpaboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.jpaboard.dto.BoardForm;
import com.example.jpaboard.entity.Board;
import com.example.jpaboard.repository.BoardRepository;
@Controller
public class BoardController {

    @Autowired
    private BoardRepository boardRepository;

    // 게시글 추가 폼
    @GetMapping("/board/addBoard")
    public String addBoardForm() {
        return "board/addBoard";
    }

    // 게시글 생성
    @PostMapping("/board/create")
    public String createBoard(BoardForm form) {
        Board entity = form.toEntity();
        boardRepository.save(entity);

        return "redirect:/board/boardList";
    }

    // 수정 폼 띄우기
    @GetMapping("/board/modifyBoard")
    public String modify(Model model, @RequestParam Integer boardNo) {
        Board board = boardRepository.findById(boardNo).orElse(null);
        if (board == null) {
            return "redirect:/board/boardList"; // 게시글 없으면 목록으로
        }
        model.addAttribute("board", board);
        return "board/modifyBoard";  // modify.mustache 또는 modify.jsp 호출
    }

    // 수정 처리 (DB 업데이트)
    @PostMapping("/board/update")
    public String update(BoardForm boardForm) {
        // 폼 데이터로부터 Entity로 변환
        Board board = boardForm.toEntity();

        // 기존 데이터 찾기
        Board target = boardRepository.findById(board.getBoardNo()).orElse(null);
        if (target == null) {
            return "redirect:/board/boardList"; // 수정할 게시글이 없으면 목록으로
        }

        // 값 덮어쓰기
        target.setBoardTitle(board.getBoardTitle());
        target.setBoardContent(board.getBoardContent());

        // DB에 저장 (수정)
        boardRepository.save(target);

        return "redirect:/board/boardOne?boardNo=" + target.getBoardNo();
    }

    // 게시글 보기
    @GetMapping("/board/boardOne")
    public String boardOne(Model model, @RequestParam Integer boardNo) {
        Board board = boardRepository.findById(boardNo).orElse(null);
        model.addAttribute("board", board);
        return "board/boardOne";
    }

    // 게시글 삭제
    @GetMapping("/board/delete")
    public String delete(@RequestParam Integer boardNo, RedirectAttributes rda) {
        Board board = boardRepository.findById(boardNo).orElse(null);
        if (board != null) {
            boardRepository.delete(board);  // 게시글 삭제
            rda.addFlashAttribute("msg", "삭제 성공");
        } else {
            rda.addFlashAttribute("msg", "삭제 실패, 해당 글이 없습니다.");
        }
        return "redirect:/board/boardList";  // 삭제 후 목록 페이지로 리다이렉트
    }

    // 게시글 목록
    @GetMapping("/board/boardList")
    public String boardList(Model model,
            @RequestParam(value = "word", defaultValue = "") String word,
            @RequestParam(value = "currentPage", defaultValue = "0") int currentPage,
            @RequestParam(value = "rowPerPage", defaultValue = "10") int rowPerPage) {

        // 정렬 설정
        Sort sort = Sort.by("boardNo").descending().and(Sort.by("boardTitle").ascending());
        PageRequest pageable = PageRequest.of(currentPage, rowPerPage, sort);

        // 게시글 리스트 조회
        Page<Board> list = boardRepository.findByBoardTitleContaining(pageable, word);

        // 모델에 데이터 추가
		/* model.addAttribute("word", word); */ //검색어를 뷰에 띄워줄 경우에 사용 
        model.addAttribute("list", list);
        model.addAttribute("prePage", (list.getNumber() > 0) ? list.getNumber() - 1 : 0);
        model.addAttribute("nextPage", (list.getNumber() < list.getTotalPages() - 1) ? list.getNumber() + 1 : list.getNumber());  // 페이지 범위 체크 추가

        return "board/boardList";
    }
}
