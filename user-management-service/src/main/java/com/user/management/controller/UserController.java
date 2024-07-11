package com.user.management.controller;

import java.util.List;

import javax.management.relation.RoleNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.management.payload.UserDto;
import com.user.management.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

Logger logger= LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserService userService;
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/createUser")
	public ResponseEntity<UserDto> createUser(@RequestBody UserDto UserDto) throws RoleNotFoundException {
		logger.info("saving user into db");
		return new ResponseEntity<>(userService.createUser(UserDto), HttpStatus.CREATED);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/getAllUsers")
	public ResponseEntity<List<UserDto>> getAllUsers() {
		logger.info("finding AllUsers ");
		return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/findUser/{userId}")
	public ResponseEntity<UserDto> getUserById(@PathVariable(name = "userId") long userId) {
		logger.info("finding user by id "+userId);
		return ResponseEntity.ok(userService.getUser(userId));
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping("/updateUser/{userId}")
	public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto, 
			@PathVariable(name="userId") long userId) throws RoleNotFoundException {
		logger.info("updating "+userId+" user ");
		UserDto userResponse = userService.updateUser(userDto, userId);
		return new ResponseEntity<>(userResponse, HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping("/deleteUser/{userId}")
	public ResponseEntity<String> deleteUser(@PathVariable(name = "userId")long userId) {
		
		logger.info("deleting user by id "+userId);
		userService.deleteUser(userId);
		return new ResponseEntity<>("User '"+userId+"' deleted successfully.", HttpStatus.OK);
	}
}
