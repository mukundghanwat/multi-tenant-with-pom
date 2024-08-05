package com.springware.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springware.entity.RoleEntity;

/**
 * Created by mukund.ghanwat on 01 Aug, 2024
 */

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {
}
