package kr.spring.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.spring.member.service.MemberService;
import kr.spring.member.vo.MemberVO;
import kr.spring.member.vo.PrincipalDetails;
import kr.spring.util.FileUtil;
import kr.spring.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/member")
public class MemberUserController {
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	//자바빈(VO) 초기화
	@ModelAttribute
	public MemberVO initCommand()
	{
		return new MemberVO();
	}
	
	//회원 가입 폼 호출
	@GetMapping("/registerUser")
	public String form()
	{
		return "thviews/member/memberRegister";
	}
	
	//회원 가입 데이터 처리
	@PostMapping("/registerUser")
	public String register(@Valid MemberVO member, BindingResult result, Model model, HttpServletRequest request)
	{
		
		log.debug("<<회원 가입>> :" + member);
		
		//유효성 체크
		if(result.hasErrors())
		{
			//오류필드 출력
			ValidationUtil.printErrorFields(result);
			return form();
		}
		
		//SpringSecurity 비밀번호 암호화
		member.setPasswd(passwordEncoder.encode(member.getPasswd()));
		
		memberService.insertMember(member);
		
		//결과 메세지 처리
		
		model.addAttribute("accessTitle", "회원 가입");
		model.addAttribute("accessMsg", "회원 가입이 완료되었습니다");
		model.addAttribute("accessBtn", "홈으로");
		model.addAttribute("accessUrl", request.getContextPath()+"/main/home");
		
		return "thviews/common/resultView";
	}
	//회원 로그인 폼 호출
	@GetMapping("/login")
	public String formLogin()
	{
		return "thviews/member/memberLogin";
	}
	
	//마이페이지 호출
	/*
	 * @PreAuthorize
	 * 메서드 호출이전에 접근  권한을 검사.
	 * 메서드 실행전에 주어진 SpEL(Spring Expression Lang) 조건을 평가하여
	 * 접근을 허용할지 결정
	 * 자주 사용하는 spEL 표현식
	 * 표현식								의미
	 * hasRole('ROLE_ADMIN')				'ROLE_ADMIN' 권한이 있는 사용자만 허용함
	 * hasAuthority("admin") 				"admin" 권한이 있는 사용자만 허용
	 * isAuthenticated()					로그인된 사용자만 허용
	 * isAnonymous()						로그인이 되지 않은 사용자만 허용
	 * #id = authentication.principal.id
	 * 						파라미터와 현재 사용자 비교
	 * @AuthenticationPrincipal				현재 로그인한 사용자의 정보를 메서드 파라미터로 주입해주는 spring  어노테이션. 인증된 사용자가 없으면 null.
	 */
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/myPage")
	public String getMyPage(@AuthenticationPrincipal PrincipalDetails principal, Model model ) {
		
		//회원 정보
		MemberVO member = memberService.selectMember(principal.getMemberVO().getMem_num());
		
		model.addAttribute("member", member);
		
		return "thviews/member/memberView";
	}
	
	//회원정보 수정
	@GetMapping("/updateUser")
	@PreAuthorize("isAuthenticated()") //로그인한 회원 번호 뽑기 -> principal이 로그인한 유저의 memberVO를 저장하고 있음
	public String formUpdate(@AuthenticationPrincipal PrincipalDetails principal, Model model) {
		MemberVO member = memberService.selectMember(principal.getMemberVO().getMem_num());
		
		model.addAttribute("memberVO", member);
		
		
		return "thviews/member/memberModify";
	}
	
	//수정폼에서 전송된 데이터 처리
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/updateUser")
	public String submitUpdate(@Valid MemberVO member, BindingResult result, @AuthenticationPrincipal PrincipalDetails prin) {
		
		log.debug("<<회원 정보 수정>> : " +member);
		
		if(result.hasErrors())
		{
			//어떤 필드에서 에러나는지를 뽑아주는 class사용
			ValidationUtil.printErrorFields(result);
			return "thviews/member/memberModify";
		}
		
		//회원번호를 VO에 매핑
		member.setMem_num(prin.getMemberVO().getMem_num());
		
		//회원정보수정
		memberService.updateMember(member);
		
		//Principal details 업데이트(이름과 이메일 최신화)
		prin.getMemberVO().setNick_name(member.getNick_name());
		prin.getMemberVO().setEmail(member.getEmail());
		
		
		return "redirect:/member/myPage";
	}
	
	//프로필 사진 출력
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/photoView")
	public String getProfile(@AuthenticationPrincipal PrincipalDetails principal, HttpServletRequest request, Model model)
	{
		try
		{
			MemberVO user = principal.getMemberVO();
			MemberVO member = memberService.selectMember(user.getMem_num());
			
			viewProfile(member, request, model);
		}
		catch(Exception e)
		{
			getBasicProfileImage(request, model);
		}
		return "imageView";
	}
	//프로필 사진 처리를 위한 공통 코드
	public void viewProfile(MemberVO member, HttpServletRequest request, Model model)
	{
		if(member == null || member.getPhoto_name() == null)
		{
			//DB에 저장된 프로필 이미지가 없기 때문에 기본 이미지 호출
			getBasicProfileImage(request,model);
		}
		else
		{
			//업로드한 이미지 읽기
			model.addAttribute("imageFile", member.getPhoto());
			model.addAttribute("filename", member.getPhoto_name());
		}
	}
	
	//기본 이미지 읽기
	public void  getBasicProfileImage(HttpServletRequest request, Model model)
	{
		byte[] readbyte = FileUtil.getBytes(request.getServletContext().getRealPath("/assets/image_bundle/face.png"));
		
		model.addAttribute("imageFile",readbyte);
		model.addAttribute("filename", "face.png");
	}
}
