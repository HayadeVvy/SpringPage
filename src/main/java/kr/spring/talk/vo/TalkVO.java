package kr.spring.talk.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TalkVO {
	private long talk_num;
	private long talkroom_num; //수신 그룹
	private long mem_num; //발신자
	private String message;
	private String chat_date;
	private int read_count; //읽지않은 메시지 수
	private String id;
	
}
