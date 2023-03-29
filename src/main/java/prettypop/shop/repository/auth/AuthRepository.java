package prettypop.shop.repository.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import prettypop.shop.entity.auth.Auth;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth, Long> {

    Optional<Auth> findByRefreshToken(String refreshToken);
    Optional<Auth> findByUsername(String username);
}
