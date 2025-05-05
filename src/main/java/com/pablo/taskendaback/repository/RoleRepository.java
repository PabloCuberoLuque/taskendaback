package com.pablo.taskendaback.repository;

import com.pablo.taskendaback.enums.RoleList;
import com.pablo.taskendaback.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findById(Integer integer);
    Optional<Role> findByName(RoleList name);
}
