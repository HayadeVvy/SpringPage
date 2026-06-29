package kr.spring.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import kr.spring.member.security.CustomAccessDeniedHandler;
import kr.spring.member.security.UserSecurityService;
import lombok.extern.slf4j.Slf4j;
 
@Slf4j
//이 클래스가 spring 설정 파일이라는 의미
@Configuration
//모든 요청 URL이 SpringSecurity의 제어를 받도록 만든 annotation
@EnableWebSecurity
//Controller 메서드 레벨에서 권한을 체크 할수 있도록 설정
//@PreAuthorize 사용시 추가
@EnableMethodSecurity
public class SecurityConfig{
	//쿠키에 사용되는 값을 암호화하기 위한 키(key) 값
	@Value("${dataconfig.rememberme-key}")
	private String rememberme_key;
	//DB연동을 위한 DATAsource 지정
	@Autowired
	@Qualifier("dataSource")
	private DataSource dataSource;
	
	//로그인 시 사용자 정보를 조회하고, 이를 기반으로 인증 Authentication을 수행하는데 사용
	// 로그인시 DB에서 회원 정보를 가져올 때 사용
	@Autowired
	private UserSecurityService userSecurityService;
	//인증(로그인)에 성공한 후 Redirect할 URL을 지정하거나, 처리 로직을 직접 작성할때 사용
	@Autowired
	private AuthenticationSuccessHandler authenticationSuccessHandler;
	//로그인 실패 시 처리를 담당하는 클래스, 사용자가 인증(로그인)을 시도 했지만, 실패 했을때, 사용자를 어떤 URL로 redirect할지를 지정하거나 추가적인 오직을 실행
	@Autowired
	private AuthenticationFailureHandler authenticationFailureHandler;
	//권한이 없을때 처리하는 클래스
	@Autowired
	private CustomAccessDeniedHandler customAccessDeniedHandler;
	//보안 설정의 핵심 역할
	//HTTP 요청에 대해, 어떤 보안 규칙을 적용할 것인지 설정하는 method.
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {	
		return http
				//URL 접근 권한 설정
				.authorizeHttpRequests(authorize-> authorize
						// /admin/** : ROLE_ADMIN
						.requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
						//기타 경로: 누구나 접근 가능함.
						.requestMatchers(
						        "/assets/**",
								"/",
								"/main/**",
								"/member/**",
								"/board/**"
						).permitAll()
						//위조건 외에는 인증 필요. 인증되지 않은 요청은 로그인 페이지로 redirect함.
						.anyRequest().authenticated() 
						)
				//일반 로그인 설정
				.formLogin(login -> login 
						//사용자 정의 로그인 페이지 주소
						.loginPage("/member/login")
						//로그인 성공시 처리할때 사용
						.successHandler(authenticationSuccessHandler)
						//로그인 실패 시 처리
						.failureHandler(authenticationFailureHandler)
						//로그인 폼의 아이디  input name
						.usernameParameter("id")
						//로그인 폼의 비밀번호 input name
						.passwordParameter("passwd"))
				//로그아웃 설정
				.logout(logout -> logout 
						//로그아웃 요청 URL
						.logoutUrl("/member/logout")
						//로그아웃 후 이동 페이지
						.logoutSuccessUrl("/")
						//Session 제거
						.invalidateHttpSession(true)
						//쿸키 삭제
						.deleteCookies("remember-me","JSESSIONID"))
				//예외 처리 설정
				.exceptionHandling(error -> error
						//권한 없는 접근 발생 시 실행
					    .accessDeniedHandler(customAccessDeniedHandler)
					)
				//자동 로그인 설정(Remember me)
				.rememberMe(me -> me
						//쿠키에 사용되는 값을 암호화 하기 위한 키 값
						.key(rememberme_key) 
						//토큰을  데이터 베이스에 저장
						.tokenRepository(persistentTokenRepository()) 
						//자동 로그인 유지 시간 (7일)
						.tokenValiditySeconds(60*60*24*7)
						//사용자 정보 조회 서비스
						.userDetailsService(userSecurityService))
				.build();	
	}
	//비밀번호 암호화 객체 생성
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	/*
	 * 
	 * persistent_logins 테이블 컬럼
	 * series: 사용자의 로그인 세션을 식별하는 고유한 값
	 *  username:로그인한 사용자의 아이디
	 * token: 사용자의 브라우저에 저장되는 토큰 값
	 * 			(쿠키에 저장되는 암호화된 토큰 값)
	 * 			이 토큰을 통해 시스템은 사용자를 인증함.
	 * 			매번 로그인이 유지될때 마다 갱신됨
	 * 			토큰이 일치하지 않으면 Remeber-me 세션이 무효화
	 * last_used: 토큰이 마지막으로 사용된 시각
	 * 			  토큰의 유효 시간을 관리하는데 사용
	 * 
	 */
	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		//JDBC 기반 토큰 저장 객체 생성
		JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
		//DB 연결 설정
		repo.setDataSource(dataSource);
		return repo;
	}
}





