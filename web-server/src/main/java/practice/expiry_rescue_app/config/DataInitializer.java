package practice.expiry_rescue_app.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import practice.expiry_rescue_app.entity.Staff;
import practice.expiry_rescue_app.entity.enums.StaffRole;
import practice.expiry_rescue_app.repository.StaffRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initializeDefaultAccounts();
    }

    private void initializeDefaultAccounts() {
        // Create default admin account
        if (staffRepository.findByUsernameAndDeletedAtIsNull("admin").isEmpty()) {
            Staff admin = new Staff();
            admin.setUsername("admin");
            admin.setEmail("admin@expiryrescue.com");
            admin.setPasswordHash(passwordEncoder.encode("Admin@123"));
            admin.setRole(StaffRole.ADMIN);
            admin.setFullName("System Administrator");
            admin.setIsActive(true);
            staffRepository.save(admin);
            log.info("Default admin account created: username=admin, password=Admin@123");
        }

        // Create default staff account
        if (staffRepository.findByUsernameAndDeletedAtIsNull("staff").isEmpty()) {
            Staff staff = new Staff();
            staff.setUsername("staff");
            staff.setEmail("staff@expiryrescue.com");
            staff.setPasswordHash(passwordEncoder.encode("Staff@123"));
            staff.setRole(StaffRole.STAFF);
            staff.setFullName("Default Staff");
            staff.setIsActive(true);
            staffRepository.save(staff);
            log.info("Default staff account created: username=staff, password=Staff@123");
        }
    }
}
