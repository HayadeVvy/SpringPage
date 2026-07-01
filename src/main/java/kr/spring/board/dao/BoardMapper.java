package kr.spring.board.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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
	public void deleteBoard(Long board_num);
	@Update("Update spboard set filename='' where board_num=#{board_num}")
	public void deleteFile(Long board_num);
}
