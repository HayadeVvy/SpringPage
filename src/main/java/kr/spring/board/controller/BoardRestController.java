package kr.spring.board.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.tomcat.util.digester.ServiceBindingPropertySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import kr.spring.board.service.BoardService;
import kr.spring.board.vo.BoardFavVO;
import kr.spring.board.vo.BoardReplyVO;
import kr.spring.board.vo.BoardResponseVO;
import kr.spring.board.vo.BoardVO;
import kr.spring.member.vo.PrincipalDetails;
import kr.spring.util.FileUtil;
import kr.spring.util.PagingUtil;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/board")
public class BoardRestController {

	@Autowired
	private BoardService service;
	
	//부모글 업로드 파일 삭제
	@DeleteMapping("/deleteFile/{board_num}")
	public ResponseEntity<Map<String,String>> processFile(@PathVariable long board_num, @AuthenticationPrincipal PrincipalDetails principal, HttpServletRequest request)
	{
		// {}: 로그 출력용 자리 표시자(placeholder)
		//예: log.debug("회원번호 : {}, 글번호 : {}, mem_num, board_num")
		log.debug("<<파일 삭제>> : {}", board_num);
		Map<String,String> mapAjax = new HashMap<String,String>();
		
		
		BoardVO db_board = service.selectBoard(board_num);
		
		//로그인한 회원 번호와 작성자 회원번로 일치 여부 체크
		
		if(principal.getMemberVO().getMem_num() != db_board.getMem_num())
		{
			//불일치
			mapAjax.put("result", "wrongAccess");
		}
		else
		{
			service.deleteFile(board_num);
			FileUtil.removeFile(db_board.getFilename());
			
			mapAjax.put("result", "success");		
		}
		
		return new ResponseEntity<Map<String,String>>(mapAjax, HttpStatus.OK);
	}
	
	//부모글 좋아요 - 읽기
	
	@GetMapping("/getFav/{board_num}")
	public ResponseEntity<Map<String,Object>> getFav(@PathVariable long board_num, @AuthenticationPrincipal PrincipalDetails principal)
	{
		log.debug("<<게시판 좋아요>> board_num: {}" + board_num );
		
		Map<String,Object> mapAjax = new HashMap<String,Object>();
		BoardFavVO fav = new BoardFavVO();
		
		fav.setBoard_num(board_num);
		if(principal == null)
		{
			//로그인 안되있는 경우
			mapAjax.put("status", "noFav");
		}
		else
		{
			//로그인 되어 있는 경우
			fav.setMem_num(principal.getMemberVO().getMem_num());
			BoardFavVO boardFav = service.selectFav(fav);
			
			if(boardFav != null)
			{
				mapAjax.put("status", "yesFav");
			}
			else
			{
				mapAjax.put("status", "noFav");
			}
			
		}
		
		mapAjax.put("count", service.selectFavCount(fav.getBoard_num()));
		
		return new ResponseEntity<Map<String,Object>>(mapAjax, HttpStatus.OK);
	}
	
	//부모글 좋아요 등록및 삭제
	@PostMapping("/writeFav")
	public ResponseEntity<Map<String,Object>> writeFav(@RequestBody BoardFavVO fav, @AuthenticationPrincipal PrincipalDetails principal)
	{
		log.debug("<<게시판 좋아요 - 등록>>: {}", fav);
		
		Map<String,Object> mapAjax = new HashMap<>();
		
		//서버로 보낸 데이터
		if(principal == null)
		{
			mapAjax.put("result", "logout");
		}
		else
		{
			//로그인한 회원번호를 boardFAV에 세팅
			fav.setMem_num(principal.getMemberVO().getMem_num());
			
			BoardFavVO boardFav = service.selectFav(fav);
			
			if(boardFav != null)
			{
				service.deleteFav(fav);
				mapAjax.put("status", "noFav");
			}
			else
			{
				service.insertFav(fav);
				mapAjax.put("status", "yesFav");
			}
			mapAjax.put("result", "success");
			mapAjax.put("count", service.selectFavCount(fav.getBoard_num()));
		}
		
		return new ResponseEntity<Map<String,Object>>(mapAjax,HttpStatus.OK);
	}
	
	//댓글 등록
	@PostMapping("/writeReply")
	public ResponseEntity<Map<String,String>> writeReply(@RequestBody BoardReplyVO boardReplyVO, @AuthenticationPrincipal PrincipalDetails principal, HttpServletRequest request)
	{
		log.debug("<<댓글 등록>> : {}", boardReplyVO);
		
		Map<String,String> mapAjax = new HashMap<>();
		
		//회원번호 저장
		boardReplyVO.setMem_num(principal.getMemberVO().getMem_num());
		
		//ip 배정
		boardReplyVO.setRe_ip(request.getRemoteAddr());
		//댓글 등록
		service.insertReply(boardReplyVO);
		mapAjax.put("result", "success");
		
		return new ResponseEntity<Map<String,String>>(mapAjax, HttpStatus.OK);
	}
	
