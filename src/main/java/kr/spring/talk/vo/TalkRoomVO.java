package kr.spring.talk.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TalkRoomVO {
	private long talkroom_num;
	private String basic_name;
	private String talkroom_date;
	private long [] members; //채팅 맴버
	private long mem_num;
	private int room_cnt; //읽지 않은 메세지 수
	private TalkRoomVO talkVO;
	private TalkMemberVO talkMemberVO;
}
