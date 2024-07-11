package com.user.management.service;

import java.util.List;

import javax.management.relation.RoleNotFoundException;

import com.user.management.payload.UserDto;

public interface UserService {
	
	UserDto createUser(UserDto userDto) throws RoleNotFoundException;
	
	List<UserDto> getAllUsers();
	
	UserDto getUser(Long userId);
	
	UserDto updateUser(UserDto userDto, Long userId) throws RoleNotFoundException;
	
	void deleteUser(Long userId);

}
