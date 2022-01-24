package ruby.oauth2.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ruby.oauth2.domain.user.entity.UserInfo;

import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    Optional<UserInfo> findByEmail(String email);
}
