package kr.spring.board.vo;

import java.sql.Date;

import org.springframework.web.multipart.MultipartFile;

import kr.spring.member.vo.MemberVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = {"content"})
public class BoardVO {
	private long board_num;
	private String title;
	private String content;
	private int hit;
	private Date reg_date;
	private Date modify_date;
	private MultipartFile upload;
	private String ip;
	private long mem_num;
	
	private int re_cnt; //댓글 개수
	private int fav_cnt; //좋아요 개수
	
	private MemberVO memberVO;
}
