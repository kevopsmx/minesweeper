package com.deviget.minesweeper.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.deviget.minesweeper.model.Response;
import com.deviget.minesweeper.model.User;
import com.deviget.minesweeper.repository.UserRepository;
@RestController
public class UserController {

	@Autowired
	UserRepository userRepository;
	
	@PostMapping("/createUser")
	public Response createUser(@RequestBody User user) {
		if(user!=null) {
			userRepository.save(user);
			Optional<User> found =  userRepository.findById(user.getUserId());
			if(found.isPresent()) {
				return new Response("User created correctly",200);
			}
			return new Response("Error creating",500);
		} else {
			return new Response("Invalid params",500);
		}
	}
}
