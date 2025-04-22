package com.example.jpaboard.controller;

import java.util.Map;

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

import com.example.jpaboard.dto.ArticleForm;
import com.example.jpaboard.entity.Article;
import com.example.jpaboard.repository.ArticleRepository;


import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ArticleController {

	@Autowired
	private ArticleRepository articleRepository;
	
	@GetMapping("/articles/sqlTest")
	public String sqlTest(Model model) {
		Map<String, Object>map = articleRepository.getMinMaxCount("a%");
		/* Log.debug(map.toString()); */
		model.addAttribute("map" , map);
		return "articles/sqlTest";
	}
	@GetMapping("/articles/new")
	public String newArticleForm() {
		return "articles/new";
	}
	
	@PostMapping("/articles/create")
	public String createArticle(ArticleForm form) {
		System.out.println(form.toString());
		// DTO 를 Entity로 변경
		/*
		 * Article entity = new Article(); entity.setTitle(form.getTitle());
		 * entity.setContent(form.getContent());
		 */
		// 여기서 articleRepository가 DB에 저장하는 역할 수행!
		Article entity = form.toEntity();
		articleRepository.save(entity);

		/*
		 * save  (entity) INSERT (또는 UPDATE) findById(id) ID로 1건 조회 findAll() 전체
		 * 조회 Update (수정) save(entity) ID가 존재하면 UPDATE Delete (삭제) 해당 엔티티 삭제 〃
		 * deleteById(id) ID로 삭제
		 */
		return "redirect:/articles/index";
	}
	@GetMapping("/articles/edit")
	public String edit(Model model, @RequestParam Long id) {
		Article article = articleRepository.findById(id).orElse(null); //findById 는 Optional 타입 
		if (article == null) {
	        return "redirect:/articles/index"; // Article이 없으면 다시 목록으로 리다이렉트
	    }
		model.addAttribute("article", article);
		return "articles/edit";
		
	}
	@GetMapping("/articles/update")
	public String update(ArticleForm articleForm) {
		Article article = articleForm.toEntity();
		return "redirect:/articles/show?id="+ article.getId();
		
	}
	

	
	
	@GetMapping("/articles/show")
	public String show(Model model, @RequestParam Long id) {
		Article article = articleRepository.findById(id).orElse(null); //findById 는 Optional 타입 
		model.addAttribute("article", article);
		return "articles/show";
		
	}
	@GetMapping("/articles/delete")
	public String delete(@RequestParam Long id, RedirectAttributes rda) {
	    Article article = articleRepository.findById(id).orElse(null); // findById 는 Optional 타입
	    if (article != null) {
	        articleRepository.delete(article); // Article 삭제
	        rda.addFlashAttribute("msg", "삭제 성공");
	    } else {
	        rda.addFlashAttribute("msg", "삭제 실패, 해당 글이 없습니다.");
	    }
	    return "redirect:/articles/index"; // 삭제 후 목록 페이지로 리다이렉트
	}
	@GetMapping("/articles/index")
	public String articleList(Model model,
			@RequestParam(value= "word" ,defaultValue ="" ) String word, 
			@RequestParam(value = "currentPage", defaultValue = "0") int currentPage,
			@RequestParam(value = "rowPerPage", defaultValue = "6") int rowPerPage) {
		Sort s1 = Sort.by("id").descending();
		Sort s2 = Sort.by("title").ascending();
		Sort sort  = s1.and(s2);
		PageRequest pageable = PageRequest.of(currentPage, rowPerPage, sort);

		Page<Article> list = articleRepository.findByTitleContaining(pageable, word);
		// Page의 추가 속성
		
		/*
		 * Log.debug("list.getTotalElements(): "+ list.getTotalElements());
		 * Log.debug("list.getTotalPages(): "+ list.getTotalPages());
		 * Log.debug("list.getNumber(): "+ list.getNumber());
		 * Log.debug("list.getSize(): "+ list.getSize());
		 * Log.debug("list.getisFirst(): "+ list.isFirst());
		 * Log.debug("list.hasNext(): "+ list.hasNext());
		 */
		//model.addAttribute("word", word); 
		model.addAttribute("list", list);
		model.addAttribute("prePage", (list.getNumber() > 0) ? list.getNumber() - 1 : 0);
		model.addAttribute("nextPage" ,list.getNumber()+1 );
		return "articles/index";
	}

}
