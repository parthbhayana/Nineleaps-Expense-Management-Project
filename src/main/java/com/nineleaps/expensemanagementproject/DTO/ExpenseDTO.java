package com.nineleaps.expensemanagementproject.DTO;

import java.time.LocalDate;

public class ExpenseDTO {
	private Float amount;
	private String currency;
	private String description;
	private String merchantName;
	private byte[] supportingDocuments;
	private LocalDate date;

	public ExpenseDTO() {
	}

	public ExpenseDTO(Float amount, String currency, String description, String merchantName,
			byte[] supportingDocuments, LocalDate date) {
		super();
		this.amount = amount;
		this.currency = currency;
		this.description = description;
		this.merchantName = merchantName;
		this.supportingDocuments = supportingDocuments;
		this.date = date;
	}

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public byte[] getSupportingDocuments() {
		return supportingDocuments;
	}

	public void setSupportingDocuments(byte[] supportingDocuments) {
		this.supportingDocuments = supportingDocuments;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

}