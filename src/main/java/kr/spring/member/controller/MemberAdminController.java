package kr.spring.member.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import kr.spring.member.service.MemberService;
import kr.spring.member.vo.MemberVO;
import kr.spring.util.PagingUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin/member")
public class MemberAdminController {
	@Autowired
	private MemberService memberService;
	
	@GetMapping("/admin_list")
	public String getList(@RequestParam(defaultValue= "1") int pageNum, String keyfield, String keyword, Model model)
	{
		Map<String,Object> map = new HashMap<String, Object>();
		
		map.put("keyfield", keyfield);
		map.put("keyword", keyword);
		
		//전체 검색 레코드수
		int count = memberService.selectRowcount(map);
		
		log.debug("<<회원목록 - count : {}>>", count);
		
		//페이지 처리
		PagingUtil page = new PagingUtil(keyfield, keyword, pageNum, count, 20, 10, "admin_list");
		
		List<MemberVO> list = null;
		
		if(count > 0)
		{
			map.put("skip", page.getSkip());
			map.put("limit", page.getLimit());
			
			list = memberService.selectList(map);
		}
		
		model.addAttribute("list", list);
		model.addAttribute("count", count);
		model.addAttribute("page", page.getPage());
		model.addAttribute("keyfield", keyfield);
		model.addAttribute("keyword", keyword);
		
		return "thviews/member/admin_list";
	}
	
	//회원 권한 수정- 매니저
	@GetMapping("/admin_update")
	public String form(long mem_num, Model model)
	{
		MemberVO memberVO = memberService.selectMember(mem_num);
		model.addAttribute("memberVO", memberVO);
		
		
		return "thviews/member/admin_modify";
	}
	
	//수정폼
	@PostMapping("/admin_update")
	public String submit(MemberVO memberVO, Model model, HttpServletRequest request)
	{
		log.debug("<<관리자 회원권한 수정: {}>>", memberVO);
		
		//회원권한 수정
		memberService.updateByAdmin(memberVO);
		
		//view에 표시할 메시지
		model.addAttribute("accessTitle", "회원 권한 수정");
		model.addAttribute("accessBtn", "이동");
		model.addAttribute("accessMsg", "회원권한 수정 완료");
		model.addAttribute("accessUrl", request.getContextPath()+"/admin/member/admin_update?mem_num=" + memberVO.getMem_num());
		
		return "thviews/common/resultView";
	}
	
}
