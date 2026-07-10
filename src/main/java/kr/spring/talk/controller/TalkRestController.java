package kr.spring.talk.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.spring.member.service.MemberService;
import kr.spring.member.vo.MemberVO;
import kr.spring.member.vo.PrincipalDetails;
import kr.spring.talk.service.TalkService;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/talk")
public class TalkRestController {

	@Autowired
	private TalkService talkService;
	
	@Autowired
	private MemberService memberService;
	
	//채팅 회원 검색
	@GetMapping("/memberSearchAjax/{id}")
	public ResponseEntity<Map<String,Object>> memberSearchAjax(@PathVariable String id, @AuthenticationPrincipal PrincipalDetails principal)
	{
		Map<String,Object> mapAjax = new HashMap<String,Object>();
		
		if(principal == null) //로그인이 안된경우
		{
			mapAjax.put("result", "logout");
		}
		else	//로그인이 된 경우
		{
			List<MemberVO> member = memberService.selectSearchMember(id);
			mapAjax.put("result", "success");
			mapAjax.put("member", member);
		}
		
		return new ResponseEntity<Map<String,Object>>(mapAjax, HttpStatus.OK);
	}
	
}
