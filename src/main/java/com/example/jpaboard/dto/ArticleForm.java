package com.example.jpaboard.dto;

import com.example.jpaboard.entity.Article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleForm {
	private Long id;
	private String title;
	private String content;
	
	public Article toEntity() {
		Article entity = new Article();
		
		entity.setId(id);
		entity.setContent(content);
		entity.setTitle(title);
	
		return entity;
		
	}

	
}
