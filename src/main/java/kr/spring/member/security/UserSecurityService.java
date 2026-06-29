package kr.spring.member.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import kr.spring.member.service.MemberService;
import kr.spring.member.vo.MemberVO;
import kr.spring.member.vo.PrincipalDetails;
import kr.spring.member.vo.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserSecurityService implements UserDetailsService {
	private final MemberService memberService;

	@Override
	public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException{
		log.debug("[Login Check 1 - UserSecurityService] 로그인 아이디 :" + id);
		MemberVO member = memberService.selectCheckMember(id);
		if (member==null || member.getAuthority().equals(UserRole.INACTIVE.getValue())) {
			log.debug("[Login Check 1] 로그인 아이디가 없거나 탈퇴회원");
			throw new UsernameNotFoundException("UserNotFound");
		}
		return new PrincipalDetails(member);
	}
}
