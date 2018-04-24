package dao;
//This class communicate with DB and set or get data from 'bankaccounts' table

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import com.google.gson.Gson;

import models.BankAccount;
import util.ConnUtil;

public class BankAccountDBDao {
	
	//Create connection with DB 'testjob' schema
	private Connection connection = ConnUtil.getConnection("testjob");
	//I will add data to 'accounthistory' table so I create a new AccountHistoryDBDao instance
	private AccountHistoryDBDao accountHIstoryDBDao = new AccountHistoryDBDao();
	
	/**
	 * description:
	 * Create a new bankAccount object and add this to bankAccounts list.
	 * 
	 * @param bankAccounts An ArrayList which store BankAccount object(s).
	 * @param resultSet Help me to executing a statement that queries the database.
	 * @throws SQLException
	 */
	private void addAccountsToArrayList(ArrayList<BankAccount> bankAccounts, ResultSet resultSet) throws SQLException {
		while(resultSet.next()){
			String accountNumber = resultSet.getString(1);
			String currency = resultSet.getString(2);
			int balance = resultSet.getInt(3);
			int userId = resultSet.getInt(4);
			
			bankAccounts.add(new BankAccount(accountNumber, currency, balance, userId));
		}
	}
	
	/**
	 * description:
	 * Get details from 'bankaccount' table based on userId
	 * 
	 * @param id User's id, which I get from session
	 * @return Convert data to Json and return with this.
	 * @throws SQLException
	 */
	public String getBankAccountDetails(int id)throws SQLException{
		ArrayList<BankAccount> bankAccounts = new ArrayList<BankAccount>();
		
		PreparedStatement statement = connection.prepareStatement("SELECT * FROM `bankaccounts` WHERE user_id=?");
	   	statement.setInt(1, id);
	   	ResultSet resultSet = statement.executeQuery();
	   	addAccountsToArrayList(bankAccounts, resultSet);
			
		Gson gson = new Gson();
		return gson.toJson(bankAccounts);
	}
	
	/**
	 * description:
	 * Get all bank account details from 'bankaccounts' table
	 * 
	 * @return Convert data to Json and return with this.
	 * @throws SQLException
	 */
	public String getAllBankAccounts() throws SQLException{
		
		ArrayList<BankAccount> bankAccounts = new ArrayList<BankAccount>();
		
		PreparedStatement statement = connection.prepareStatement("SELECT * FROM `bankaccounts`");
    	ResultSet resultSet = statement.executeQuery();
    	addAccountsToArrayList(bankAccounts, resultSet);

		Gson gson = new Gson();
		return gson.toJson(bankAccounts);
	}
	
	/**
	 * description:
	 * Get bank account currency based on account number from 'bankaccounts' table
	 * 
	 * @param accountNumber The user's account number.
	 * @return Convert data to Json and return with this.
	 * @throws SQLException
	 */
	public String getCurrency(String accountNumber) throws SQLException{
		String currency = null;

			PreparedStatement statement = connection.prepareStatement("SELECT currency FROM `bankaccounts` WHERE account_number=?");
			statement.setString(1, accountNumber);
	    	ResultSet resultSet = statement.executeQuery();
	    	if(resultSet.next()){
	    		currency = resultSet.getString(1);
	    		
	    	}
		
		return currency;
	}

	/**
	 * description:
	 * Get bank account available balance based on account number from 'bankaccounts' table
	 * 
	 * @param accountNumber The user's account number
	 * @return The available balance on user's account
	 * @throws SQLException
	 */
	public int getBalance(String accountNumber) throws SQLException{
		int balance = 0;
		
		PreparedStatement statement = connection.prepareStatement("SELECT balance FROM `bankaccounts` WHERE account_number=?");
		statement.setString(1, accountNumber);
    	ResultSet resultSet = statement.executeQuery();
	    if(resultSet.next()){
	   		balance = resultSet.getInt(1);  		
	   	}
		
		return balance;
	}
	
