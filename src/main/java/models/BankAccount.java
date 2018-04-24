package models;

//This class created based on database bank accounts table
//I create setters and getters all parameters

public class BankAccount {
	
	private String accountNumber;
	private String currency;
	private int balance;
	private int userId;
	
	public BankAccount(String accountNumber, String currency, int balance, int userId) {
		super();
		this.accountNumber = accountNumber;
		this.currency = currency;
		this.balance = balance;
		this.userId = userId;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	
	
	

}
