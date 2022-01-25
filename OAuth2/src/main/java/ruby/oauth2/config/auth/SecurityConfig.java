package ruby.oauth2.config.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import ruby.oauth2.domain.user.enumType.Role;

@RequiredArgsConstructor
@EnableWebSecurity      // @Configuration 을 포함
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .headers().frameOptions().disable();

        http
                .authorizeRequests()                                                // url별 권한 관리를 설정하는 옵션의 시작점
                .mvcMatchers("/", "/h2-console/**").permitAll()            // permitAll() : 인증 x
                .antMatchers("/admin/**").hasRole(Role.ADMIN.name())     // hasRole() : 해당 권한이 있어야 접근 가능
                .antMatchers("/user/**").hasRole(Role.USER.name())
                .anyRequest().authenticated();
//                .expressionHandler(expressionHandler());

        http.logout().logoutSuccessUrl("/");
        http.oauth2Login()
                .userInfoEndpoint()
                .userService(customOAuth2UserService);          // 소셜 로그인 성공시 후속조치를 진행할 인터페이스의 구현체를 등록
    }

    public SecurityExpressionHandler expressionHandler() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");

        DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy);

        return expressionHandler;
    }
}
