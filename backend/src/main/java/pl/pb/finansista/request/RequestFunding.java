package pl.pb.finansista.request;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;
import pl.pb.finansista.common.BaseEntity;
import pl.pb.finansista.reference.FundingSource;
import pl.pb.finansista.user.User;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * Załącznik 1, sekcja VI: jeden wiersz tabeli źródeł finansowania.
 * Kwotę wnioskowaną wypełnia realizator; kwotę przyznaną wraz z podpisem
 * (kto/kiedy) wypełnia dysponent danego źródła.
 */
@Entity
@Table(name = "request_funding", uniqueConstraints =
        @UniqueConstraint(name = "uq_reqfunding_request_source", columnNames = {"request_id", "funding_source_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestFunding extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funding_source_id", nullable = false)
    private FundingSource source;

    @Column(name = "amount_requested", nullable = false)
    private BigDecimal amountRequested;

    @Column(name = "amount_granted")
    private BigDecimal amountGranted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "granted_by_id")
    private User grantedBy;

    @Column(name = "granted_at")
    private ZonedDateTime grantedAt;

    public RequestFunding(Request request, FundingSource source, BigDecimal amountRequested) {
        Assert.isTrue(amountRequested == null || amountRequested.signum() >= 0,
                "Requested funding amount cannot be negative");
        this.request = request;
        this.source = source;
        this.amountRequested = amountRequested;
    }

    /** The dysponent signs the row: records the granted amount and who/when. */
    public void grant(BigDecimal amountGranted, User dysponent) {
        this.amountGranted = amountGranted;
        this.grantedBy = dysponent;
        this.grantedAt = ZonedDateTime.now();
    }

    public boolean isGranted() {
        return grantedBy != null;
    }
}
