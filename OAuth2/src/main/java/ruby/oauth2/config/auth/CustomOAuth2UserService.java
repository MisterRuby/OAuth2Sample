package ruby.oauth2.config.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import ruby.oauth2.config.auth.dto.OAuthAttributes;
import ruby.oauth2.config.auth.dto.SessionUser;
import ruby.oauth2.domain.user.repository.UserInfoRepository;
import ruby.oauth2.domain.user.entity.UserInfo;

import javax.servlet.http.HttpSession;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserInfoRepository userInfoRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // registrationId - 현재 로그인 진행중인 서비스를 구분. ex) 네이버, 구글, 카카오
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        
        // userNameAttributeName - OAuth2 로그인 진행시 키가 되는 필드 값.
        String userNameAttributeName = userRequest
                .getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        
        // OAuthAttributes - OAuth2UserService 를 통해 가져온 OAuth2User 의 attributes 를 담을 클래스 
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        UserInfo userInfo = saveOrUpdate(attributes);

        // SessionUser - 세샨에 사용자 정보를 저장하기 위한 dto 클래스
        //          - UserInfo 엔티티가 아닌 별도의 dto 클래스를 생성해서 직렬화
        //          - 엔티티에는 자식 엔티티가 포함될 수 있으므로 별도의 dto에 정보를 담아 직렬화
        httpSession.setAttribute("userInfo", new SessionUser(userInfo));

        return new DefaultOAuth2User(
                Collections.singleton(
                        new SimpleGrantedAuthority(userInfo.getRoleKey())), attributes.getAttributes(), attributes.getNameAttributeKey());
    }

    // 사용자 정보가 업데이트 되었을 때 업데이트 된 내용을 반영
    private UserInfo saveOrUpdate(OAuthAttributes attributes) {
        UserInfo userInfo = userInfoRepository
                .findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName()))
                .orElse(attributes.toEntity());

        return userInfoRepository.save(userInfo);
    }
}
