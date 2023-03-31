package prettypop.shop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prettypop.shop.dto.MemberRegisterParam;
import prettypop.shop.entity.CartItem;
import prettypop.shop.entity.Item;
import prettypop.shop.entity.Member;
import prettypop.shop.exception.MemberUsernameDuplicateException;
import prettypop.shop.repository.CartItemRepository;
import prettypop.shop.repository.ItemRepository;
import prettypop.shop.repository.MemberRepository;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberService {

    private static final int BASE_POINT = 3000;

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ItemRepository itemRepository;
    private final CartItemRepository cartItemRepository;

    @Transactional
    public Long join(MemberRegisterParam param) {
        log.info("회원가입 서비스 실행");
        validateDuplicateUsername(param.getUsername());

        if (!param.getPassword().equals(param.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        String encodedPassword = passwordEncoder.encode(param.getPassword());

        Member member = Member.builder()
                .username(param.getUsername())
                .password(encodedPassword)
                .name(param.getName())
                .nickname(generateRandomNickname())
                .gender(param.getGender())
                .birthDate(param.getBirthDate())
                .address(param.getAddress())
                .phoneNumber(param.getPhoneNumber())
                .email(param.getEmail())
                .point(BASE_POINT)
                .build();

        Member savedMember = memberRepository.save(member);
        return savedMember.getId();
    }

    @Transactional
    public void addCart(Long memberId, Long itemId, int count) {
        log.info("카트 아이템 추가 로직 실행");
        Member member = memberRepository.findById(memberId)
                .orElseThrow(IllegalArgumentException::new);
        Item item = itemRepository.findById(itemId).
                orElseThrow(IllegalArgumentException::new);

        Optional<CartItem> optional = cartItemRepository.findByMemberItem(member, item);
        if (optional.isEmpty()) {
            CartItem cartItem = cartItemRepository.save(new CartItem(member, item, count));
            member.addCartItem(cartItem);
        } else {
            CartItem cartItem = optional.get();
            cartItem.addCount(count);
        }
    }

    private void validateDuplicateUsername(String username) {
        memberRepository.findByUsername(username)
                .ifPresent(m -> {
                    throw new MemberUsernameDuplicateException();
                });
    }

    private String generateRandomNickname() {
        return RandomString.make(10);
    }
}
