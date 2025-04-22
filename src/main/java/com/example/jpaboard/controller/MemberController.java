package com.example.jpaboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.jpaboard.dto.MemberForm;
import com.example.jpaboard.entity.Member;
import com.example.jpaboard.repository.MemberRepository;
import com.example.jpaboard.util.SHA256Util;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class MemberController {
	@Autowired
	MemberRepository memberRepository;

	// 회원목록
	@GetMapping("/member/memberList")
	public String memberList(HttpSession session, Model model,
			@RequestParam(value = "word", defaultValue ="") String word,
			@RequestParam(value = "currentPage", defaultValue = "0") int currentPage,
			@RequestParam(value = "rowPerPage", defaultValue = "10") int rowPerPage ) {
		// session 인증/인가 검사
		if(session.getAttribute("loginMember") == null) {
			return "redirect:/member/login";
		}

		// 사용자 목록 + 페이징 + id 검색
		PageRequest pageable = PageRequest.of(currentPage, rowPerPage);

		Page<Member> memberList = memberRepository.findByMemberIdContaining(word, pageable);
		model.addAttribute("list", memberList);
		model.addAttribute("prePage", (memberList.getNumber() > 0) ? memberList.getNumber() - 1 : 0);
		model.addAttribute("nextPage", (memberList.getNumber() < memberList.getTotalPages() - 1) ? memberList.getNumber() + 1 : memberList.getNumber());  
		return "member/memberList";
	}
	// 로그인 폼
	@GetMapping("/member/login")
	public String login() {
		return "member/login";
	}
	// 로그인 액션
	@PostMapping("/member/login")
	public String login(HttpSession session, MemberForm memberForm, RedirectAttributes rda) {
		// pw 암호화
		memberForm.setMemberPw(SHA256Util.encoding(memberForm.getMemberPw()));
		// 로그인 확인 메서드
		Member loginMember 
		= memberRepository.findByMemberIdAndMemberPw(memberForm.getMemberId(), memberForm.getMemberPw());
		if(loginMember == null) {
			log.debug("로그인 실패");
			rda.addFlashAttribute("msg", "로그인 실패");
			return "redirect:/member/login";
		}
		// 로그인 성공 코드 구현
		log.debug("로그인 성공");
		session.setAttribute("loginMember", loginMember); // ISSUE : pw정보까지 세션에 저장
		return "redirect:/";
	}

	// 로그아웃
	@GetMapping("/member/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/member/login";
	}

	// 회원가입 + member_id 중복확인
	@GetMapping("/member/joinMember")
	public String joinMember() {
		return "member/joinMember";
	}
	@PostMapping("/member/joinMember")
	public String joinMember(MemberForm memberForm, RedirectAttributes rda) {
		log.debug(memberForm.toString());
		log.debug("isMebmerId : "+memberRepository.existsByMemberId(memberForm.getMemberId()));

		if(memberRepository.existsByMemberId(memberForm.getMemberId())) {
			rda.addFlashAttribute("msg", memberForm.getMemberId()+" ID가 이미 존재합니다.");
			return "redirect:/member/joinMember"; 
		}

		// false이면 회원가입 진행
		// memberForm.getMemberPw()값을 SHA-256방식으로 암호화
		memberForm.setMemberPw(SHA256Util.encoding(memberForm.getMemberPw()));

		Member member = memberForm.toEntity();
		memberRepository.save(member); // entity저장 -> 최종 커밋시 -> 테이블에 행이 추가(insert)

		return "redirect:/member/login";
	}


	// 회원정보수정
	@GetMapping("/member/modifyMemberPw")
	public String modifyMemberPw() {
		return "member/modifyMemberPw";
	}
	@PostMapping("/member/modifyMemberPw")
	public String modifyMemberPw(HttpSession session,
	                             @RequestParam String currentPw,
	                             @RequestParam String newPw,
	                             @RequestParam String confirmPw,
	                             RedirectAttributes rda) {

	    Member loginMember = (Member) session.getAttribute("loginMember");

	    // 로그인 안 되어 있으면 로그인 페이지로
	    if (loginMember == null) {
	        return "redirect:/member/login";
	    }

	    // 현재 비밀번호 확인
	    String encodedCurrentPw = SHA256Util.encoding(currentPw);
	    if (!loginMember.getMemberPw().equals(encodedCurrentPw)) {
	        rda.addFlashAttribute("msg", "현재 비밀번호가 일치하지 않습니다.");
	        return "redirect:/member/modifyMemberPw";
	    }

	    // 새 비밀번호 확인
	    if (!newPw.equals(confirmPw)) {
	        rda.addFlashAttribute("msg", "새 비밀번호가 서로 일치하지 않습니다.");
	        return "redirect:/member/modifyMemberPw";
	    }

	    // 비밀번호 변경 및 저장
	    loginMember.setMemberPw(SHA256Util.encoding(newPw));
	    memberRepository.save(loginMember);

	    // 로그아웃 시키고 다시 로그인 유도
	    session.invalidate();
	    rda.addFlashAttribute("msg", "비밀번호가 변경되었습니다. 다시 로그인해주세요.");
	    return "redirect:/member/login";
	}

	// 회원탈퇴
	 @GetMapping("/member/delete")
	    public String deleteConfirmPage() {
	        return "member/delete"; 
	    }

	    @PostMapping("/member/delete")
	    public String deleteMember(HttpSession session, RedirectAttributes rttr) {
	        Member loginMember = (Member) session.getAttribute("loginMember");

	        if (loginMember != null) {
	            memberRepository.delete(loginMember);
	            session.invalidate();
	            rttr.addFlashAttribute("msg", "정상적으로 탈퇴되었습니다.");
	        }

	        return "redirect:/";
	    }

}
