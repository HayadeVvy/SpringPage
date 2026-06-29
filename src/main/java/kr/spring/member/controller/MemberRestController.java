package kr.spring.member.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.spring.member.service.MemberService;
import kr.spring.member.vo.MemberVO;
import kr.spring.member.vo.PrincipalDetails;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/member")
	public class MemberRestController {
	@Autowired
	private MemberService memberService;
	
	//아이디 중복 체크
	@GetMapping("/confirmId/{id}")
	public ResponseEntity<Map<String,String>> checkId(@PathVariable String id) //id는 id거나 nick_name이 올수 있음
		{
			log.debug("<<아이디 중복 체크>> :" + id);
			
			Map<String,String> mapAjax = new HashMap<>();
			
			Map<String,String> map = new HashMap<String,String>();
			
			map.put("id", id);
			MemberVO member = memberService.selectIdAndNickName(map);
			
			if(id!= null)
			{
				//아이디 체크
				if(member != null)
				{
					//아이디 중복
					mapAjax.put("result", "idDuplicated");
				}
				else
				{
					if(!Pattern.matches("^[A-Za-z0-9]{4,14}$", id))
					{
						//패턴 불일치
						mapAjax.put("result", "notMatchPattern");
					}
					else
					{
						mapAjax.put("result","idnotFound");
					}
				}
			}
			else
			{ //아이디 입력 안함
				mapAjax.put("result", "error");
			}
			
			return new ResponseEntity<Map<String,String>>(mapAjax, HttpStatus.OK);
		}
	
	@GetMapping("/confirmNickName/{nick_name}")
	public ResponseEntity<Map<String,String>> checkNickName(@PathVariable String nick_name)
	{
		log.debug("<<닉네임 중복 체크>> :" + nick_name);
		
		Map<String,String> mapAjax = new HashMap<>();
		
		Map<String,String> map = new HashMap<String,String>();
		
		map.put("nick_name", nick_name);
		MemberVO member = memberService.selectIdAndNickName(map);
		
		if(nick_name!= null)
		{
			//별명 체크
			if(member != null)
			{
				//아이디 중복
				mapAjax.put("result", "nickDuplicated");
			}
			else
			{
				if(!Pattern.matches("^[A-Za-z0-9ㄱ-ㅎ가-힣]{2,10}$", nick_name))
				{
					//패턴 불일치
					mapAjax.put("result", "notMatchPattern");
				}
				else
				{
					mapAjax.put("result","nickNotFound");
				}
			}
		}
		else
		{ //아이디 입력 안함
			mapAjax.put("result", "error");
		}
		
		return new ResponseEntity<Map<String,String>>(mapAjax, HttpStatus.OK);
	}
	
	//프로필 사진 업로드 
	@PutMapping("/updateMyPhoto")
	public ResponseEntity<Map<String,String>> processProfile(MemberVO member, @AuthenticationPrincipal PrincipalDetails principal)
	{
		Map<String,String> mapAjax= new HashMap<String,String>();
		
		member.setMem_num(principal.getMemberVO().getMem_num());
		
		memberService.updateProfile(member);
		mapAjax.put("result", "success");
		
		
		
		return new ResponseEntity<Map<String,String>>(mapAjax, HttpStatus.OK);
	}
}