	/**
	 * description:
	 * 	Create transfer between 2 accounts
	 * Update the source and the target account balance based on amount
	 * Set auto commit to false so the statement isn't automatically committed after executeUodate() method
	 * Commit all statement after I call connection.commit() method
	 * In catch block I call connection.rollback(), so if something went wrong I cancel all the transactions
	 * 
	 * @param sourceAccount Started transfer from this account.
	 * @param targetAccount Transfer to this account.
	 * @param amount The amount that you want to transfer
	 * @throws SQLException
	 */
	public void createTransfer(String sourceAccount, String targetAccount, int amount) throws SQLException{
		
		PreparedStatement deductSource = null;
		PreparedStatement addTarget = null;
		String currency = getCurrency(sourceAccount);
		
		
		try {
			connection.setAutoCommit(false);
			deductSource = connection.prepareStatement("UPDATE `bankaccounts` SET balance = balance-? WHERE account_number=?");
			deductSource.setInt(1, amount);
			deductSource.setString(2, sourceAccount);
			accountHIstoryDBDao.addHistoryDetails(targetAccount, currency, amount, "deduction", sourceAccount);
			deductSource.executeUpdate();
			addTarget = connection.prepareStatement("UPDATE `bankaccounts` SET balance = balance+? WHERE account_number=?");
			addTarget.setInt(1, amount);
			addTarget.setString(2, targetAccount);
			accountHIstoryDBDao.addHistoryDetails(sourceAccount, currency, amount, "crediting", targetAccount);
			addTarget.executeUpdate();
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			connection.rollback();
		} finally {
			if (deductSource != null) {
				deductSource.close();
			}

			if (addTarget != null) {
				addTarget.close();
			}
		}
	}
	
	public boolean availableAccountNumber(String accountNumber) throws SQLException {
		ArrayList<String> accountNumbers = new ArrayList<>();
    	
    	PreparedStatement statement = connection.prepareStatement("SELECT * FROM `bankaccounts`");
	   	ResultSet resultSet = statement.executeQuery();
	   	while (resultSet.next()) {
	   		String accountn = resultSet.getString(1);
	   		accountNumbers.add(accountn);
	   	}
	   	
	   	for (String anumber : accountNumbers) {
			if (accountNumber.equals(anumber)){
				return false;
			}
		}
    	return true;
	}
	
	private String generateAccountNumber() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 17; i++) {
			if(i == 8){
				sb.append("-");
			} else {
				Random random = new Random();
				sb.append(random.nextInt(10));
			}
		}
		
		return sb.toString();
	}
	
	public void addBankAccount(String currency, int userID) throws SQLException {
		String accountNumber = generateAccountNumber();
		while(availableAccountNumber(accountNumber) == false) {
			accountNumber = generateAccountNumber();
		}
		PreparedStatement statement = connection.prepareStatement("INSERT INTO `bankaccounts` VALUES (?, ?, ?, ?)");
		statement.setString(1, accountNumber);
		statement.setString(2, currency);
		statement.setInt(3, 0);
		statement.setInt(4, userID);
		statement.executeUpdate();
	}
	
	public void deleteBankAccount(String bankAccountNumber) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("DELETE FROM `bankaccounts` WHERE `account_number`=?");
		statement.setString(1, bankAccountNumber);
		statement.executeUpdate();
	}
	
	public boolean accountContainsMoney(String bankAccountNumber) throws SQLException {
		int amount = -1;
		
		PreparedStatement statement = connection.prepareStatement("SELECT * FROM `bankaccounts` WHERE `account_number`=?");
		statement.setString(1, bankAccountNumber);
	   	ResultSet resultSet = statement.executeQuery();
	   	if(resultSet.next()){
	   		amount = resultSet.getInt(3);
	   	}
	   	
	   	if(amount == 0){
	   		return false;
	   	}
	   	return true;
	}


}
