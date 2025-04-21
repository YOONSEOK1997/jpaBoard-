package com.example.jpaboard.repository;

import java.awt.print.Pageable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;


import com.example.jpaboard.entity.Article;


public interface ArticleRepository extends JpaRepository<Article, Long>{
	//CrudRepository : insert , select one , select all , update ,delete 
	
	//JpaRepository(CrudRepository 자식 s ) : select limit , select order by , .. ... 
	
	//findAll() : 원하는 컬럼만 가지고 오도록
	
	Page<Article> findByTitleContaining(PageRequest pageable, String word);

	

	
}