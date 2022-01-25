package ruby.oauth2.config.web;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ruby.oauth2.config.auth.dto.LoginUserArgumentResolver;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final LoginUserArgumentResolver loginUserArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginUserArgumentResolver);
    }
}

/**
 * loginUserArgumentResolver 는 spring bean 객체
 *  - addArgumentResolvers 에 의해 loginUserArgumentResolver 가 추가
 *  - loginUserArgumentResolver 는 내부의 resolveArgument 를 통해 SessionUser 객체를 컨트롤러에 전달
 */