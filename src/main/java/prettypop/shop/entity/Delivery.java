package prettypop.shop.entity;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
public class Delivery extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "DELIVERY_ID")
    private Long id;

    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;

    @NotNull
    private String recipient;

    @NotNull
    private String contact;

    @Embedded
    private Address address;
    private String memo;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;


    public void setOrder(Order order) {
        this.order = order;
    }

    protected Delivery() {
    }

    @Builder
    public Delivery(String recipient, String contact, Address address, String memo) {
        this.recipient = recipient;
        this.contact = contact;
        this.address = address;
        this.memo = memo;
        this.deliveryStatus = DeliveryStatus.STARTED;
    }

    public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
}
