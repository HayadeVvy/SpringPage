package kr.spring.talk.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.*;

import kr.spring.talk.vo.TalkMemberVO;
import kr.spring.talk.vo.TalkRoomVO;
import kr.spring.talk.vo.TalkVO;

@Mapper
public interface TalkMapper {
	//채팅방 번호
	@Select("Select sptalkroom_seq.nextval from dual")
	public Integer selectTalkRoomNum();
	//채팅방 생성
	@Insert("insert into sptalkroom (talkroom_num, basic_name) values (#{talkroom_num}, #{basic_name})")
	public void insertTalkRoom(TalkRoomVO talkRoomVO);
	//채팅방 멤버 등록
	@Insert("insert into sptalk_member (talkroom_num, room_name, mem_num) values (#{talkroom_num}, #{room_name}, #{mem_num})")
	public void insertTalkRoomMember(
	@Param(value="talkroom_num") Long talkroom_num,
	@Param(value="room_name") String room_name,
	@Param(value="mem_num") Long mem_num);
	
	//채팅방 목록
	public List<TalkRoomVO> selectTalkRoomList(Map<String,Object> map);
	
	public Integer selectTalkRoomRowCount(Map<String, Object> map);
	
	//채팅 멤버 읽기
	public List<TalkMemberVO> selectTalkMember(Long talkroom_num);
	
	//채팅 메세지 번호 생성
	public Integer selectTalkNum();
	
	//채팅 메세지 등록
	public void insertTalk(TalkVO talkVO);
	
	//읽지 않은 채팅 기록 저장
	public void insertTalkRead(
	@Param(value="talkroom_num") Long talkroom_num,
	@Param(value="talk_num") Long talk_num,
	@Param(value="mem_num") Long mem_num
	);
	
	//채팅 메세지 읽기
	public List<TalkVO> selectTalkDetail(Map<String,Object> map);
	
	//읽은 채팅 기록 삭제
	public void deleteTalkRead(Map<String,Long> map);

}
