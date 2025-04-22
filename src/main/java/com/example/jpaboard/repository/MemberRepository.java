package com.example.jpaboard.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.jpaboard.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Integer>{

	//member_id 중복검사
	boolean existsByMemberId(String memberId);
		//로그인 하는 추상메서드 
	Member findByMemberIdAndMemberPw(String mermberId, String memberPw);
	Page<Member> findByMemberIdContaining(String word, PageRequest pageable);



}
