package com.example.jpaboard.dto;


import com.example.jpaboard.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberForm {
	private Integer memberNo;
	private String memberId;
	private String memberRole;
	private String memberPw;

	public Member toEntity() {
		Member entity = new Member();

		
		entity.setMemberId(memberId);
		entity.setMemberPw(memberPw);
		entity.setMemberRole(memberRole);
		return entity;
	}
}
