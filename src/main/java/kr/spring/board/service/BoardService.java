package kr.spring.board.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;

import kr.spring.board.vo.BoardFavVO;
import kr.spring.board.vo.BoardReplyVO;
import kr.spring.board.vo.BoardResponseVO;
import kr.spring.board.vo.BoardVO;

public interface BoardService {
	public List<BoardVO> selectList(Map<String,Object> map);
	public Integer selectRowCount(Map<String,Object> map);
	public void insertBoard(BoardVO board);
	public BoardVO selectBoard(Long board_num);
	public void updateHit(Long board_num);
	public void updateBoard(BoardVO board);
	public void deleteBoard(Long board_num);
	public void deleteFile(Long board_num);
	public BoardFavVO selectFav(BoardFavVO fav);
	public Integer selectFavCount(Long board_num);
	public void insertFav(BoardFavVO fav);
	public void deleteFav(BoardFavVO fav);
	
	public List<BoardReplyVO> selectListReply(Map<String,Object> map);
	
	public Integer selectRowCountReply(Map<String,Object> map);
	
	public void insertReply(BoardReplyVO boardReply);
	
	//댓글 수정, 삭제시 작성자 회원번호를 구하기 위해 사용
	public BoardReplyVO selectReply(Long re_num);
	
	public void updateReply(BoardReplyVO boardReply);
	
	public void deleteReply(Long re_num);
	
	//답글(대댓글)
		public List<BoardResponseVO> selectListResponse(Long re_num);
		
		public BoardResponseVO selectResponse(Long te_num);
		public void insertResponse(BoardResponseVO boardResponse);
		public void updateResponse(BoardResponseVO boardResponse);
		public void deleteResponse(Long te_num);
		//댓글 삭제시 답글 삭제용
		@Delete("delete from spboard_response where re_num=#{re_num}")
		public void deleteResponseByReNum(Long re_num);
		//답글 개수 표시를 위해서 사용
		public Integer selectResponseCount(Long re_num);
		//부모글 삭제시 댓글의 답글이 존재하면 댓글 번호를 구해서
		//답글 삭제시 사용
		@Delete("Delete from spboard_response where re_num in (select re_num from spboard_reply where board_num=#{board_num}) ")
		public void deleteResponseByBoardNum(Long board_num);
		

	
}
