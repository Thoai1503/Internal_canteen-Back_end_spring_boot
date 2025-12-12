package tinhvomon.com.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import tinhvomon.com.models.User;
import tinhvomon.com.models.UserRespone;
import tinhvomon.com.repository.UserRepo;
import tinhvomon.com.service.JWTSercurity;
import tinhvomon.com.utils.UserToUserResponse;

@RestController
@RequestMapping("/auth")
public class UserController {
	private final UserRepo repo;
	
	@Autowired
	
	private JWTSercurity jwtSercurity;
	
	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	
	@Autowired
	public UserController (UserRepo repo) {
		this.repo =repo;
	}

	@PostMapping("/register")
	public ResponseEntity<?> register (@RequestBody User user) {
		System.out.print("User:"+ user.getEmail());
		
	 var us =	repo.createNewUser(user);
		if(us== null) {
			return ResponseEntity.status(401).body("User already exits");
		}
		return ResponseEntity.ok(us);
	}
	
	
	@PostMapping("/login")
	public ResponseEntity<?> login (@RequestBody User user,Authentication authentication, HttpServletResponse response) {

		System.out.println("Credential: "+user.getEmail()+" "+user.getPassword());

		
	var us= 	repo.findUserByEmail(user.getEmail());
	  System.out.println("Role Controller: "+us.getRole() );
	

	if(!
			encoder.matches(user.getPassword().trim(), us.getPassword().trim())) {
		return ResponseEntity.status(401).body("Invalid password");
	}
	var token = jwtSercurity.generateToken(us.getEmail());
	var role = us.getRole().trim();
	var user_id = us.getId();
         System.out.println("id: "+user_id);
	  Cookie cookie = new Cookie("token", token);
	    cookie.setHttpOnly(true); // không cho JS đọc (an toàn)
	    cookie.setSecure(true);   // chỉ gửi qua HTTPS
	    cookie.setPath("/");      // áp dụng cho toàn bộ app
		  cookie.setDomain("103.90.225.130");
	    cookie.setMaxAge(7 * 24 * 60 * 60); // 7 ngày
	    response.addCookie(cookie);
	    
		  Cookie roleCookie = new Cookie("role", role);
		  roleCookie.setHttpOnly(true); // không cho JS đọc (an toàn)
		  roleCookie.setSecure(true);   // chỉ gửi qua HTTPS
		  roleCookie.setPath("/");      // áp dụng cho toàn bộ app
		  roleCookie.setDomain("103.90.225.130");
		  roleCookie.setMaxAge(7 * 24 * 60 * 60); // 7 ngày
		    response.addCookie(roleCookie);
	
	    Cookie userCookie = new Cookie("user", String.valueOf(us.getId()));
	    userCookie.setHttpOnly(true);
	    userCookie.setSecure(true);
	    userCookie.setPath("/");
	    userCookie.setMaxAge(7 * 24 * 60 * 60);
	    userCookie.setDomain("103.90.225.130");
	    response.addCookie(userCookie);
	    
	
	
		return ResponseEntity.ok(UserToUserResponse.convert(us, token));
	}
	@PostMapping("/verifytoken")
	public ResponseEntity<?>  verifyToken (@RequestBody String token){
		System.out.println("Token: "+token);
		token = token.replace("\"", "");
		var username = jwtSercurity.extractUsername(token);
		if (username == null) {
			return ResponseEntity.status(401).body("Invalid token");
		}
		 User user = repo.findUserByEmail(username);

		 var response = UserToUserResponse.convert(user, token);
		return ResponseEntity.ok(UserToUserResponse.convert(user, token));
	}
	
}
