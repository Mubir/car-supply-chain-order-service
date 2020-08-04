package com.mubir.order.domain;


<<<<<<< HEAD
import lombok.Builder;
=======
>>>>>>> c1f9aea2bbf8801c1a887fee4562ab39bc8cbead
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class CarOrder extends BaseEntity {

    @Builder
    public CarOrder(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate, String customerRef,
                    Customer customer, Set<CarOrderLine> carOrderLines, OrderStatusEnum orderStatus,
                    String orderStatusCallbackUrl) {
        super(id, version, createdDate, lastModifiedDate);
        this.customerRef = customerRef;
        this.customer = customer;
        this.carOrderLines = carOrderLines;
        this.orderStatus = orderStatus;
        this.orderStatusCallbackUrl = orderStatusCallbackUrl;
    }

    private String customerRef;

    @ManyToOne
    private Customer customer;
    
    @OneToMany(mappedBy = "carOrder", cascade = CascadeType.ALL)

    @Fetch(FetchMode.JOIN)
    private Set<CarOrderLine> carOrderLines;

    private OrderStatusEnum orderStatus = OrderStatusEnum.NEW;
    private String orderStatusCallbackUrl;

}