	@GetMapping("/listReply/{board_num}/{pageNum}/{rowCount}")
	public ResponseEntity<Map<String,Object>> getList(@PathVariable long board_num, @PathVariable int pageNum, @PathVariable int rowCount, @AuthenticationPrincipal PrincipalDetails principal){
		
		log.debug("<<댓글 목록>> board_num: {}, pageNum: {}", board_num, pageNum);
		
		Map<String,Object> map = new HashMap<String,Object>();
		
		map.put("board_num", board_num);
		
		//총 글의 개수
		int count = service.selectRowCount(map);
		
		List<BoardReplyVO> list = null;
		
		if(count > 0)
			
		{
			PagingUtil page = new PagingUtil(pageNum, count, rowCount);
			map.put("skip", page.getSkip());
			map.put("limit", page.getLimit());
			
			list = service.selectListReply(map);
		}
		else
		{
			list = Collections.emptyList();
		}
		
		Map<String,Object> mapAjax = new HashMap<String,Object>();
		mapAjax.put("count", count);
		mapAjax.put("list", list);
		//로그인한 회원번호와 작성자 회원번호 일치 여부를 체크하기
		//위해 로그인한 회원번호 전송
		if(principal!=null)
		{
			mapAjax.put("user_num", principal.getMemberVO().getMem_num());
		}
		
		return new ResponseEntity<Map<String,Object>>(mapAjax, HttpStatus.OK);
	}
	
	//댓글 수정
	@PutMapping("/updateReply")
	public ResponseEntity<Map<String,String>> modifyReply(@RequestBody BoardReplyVO reply, @AuthenticationPrincipal PrincipalDetails principal, HttpServletRequest request)
	{
		log.debug("<<댓글 수정>> : {}", reply);
		
		Map<String,String> mapAjax = new HashMap<>();
		
		BoardReplyVO db_reply = service.selectReply(reply.getRe_num());
		
		if(principal.getMemberVO().getMem_num() == db_reply.getMem_num())
		{
			//로그인 회원번호롸 작성자 회원번호 일치
			//ip저장
			reply.setRe_ip(request.getRemoteAddr());
			mapAjax.put("result", "success");
		}
		else
		{
			//로그인 회원번호와 작성자 회원번호 불일치
			mapAjax.put("result", "wrongAccess");
		}
		return new ResponseEntity<Map<String,String>>(mapAjax, HttpStatus.OK);
	}
	
	//댓글 삭제
	@DeleteMapping("/deleteReply/{re_num}")
	public ResponseEntity<Map<String,String>> deleteReply(@PathVariable long re_num, @AuthenticationPrincipal PrincipalDetails principal)
	{
		log.debug("<<댓글 삭제>> : re_num : {}", re_num);
		
		Map<String,String> mapAjax = new HashMap<>();
		BoardReplyVO db_reply = service.selectReply(re_num);
		if(principal.getMemberVO().getMem_num() == db_reply.getMem_num())
		{
			//로그인한 회원번호와 작성자 회원번호가 일치
			service.deleteReply(re_num);
			mapAjax.put("result", "success");
			
		}
		else
		{
			//로그인한 회원번호와 작성자 회원번호 불일치
			mapAjax.put("result", "wrongAccess");
		}
		
		return new ResponseEntity<Map<String,String>>(mapAjax, HttpStatus.OK);
	}
	
	//답글 등록
	@PostMapping("/writeResponse")
	public ResponseEntity<Map<String,String>> writeResponse(@RequestBody BoardResponseVO boardResponseVO, @AuthenticationPrincipal PrincipalDetails principal, HttpServletRequest request)
	{
		log.debug("<<답글 등록>> : {}", boardResponseVO);
		Map<String, String> mapAjax = new HashMap<>();
		
		//회원 번호 저장
		boardResponseVO.setMem_num(principal.getMemberVO().getMem_num());
		
		//아이피 저장
		boardResponseVO.setTe_ip(request.getRemoteAddr());
		
		//답글 등록
		service.insertResponse(boardResponseVO);
		mapAjax.put("result", "success");
		
		
		
		return new ResponseEntity<Map<String,String>>(mapAjax, HttpStatus.OK);
	}
	
	//답글 등록
	@GetMapping("/listResp/{re_num}")
	public ResponseEntity<Map<String,Object>> getListResp(@PathVariable long re_num, @AuthenticationPrincipal PrincipalDetails principal)
	{
		log.debug("<<답글 목록>> re_num : {}", re_num);
		
		List<BoardResponseVO> list = service.selectListResponse(re_num);
		Map<String, Object> mapAjax = new HashMap<String, Object>();
		mapAjax.put("list", list);
		
		if(principal!= null)
		{
			mapAjax.put("user_num", principal.getMemberVO().getMem_num());
		}
		
		return new ResponseEntity<Map<String,Object>>(mapAjax, HttpStatus.OK);
	}
	
	
}
