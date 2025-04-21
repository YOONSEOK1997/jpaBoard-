package com.example.jpaboard.entity;

// JPA 관련 어노테이션 import
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Lombok 어노테이션 import (자동으로 생성자, getter/setter 생성)
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // getter, setter, toString, equals, hashCode 자동 생성 (Lombok)
@NoArgsConstructor // 기본 생성자 자동 생성 (파라미터 없는 생성자)
@AllArgsConstructor // 모든 필드를 매개변수로 받는 생성자 자동 생성
@Entity // 이 클래스는 JPA의 Entity (DB 테이블과 매핑됨)
@Table(name="Article") // DB에서 "Article"이라는 테이블로 매핑됨
public class Article {

    @Id // 이 필드는 테이블의 Primary Key (기본 키)
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    // DB에서 자동 증가 방식으로 ID 값을 생성 (MySQL의 auto_increment와 같음)
    //column id 가 id면 @Column(name = "id") 생략 가능
    private long id;

    @Column(name="title") 
    // DB 테이블의 "title"이라는 컬럼에 매핑
    private String title;

    @Column(name="content") 
    // DB 테이블의 "content"라는 컬럼에 매핑
    private String content;
  
}
