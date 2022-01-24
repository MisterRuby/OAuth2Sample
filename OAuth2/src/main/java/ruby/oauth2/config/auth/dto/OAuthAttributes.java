package ruby.oauth2.config.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ruby.oauth2.domain.user.entity.UserInfo;
import ruby.oauth2.domain.user.enumType.Role;

import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;

    /**
     * @param registrationId
     * @param userNameAttributeName oAuth2User 에서 반환하는 사용자 정보
     * @param attributes
     * @return
     */
    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    /**
     * UserInfo 엔티티 생성
     *  - userInfoRepository.findBtEmail 에서 해당 유저의 정보를 찾지 못했을 때. 즉, 신규로 가입할 때
     * @return
     */
    public UserInfo toEntity() {
        return UserInfo.builder()
                .name(name)
                .email(email)
                .role(Role.USER)
                .build();
    }
}
