package kr.spring.board.service;

import java.util.List;
import java.util.Map;

import kr.spring.board.vo.BoardFavVO;
import kr.spring.board.vo.BoardReplyVO;
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
}
