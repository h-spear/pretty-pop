package prettypop.shop.entity;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
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
    private String nickname;

    @Embedded
    private Address address;

    private String phoneNumber;

    private String email;
    private int point;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<WishItem> wishList = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<CartItem> shoppingCart = new ArrayList<>();

    protected Member() {
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changePersonalInfo(String name, Address address, String phoneNumber, String email) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    @Builder
    public Member(String username, String password, String name, String nickname, Address address, String phoneNumber, String email, int point) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.point = point;
    }

    public void addCartItem(Item item, int count) {
        shoppingCart.add(new CartItem(this, item, count));
    }

    public void addWishItem(Item item) {
        wishList.add(new WishItem(this, item));
    }
}
