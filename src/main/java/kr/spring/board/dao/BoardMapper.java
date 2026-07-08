package kr.spring.board.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import kr.spring.board.vo.BoardFavVO;
import kr.spring.board.vo.BoardReplyVO;
import kr.spring.board.vo.BoardResponseVO;
import kr.spring.board.vo.BoardVO;
@Mapper
public interface BoardMapper {
	//부모글 
	public List<BoardVO> selectList(Map<String,Object> map);
	public Integer selectRowCount(Map<String,Object> map);
	public void insertBoard(BoardVO board);
	@Select("select * from spboard join spmember using (mem_num) where board_num=#{board_num}")
	@ResultMap("boardMap")
	public BoardVO selectBoard(Long board_num);
	@Update("update spboard set hit= hit + 1 where board_num = #{board_num}")
	public void updateHit(Long board_num);
	public void updateBoard(BoardVO board);
	@Delete("delete from spboard where board_num=#{board_num}")
	public void deleteBoard(Long board_num);
	@Update("Update spboard set filename='' where board_num=#{board_num}")
	public void deleteFile(Long board_num);
	
	//부모글 좋아요
	@Select("Select * from spboard_fav where board_num=#{board_num} and mem_num=#{mem_num} ")
	public BoardFavVO selectFav(BoardFavVO fav);
	@Select("Select count(*) from spboard_fav where board_num= #{board_num}")
	public Integer selectFavCount(Long board_num);
	@Insert("insert into spboard_fav (board_num, mem_num) values (#{board_num}, #{mem_num})")
	public void insertFav(BoardFavVO fav);
	@Delete("delete from spboard_fav where board_num=#{board_num} and mem_num= #{mem_num}")
	public void deleteFav(BoardFavVO fav);
	@Delete("delete from spboard_fav where board_num=#{board_num}")
	public void deleteFavByBoardNum(Long board_num);
	
	//댓글
	public List<BoardReplyVO> selectListReply(Map<String,Object> map);
	@Select("Select count(*) from spboard_reply where board_num=#{board_num}")
	public Integer selectRowCountReply(Map<String,Object> map);
	
	public void insertReply(BoardReplyVO boardReply);
	
	//댓글 수정, 삭제시 작성자 회원번호를 구하기 위해 사용
	@Select("select * from spboard_reply where re_num=#{re_num}")
	public BoardReplyVO selectReply(Long re_num);
	@Update("update spboard_reply set re_content=#{re_content}, re_ip=#{re_ip},re_mdate=sysdate where re_num=#{re_num}")
	public void updateReply(BoardReplyVO boardReply);
	@Delete("delete from spboard_reply where re_num=#{re_num}")
	public void deleteReply(Long re_num);
	
	
	//부모글 삭제시 댓글이 존재하면, 부모글 삭제 전 댓글 삭제
	@Delete("delete from spboard_reply where board_num=#{board_num}")
	public void deleteReplyByBoardNum(Long board_num);
	
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
