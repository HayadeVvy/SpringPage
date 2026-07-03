package kr.spring.board.vo;

import kr.spring.member.vo.MemberVO;
import kr.spring.util.DurationFromNow;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BoardReplyVO {
	private long re_num;
	private String re_content;
	private String re_date;
	private String re_mdate;
	private String re_ip;
	private long board_num;
	private long mem_num;
	
	private int resp_cnt;
	
	
	private MemberVO memberVO;
	
	public void setRe_date(String re_date)
	{
		this.re_date = DurationFromNow.getTimeDiffLabel(re_date);
	}
	public void setRe_mdate(String re_mdate)
	{
		this.re_mdate = DurationFromNow.getTimeDiffLabel(re_mdate);
	}
}
