package prettypop.shop.entity.auth;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
public class Auth {

    @Id
    @GeneratedValue
    @Column(name = "AUTH_ID", nullable = false)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String refreshToken;

    protected Auth() {
    }

    public Auth(Long userId, String username, String refreshToken) {
        this.userId = userId;
        this.username = username;
        this.refreshToken = refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
