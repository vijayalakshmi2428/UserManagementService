package com.user.management.dao;

import java.util.Collection;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.user.management.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

	Set<Role> findByNameIn(Collection<String> roleNames);
}
