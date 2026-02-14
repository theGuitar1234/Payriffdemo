package org.payriff.springboot.seeddata;

import java.math.BigDecimal;
import java.time.Instant;

import org.payriff.springboot.entities.Money;
import org.payriff.springboot.entities.Role;
import org.payriff.springboot.entities.User;
import org.payriff.springboot.repositories.RoleRepository;
import org.payriff.springboot.repositories.UserRepository;
import org.payriff.springboot.utilities.constants.currency;
import org.payriff.springboot.utilities.constants.roles;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class SeedData implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public SeedData(
        RoleRepository roleRepository,
        UserRepository userRepository
    ) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        
        Role role;
        for (roles r : roles.values()) {
            role = new Role();
            role.setRoleId(r.getRoleId());
            role.setRoleNameString(r.getRoleNameString());
            
            roleRepository.saveAndFlush(role);
        }

        User user = new User();
        user.setBillingEnabled(true);
        user.setNextDate(Instant.now());
        user.setPayment(new Money(BigDecimal.valueOf(200.45), currency.AZN));

        role = roleRepository.findById(roles.TEACHER.getRoleId())
            .orElseThrow(() -> new RuntimeException("Role not found by ID"));
        user.addRole(role);

        userRepository.saveAndFlush(user);        
    }
    
}
