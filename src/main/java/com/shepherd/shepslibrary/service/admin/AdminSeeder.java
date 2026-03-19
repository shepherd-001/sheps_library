package com.shepherd.shepslibrary.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(3)
public class AdminSeeder implements CommandLineRunner {
    private final AdminService  adminService;

    @Override
    public void run(String... args) throws Exception {
        adminService.createAdminIfNotExists();
    }
}
