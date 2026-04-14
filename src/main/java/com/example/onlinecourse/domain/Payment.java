package com.example.onlinecourse.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "PAYMENT")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`PaymentId`")
    private Integer id;

    @OneToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "`ReservationId`", nullable = false, unique = true)
    private Enrollment reservation;

    @Column(name = "`Amount`", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "`PaymentDate`", nullable = false)
    private LocalDate paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "`PaymentType`", nullable = false)
    private PaymentType paymentType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Enrollment getReservation() {
        return reservation;
    }

    public void setReservation(Enrollment reservation) {
        this.reservation = reservation;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }
}
