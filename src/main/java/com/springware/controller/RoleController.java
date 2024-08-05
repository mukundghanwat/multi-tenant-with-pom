package com.springware.controller;

import com.springware.config.sqlConfig.TenantContext;
import com.springware.entity.RoleEntity;
import com.springware.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by mukund.ghanwat on 01 Aug, 2024
 */
@RequestMapping("/role")
@RestController
public class RoleController {
    @Autowired
    RoleRepository roleRepository;

    @GetMapping(value = "/{tenant}")
    ResponseEntity<Object> fetchRole(@PathVariable("tenant") String tenant) {
        TenantContext.setCurrentTenant(tenant);
        List<RoleEntity> roleList = roleRepository.findAll();
        return new ResponseEntity<Object>(roleList, HttpStatus.OK);

    }
}
