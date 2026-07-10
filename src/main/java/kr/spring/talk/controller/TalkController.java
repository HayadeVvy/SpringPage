package kr.spring.talk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.spring.member.service.MemberService;
import kr.spring.member.vo.MemberVO;
import kr.spring.talk.service.TalkService;
import kr.spring.talk.vo.TalkRoomVO;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/talk")
public class TalkController {

	@Autowired
	private TalkService talkService;
	
	@Autowired
	private MemberService memberService;
	
	//채팅방 목록
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/talkList")
	public String chatList()
	{
		return "thviews/talk/talkList";
	}
	
	//채팅방 생성 폼 호출
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/talkRoomWrite")
	public String talkRoomWrite()
	{
		return "thviews/talk/talkRoomWrite";
	}
	
	//초대한 회원의 id 구하기
	private String findMemberID(TalkRoomVO vo, MemberVO user)
	{
		String member_id = "";
		long[] members = vo.getMembers();
		for(int i=0; i<members.length; i++)
		{
			String temp_id = memberService.selectMember(members[i]).getId();
			//초대한 사람의 아이디는 제외
			if(!user.getId().equals(temp_id))
			{
				member_id += temp_id;
				if(i< members.length-1) member_id += ", ";
			}
		}
		
		return member_id;
	}
	
}
