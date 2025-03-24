package com.nami.springbootinit.proxytest2;

public class Payment {
    private Long id;
    private double amount;
    private String currency;
    private String status;

    public Payment() {
    }

    public Payment(Long id, double amount, String currency, String status) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

