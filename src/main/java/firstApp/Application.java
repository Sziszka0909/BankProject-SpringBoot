package firstApp;

import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.http.HttpSession;
import javax.xml.ws.Response;

import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import models.User;
import com.google.gson.Gson;

import dao.UserDBDao;

@SpringBootApplication
@RestController
public class Application {
	
	private UserDBDao userDBDao = new UserDBDao();

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);

	}
	
	/*
	@RequestMapping(value="/invalid", method=RequestMethod.GET)
	public String inv(HttpSession session){
		session.invalidate();
		return "megszűnt a session";
	}
	*/
	
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String login(@RequestParam("username") String username, @RequestParam("password") String password, HttpSession session){
		String result;
		try {
			result = userDBDao.getUser(username, password);
			Gson gson = new Gson();
			User user = gson.fromJson(result, User.class);
			
			if(!user.getPassword().equals("")){
				JSONObject jsonObject = new JSONObject(result);
				int id = jsonObject.getInt("userId");
				session.setAttribute("id", id);
				return "ok";
			} else {
				return "error";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "error";
	}
	
	@RequestMapping(value="/register", method=RequestMethod.GET)
	public String register(@RequestParam("username") String username, @RequestParam("password") String password){
		
		try {
			if (userDBDao.usernameAvailable(username) == false) {
				return "Username is not available.";
			} else {
				userDBDao.addUSer(username, password);
				return "ok";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return "Something went wrong.";
	}

}
