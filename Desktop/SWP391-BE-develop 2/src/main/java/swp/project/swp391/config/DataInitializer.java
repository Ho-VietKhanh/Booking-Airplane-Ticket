package swp.project.swp391.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import swp.project.swp391.entity.Permission;
import swp.project.swp391.entity.Role;
import swp.project.swp391.entity.User;
import swp.project.swp391.repository.PermissionRepository;
import swp.project.swp391.repository.RoleRepository;
import swp.project.swp391.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
    public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional
    public void run(String... args) {
        initializeRolesAndPermissions();
    }

    @Transactional
    protected void initializeRolesAndPermissions() {
        // 1) Tạo permissions (idempotent)
        createPermissionsIfNotExist();

        // 2) Tạo roles (idempotent)
        Role adminRole         = upsertRoleIfNeeded("ADMIN", "Administrator", "Full system access");
        Role evmStaffRole      = upsertRoleIfNeeded("EVM_STAFF", "EVM Staff", "Electric Vehicle Manufacturer Staff");
        Role dealerManagerRole = upsertRoleIfNeeded("DEALER_MANAGER", "Dealer Manager", "Dealer Manager");
        Role dealerStaffRole   = upsertRoleIfNeeded("DEALER_STAFF", "Dealer Staff", "Dealer Staff");
        Role userRole          = upsertRoleIfNeeded("USER", "User", "Basic user");

        // 3) Gán permission theo chiến lược *.any
        // Helper: lấy Permission theo tên
        Map<String, Permission> pMap = permissionRepository.findAll().stream()
                .collect(Collectors.toMap(Permission::getName, p -> p));

        // ADMIN: tất cả
            adminRole.setPermissions(new HashSet<>(pMap.values()));
            roleRepository.save(adminRole);

        // DEALER_MANAGER: có thể đọc/sửa mọi customer + đọc report + thao tác order
        setRolePermissionsByNames(dealerManagerRole, pMap, List.of(
                "customer.read.any",
                "customer.update.any",
                "order.create","order.read","order.update",
                "report.read","report.export"
        ));

        // DEALER_STAFF: có thể đọc (any) customer để phục vụ DV, nhưng KHÔNG update mọi customer
        // (tuỳ nghiệp vụ; nếu muốn cho phép sửa mọi customer, thêm "customer.update.any")
        setRolePermissionsByNames(dealerStaffRole, pMap, List.of(
                "customer.read.any",
                "order.create","order.read","order.update"
        ));

        // EVM_STAFF: thao tác vehicle/order/dealer/report, KHÔNG đụng customer.any
        setRolePermissionsByNames(evmStaffRole, pMap, List.of(
                "vehicle.create","vehicle.read","vehicle.update","vehicle.delete",
                "order.create","order.read","order.update","order.delete",
                "dealer.create","dealer.read","dealer.update","dealer.delete",
                "report.read","report.export"
        ));

        // USER: KHÔNG có customer.read.any / customer.update.any
        // user chỉ xem/sửa chính mình thông qua nhánh "owner" trong service
        setRolePermissionsByNames(userRole, pMap, List.of(
                // ví dụ cho user: chỉ quyền với order cá nhân (tùy nghiệp vụ)
                "order.create","order.read","order.update"
        ));

        // 4) Tạo admin mặc định nếu chưa có
        createAdminUserIfNotExist();
    }

    /** Tạo tất cả permission cần thiết (idempotent) */
    private void createPermissionsIfNotExist() {
        // Core CRUD theo resource
        String[][] base = new String[][]{
                {"user","create","Tạo người dùng","Thêm mới người dùng"},
                {"user","read","Xem người dùng","Xem thông tin người dùng"},
                {"user","update","Cập nhật người dùng","Sửa thông tin người dùng"},
                {"user","delete","Xóa người dùng","Xóa người dùng"},
                {"user","inactive","Inactive user","Vô hiệu hóa tài khoản người dùng"},
                {"user","reactivate","Reactivate user","Kích hoạt lại tài khoản người dùng"},

                {"vehicle","create","Tạo xe","Thêm mới xe"},
                {"vehicle","read","Xem xe","Xem thông tin xe"},
                {"vehicle","update","Cập nhật xe","Sửa thông tin xe"},
                {"vehicle","delete","Xóa xe","Xóa xe"},

                {"order","create","Tạo đơn hàng","Thêm mới đơn hàng"},
                {"order","read","Xem đơn hàng","Xem thông tin đơn hàng"},
                {"order","update","Cập nhật đơn hàng","Sửa thông tin đơn hàng"},
                {"order","delete","Xóa đơn hàng","Xóa đơn hàng"},

                {"dealer","create","Tạo đại lý","Thêm mới đại lý"},
                {"dealer","read","Xem đại lý","Xem thông tin đại lý"},
                {"dealer","update","Cập nhật đại lý","Sửa thông tin đại lý"},
                {"dealer","inactive","Vô hiệu hóa đại lý","Vô hiệu hóa đại lý"},
                {"dealer","reactivate","Kích hoạt lại đại lý","Kích hoạt lại đại lý"},
                {"dealer","delete","Xoá đại lý","Xoá đại lý"},

                {"report","read","Xem báo cáo","Xem các báo cáo"},
                {"report","export","Xuất báo cáo","Xuất báo cáo ra file"}
        };

        // CUSTOMER: tách quyền mức any
        String[] customerAny = new String[]{
                "customer.read.any",   // Xem bất kỳ customer
                "customer.update.any", // Sửa bất kỳ customer
                "customer.delete.any"  // (tuỳ nghiệp vụ có dùng hay không)
        };

        // Thêm quyền "role.read.all"
        createPermissionIfNotExist(
                "role.read.all",
                "Xem tất cả các role",
                "Quyền xem tất cả các vai trò trong hệ thống",
                "role",
                "read.all"
        );
        // Thêm quyền "user.read.all"
        createPermissionIfNotExist(
                "user.read.all",
                "Xem tất cả người dùng",
                "Quyền xem tất cả người dùng trong hệ thống",
                "user",
                "read.all"
        );

        // Lưu các quyền khác
        for (String[] p : base) {
            String resource = p[0], action = p[1];
            String display  = p[2], desc = p[3];
            String name     = resource + "." + action;
            createPermissionIfNotExist(name, display, desc, resource, action);
        }

        // Lưu customer.any*
        for (String name : customerAny) {
            createPermissionIfNotExist(
                    name,
                    "Quyền " + name,
                    "Quyền " + name + " trên mọi khách hàng",
                    "customer",
                    name.substring("customer.".length()) // e.g. "read.any"
            );
        }
    }


    private Permission createPermissionIfNotExist(String name, String displayName, String description, String resource, String action) {
        return permissionRepository.findByName(name).orElseGet(() -> {
            Permission permission = Permission.builder()
                    .name(name)
                    .displayName(displayName)
                    .description(description)
                    .resource(resource)
                    .action(action)
                    .isActive(true)
                    .build();
            return permissionRepository.save(permission);
        });
    }

    private Role upsertRoleIfNeeded(String name, String displayName, String description) {
        return roleRepository.findByName(name).orElseGet(() -> {
            Role r = Role.builder()
                    .name(name)
                    .displayName(displayName)
                    .description(description)
                    .isActive(true)
                    .build();
            return roleRepository.save(r);
        });
    }

    private void setRolePermissionsByNames(Role role, Map<String, Permission> all, List<String> names) {
        Set<Permission> perms = names.stream()
                .map(n -> {
                    Permission p = all.get(n);
                    if (p == null) throw new IllegalStateException("Permission not found: " + n);
                    return p;
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
        role.setPermissions(perms);
        roleRepository.save(role);
    }

    private void createAdminUserIfNotExist() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new IllegalStateException("Vai trò ADMIN không được tìm thấy."));
            User adminUser = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .email("admin@example.com")
                    .isVerified(true)
                    .roles(Set.of(adminRole))
                    .isActive(true)
                    .build();
            userRepository.save(adminUser);
            System.out.println("Đã tạo người dùng Admin thành công.");
        }
    }
}
