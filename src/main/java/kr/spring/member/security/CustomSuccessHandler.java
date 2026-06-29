package kr.spring.member.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.support.SessionFlashMapManager;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.spring.member.vo.MemberVO;
import kr.spring.member.vo.PrincipalDetails;
import kr.spring.member.vo.UserRole;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler{
	
	@Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
    		HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        MemberVO user = ((PrincipalDetails)authentication.getPrincipal()).getMemberVO();
        log.debug("[Login Check 2] CustomSuccessHandler : " + user);
       
		if(user.getAuthority().equals(UserRole.ADMIN.getValue())) {					
			setDefaultTargetUrl("/admin/home");
		}else if(user.getAuthority().equals(UserRole.SUSPENDED.getValue())) {	
			log.debug("[Login Check 2] 정지회원 : " + user.getId());
			new SecurityContextLogoutHandler().logout(request, response, authentication);
			
			FlashMap flashMap = new FlashMap();
	        flashMap.put("error", "error_suspended");
	        FlashMapManager flashMapManager = new SessionFlashMapManager();
	        flashMapManager.saveOutputFlashMap(flashMap, request, response);
	        
			setDefaultTargetUrl("/member/login");
		}else {
			setDefaultTargetUrl("/");
		}
       super.onAuthenticationSuccess(request, response, authentication);
    }
}
