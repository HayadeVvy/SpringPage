package kr.spring.board.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.spring.board.service.BoardService;
import kr.spring.board.vo.BoardVO;
import kr.spring.member.vo.PrincipalDetails;
import kr.spring.util.FileUtil;
import kr.spring.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/board")
public class BoardUserController {

	@Autowired
	private BoardService boardService;
	
	//자바빈 초기화
	@ModelAttribute
	public BoardVO initCommand()
	{
		return new BoardVO();
	}
	//폼 호출
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/write")
	public String form()
	{
		return "thviews/board/boardWrite";
	}
	
	//전송된 데이터 처리
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/write")
	public String submit(@Valid BoardVO board, BindingResult result, HttpServletRequest request, @AuthenticationPrincipal PrincipalDetails principal, Model model)throws IllegalStateException, IOException
	{
		log.debug("<<게시판 글 저장>> : " + board);
		
		//유효성 체크시 오류가 있으면, 폼 호출
		if(result.hasErrors())
		{
			//유효성 체크결과 오류 표시
			ValidationUtil.printErrorFields(result);
			return form();
		}
		
		//회원번호 세팅
		board.setMem_num(principal.getMemberVO().getMem_num());
		
		//ip세팅
		board.setIp(request.getRemoteAddr());
		
		//파일 세팅
		board.setFilename(FileUtil.createFile(request, board.getUpload()));
		
		boardService.insertBoard(board);
		
		model.addAttribute("message", "글 등록을 완료!");
		model.addAttribute("url", request.getContextPath()+"/board/list");
		
		return "thviews/common/resultAlert";
	}
	
	//게시판 목록
	@GetMapping("/list")
	public String getList()
	{
		return "thviews/board/boardList";
	}
	
	
}
