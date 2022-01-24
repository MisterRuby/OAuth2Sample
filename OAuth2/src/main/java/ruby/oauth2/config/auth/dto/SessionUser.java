package ruby.oauth2.config.auth.dto;

import lombok.Getter;
import ruby.oauth2.domain.user.entity.UserInfo;

import java.io.Serializable;

/**
 * 인증된 사용자 정보
 */
@Getter
public class SessionUser implements Serializable {
    private String name;
    private String email;

    public SessionUser(UserInfo userInfo) {
        this.name = userInfo.getName();
        this.email = userInfo.getEmail();
    }
}
