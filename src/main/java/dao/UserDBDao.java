package dao;
//This class communicate with DB and set or get data from 'users' table

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.google.gson.Gson;

import models.User;
import util.ConnUtil;

public class UserDBDao {
	
	//Create connection with DB 'testjob' schema
    private Connection connection = ConnUtil.getConnection("testjob");
    
    /**
     * description:
     * Get user based on user name and
     * If the password is equal to password saved in db, return user
     * Return error message if the passwords are not the same
     * 
     * @param username The user's name
     * @param password The user's password
     * @return Convert data to Json and return with this.
     * @throws SQLException
     */
    public String getUser(String username, String password) throws SQLException {
    	User user = new User(0, "","");
    	Gson gson = new Gson();
    	
	    PreparedStatement statement = connection.prepareStatement("SELECT * FROM `users` WHERE username=?");
	   	statement.setString(1, username);
	   	ResultSet resultSet = statement.executeQuery();
	   	if (resultSet.next()) {
	   		Integer userID = resultSet.getInt(1);
	        String userName = resultSet.getString(2);
	        String pass = resultSet.getString(3);
	            
	        user = new User(userID, userName, pass);
	    	}
	   	
    	if (user.getPassword().equals(password)) {
    		return gson.toJson(user);
    	}
    	return gson.toJson(new User(0, "", ""));
    }
    
    public boolean usernameAvailable(String username) throws SQLException {
    	ArrayList<String> usernames = new ArrayList<>();
    	
    	PreparedStatement statement = connection.prepareStatement("SELECT * FROM `users`");
	   	ResultSet resultSet = statement.executeQuery();
	   	while (resultSet.next()) {
	   		String usern = resultSet.getString(2);
	   		usernames.add(usern.toLowerCase());
	   	}
	   	
	   	for (String user : usernames) {
			if (username.toLowerCase().equals(user)){
				return false;
			}
		}
    	return true;
    }
    
    public void addUSer(String username, String password) throws SQLException {
    	PreparedStatement statement = connection.prepareStatement("INSERT INTO `users` VALUES (0, ?, ?)");
		statement.setString(1, username);
		statement.setString(2, password);
		statement.executeUpdate();
    }
    
    public String getUsername(int userID) throws SQLException {
    	PreparedStatement statement = connection.prepareStatement("SELECT * FROM `users` WHERE `user_id`=?");
    	statement.setInt(1, userID);
	   	ResultSet resultSet = statement.executeQuery();
	   	if(resultSet.next()){
	   		return resultSet.getString(2);
	   	}
	   	return "User";
    }

}
