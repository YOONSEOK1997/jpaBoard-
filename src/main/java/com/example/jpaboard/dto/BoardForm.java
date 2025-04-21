package com.example.jpaboard.dto;


import com.example.jpaboard.entity.Board;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardForm {
	private Integer boardNo;
	private String boardTitle;
	private String boardContent;
	
	public Board toEntity() {
		Board entity = new Board();
		
		entity.setBoardNo(boardNo);
		entity.setBoardTitle(boardTitle);
		entity.setBoardContent(boardContent);
	
		return entity;
		
	}

	
}
