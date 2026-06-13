package pl.pb.finansista.request;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.pb.finansista.common.BaseEntity;

import java.math.BigDecimal;

@Entity
@Table(name = "request_funding")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestFunding extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    @Column(nullable = false)
    private String sourceName;

    private BigDecimal amountRequested;
    private BigDecimal amountGranted;

    public RequestFunding(Request request, String sourceName,
                          BigDecimal amountRequested, BigDecimal amountGranted) {
        this.request = request;
        this.sourceName = sourceName;
        this.amountRequested = amountRequested;
        this.amountGranted = amountGranted;
    }
}
