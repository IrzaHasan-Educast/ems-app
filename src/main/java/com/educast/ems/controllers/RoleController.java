// 📁 com.educast.ems.controllers.RoleController.java
package com.educast.ems.controllers;

import com.educast.ems.models.Role;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    @GetMapping
    public List<Role> getAllRoles() {
        return Arrays.asList(Role.values());
    }
}
