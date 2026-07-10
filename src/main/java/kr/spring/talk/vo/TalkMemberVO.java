package kr.spring.talk.vo;

import java.sql.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TalkMemberVO {
	private long talkroom_num;
	private long mem_num;
	private String room_name;
	private Date member_date;
	
	private String id;
	
	private TalkVO talkVO;
}
