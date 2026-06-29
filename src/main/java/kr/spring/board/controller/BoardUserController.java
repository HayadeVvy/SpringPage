package kr.spring.board.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.spring.board.service.BoardService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/board")
public class BoardUserController {

	@Autowired
	private BoardService boardService;
	
	//게시판 목록
	@GetMapping("/list")
	public String getList()
	{
		return "thviews/board/boardList";
	}
	
}
