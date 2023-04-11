package prettypop.shop.entity;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
public class Member extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    private String username;

    private String password;

    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;
    private LocalDate birthDate;

    private String nickname;

    @Embedded
    private Address address;

    private String phoneNumber;

    private String email;
    private int point;

    @ElementCollection
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<WishItem> wishList = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<CartItem> shoppingCart = new ArrayList<>();

    @OneToMany(mappedBy = "reviewer")
    private List<Review> reviews = new ArrayList<>();

    protected Member() {
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changePersonalInfo(String name, Gender gender, LocalDate birthDate, Address address, String phoneNumber, String email) {
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public void increasePoint(int point) {
        this.point += point;
    }

    public void decreasePoint(int point) {
        if (this.point < point) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }
        this.point -= point;
    }

    @Builder
    public Member(String username, String password, String name, Gender gender, LocalDate birthDate, String nickname, Address address, String phoneNumber, String email, int point) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
        this.nickname = nickname;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.point = point;
        this.roles.add("ROLE_USER");
    }

    public void addCartItem(CartItem cartItem) {
        shoppingCart.add(cartItem);
    }

    public void addWishItem(WishItem wishItem) {
        wishList.add(wishItem);
    }

    public void addRole(String role) {
        if (!this.roles.contains(role)) {
            this.roles.add(role);
        }
    }
}
