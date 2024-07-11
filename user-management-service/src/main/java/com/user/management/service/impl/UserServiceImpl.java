package com.user.management.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.management.relation.RoleNotFoundException;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.user.management.dao.RoleRepository;
import com.user.management.dao.UserRepository;
import com.user.management.entity.Role;
import com.user.management.entity.User;
import com.user.management.enums.RoleType;
import com.user.management.exception.UserNotFoundException;
import com.user.management.payload.UserDto;
import com.user.management.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	
	Logger logger= LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public UserDto createUser(UserDto userDto) throws RoleNotFoundException {

		User user = mapToEntity(userDto);
		
		List<String> roleTypes = userDto.getRoles().stream().map(roleType -> roleType.name()).collect(Collectors.toList());
		
		Set<Role> userRoles = roleRepository.findByNameIn(roleTypes);
		
		if(userRoles == null)
			throw new RoleNotFoundException("Role not found");
		
		user.setPassword(passwordEncoder.encode(userDto.getPassword()));
		user.setRoles(userRoles);
	    
	    User savedUser = userRepository.save(user);

	    logger.info("new user saved successfully");
		
		// convert entity to DTO
		UserDto userResponse = mapToDto(savedUser);

		return userResponse;
	}

	@Override
	public List<UserDto> getAllUsers() {

		List<User> allUsers = userRepository.findAll();
		
		logger.info("fetched all users from db");
		
		return allUsers.stream().map(user -> mapToDto(user)).collect(Collectors.toList());
	}

	@Override
	public UserDto getUser(Long userId) {

		User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User does not found "));
		
		logger.info(userId+" User fetched successfully");
		
		return mapToDto(user);
	}

	@Override
	public UserDto updateUser(UserDto userDto, Long userId) throws RoleNotFoundException {

		User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User does not found "));	
		
		user.setEmail(userDto.getEmail());
		user.setName(userDto.getName());
		user.setUsername(userDto.getUsername());
		user.setPassword(passwordEncoder.encode(userDto.getPassword()));
		
		List<String> roleTypes = userDto.getRoles().stream().map(roleType -> roleType.name()).collect(Collectors.toList());
		
		Set<Role> userRoles = roleRepository.findByNameIn(roleTypes);
		
		if(userRoles == null)
			throw new RoleNotFoundException("Role not found");
		
		user.setRoles(userRoles);
		
		User updatedUser = userRepository.save(user);
		logger.info(userId+" User updated successfully");
		return mapToDto(updatedUser);
	}

	@Override
	public void deleteUser(Long userId) {
		

		User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User does not found "));
		
		logger.info(userId+" user deleted successfully");
		
		userRepository.delete(user);
		
	}
	
	

	public User mapToEntity(UserDto userDto) {
		
		User user = modelMapper.map(userDto, User.class);

			return user;

	}
		
	public UserDto mapToDto(User user) {

		UserDto userDto = modelMapper.map(user, UserDto.class);

		Set<RoleType> roleTypes = user.getRoles().stream().map(role -> RoleType.valueOf(role.getName())).collect(Collectors.toSet());
		userDto.setRoles(roleTypes);
		return userDto;
	}

}
