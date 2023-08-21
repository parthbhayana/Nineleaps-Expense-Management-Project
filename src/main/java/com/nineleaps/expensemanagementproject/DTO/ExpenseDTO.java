package com.nineleaps.expensemanagementproject.DTO;

import java.time.LocalDate;

public class ExpenseDTO {
    private Long amount;
    private String currency;
    private String description;
    private String merchantName;
    private byte[] supportingDocuments;
    private LocalDate date;

    public ExpenseDTO() {
    }

    public ExpenseDTO(Long amount, String currency, String description, String merchantName, byte[] supportingDocuments,LocalDate date) {
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.merchantName = merchantName;
        this.supportingDocuments = supportingDocuments;
        this.date=date;
    }


    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public void setSupportingDocuments(byte[] supportingDocuments) {
        this.supportingDocuments = supportingDocuments;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public byte[] getSupportingDocuments() {
        return supportingDocuments;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }


}