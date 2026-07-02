package kr.spring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//자바코드 기반 설정 클래스
@Configuration
public class AppConfig implements WebMvcConfigurer{
	
	@Value("${file.upload.path}")
	private String uploadPath;
	
	//Spring MVC에서 별도의 Controller 클래스 없이 특정 URL 요청을 바로 View로 연결
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/resultView").setViewName("thviews/common/resultView");
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
    	//웹상에서 호출한 경로를 실제경로로 변경
    	registry.addResourceHandler("/assets/upload/**").addResourceLocations("file:" + uploadPath);
    }
    
}
