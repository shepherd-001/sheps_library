package com.shepherd.shepslibrary.service.userRoleAndPermission;

import com.shepherd.shepslibrary.common.exceptions.AuthorizationConflictException;
import com.shepherd.shepslibrary.common.request.PaginationRequest;
import com.shepherd.shepslibrary.data.dto.request.*;
import com.shepherd.shepslibrary.data.dto.response.RoleResponse;
import com.shepherd.shepslibrary.data.model.*;
import com.shepherd.shepslibrary.data.model.enums.TokenType;
import com.shepherd.shepslibrary.data.repository.*;
import com.shepherd.shepslibrary.security.*;
import com.shepherd.shepslibrary.service.userRoleAndPermission.permission.*;
import com.shepherd.shepslibrary.service.userRoleAndPermission.role.*;
import jakarta.servlet.Filter;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.*;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class AuthorizationIntegrationTest {
    @Autowired RoleService roles;
    @Autowired PermissionService permissions;
    @Autowired UserRoleRepository roleRepository;
    @Autowired PermissionRepository permissionRepository;
    @Autowired UserRepository userRepository;
    @Autowired TokenRepository tokenRepository;
    @Autowired JdbcTemplate jdbc;
    @Autowired JwtUtils jwt;
    @Autowired WebApplicationContext context;
    Map<String, Permission> catalogue;
    User admin;
    MockMvc mvc;

    @BeforeEach void setup() {
        SecurityContextHolder.clearContext();
        tokenRepository.deleteAll();
        userRepository.deleteAll();
        jdbc.update("DELETE FROM authorization_audit");
        roleRepository.deleteAll();
        permissionRepository.deleteAll();
        catalogue = permissionRepository.saveAll(PermissionCatalog.codes().stream()
                .map(code -> Permission.builder().name(code).build()).toList()).stream()
                .collect(Collectors.toMap(Permission::getName, permission -> permission));
        SystemRolePolicy.defaults().forEach((name, codes) -> roleRepository.saveAndFlush(UserRole.builder()
                .name(name).permissions(entities(codes)).build()));
        admin = account("administrator", "ADMIN");
        authenticate(admin);
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters((Filter) context.getBean("springSecurityFilterChain")).build();
    }

    @AfterEach void cleanup() { SecurityContextHolder.clearContext(); }

    @Test void createNormalizesAndAuditsWithoutGrantingPermissions() {
        RoleResponse role = roles.createRole(new AddRoleRequest("support", "Create support role"));
        assertEquals("SUPPORT", role.name());
        assertTrue(role.permissionNames().isEmpty());
        assertFalse(role.systemRole());
        assertEquals(0, role.version());
        assertEquals(admin.getId(), jdbc.queryForObject("SELECT actor_id FROM authorization_audit", UUID.class));
        assertEquals("ROLE_CREATED", jdbc.queryForObject("SELECT action FROM authorization_audit", String.class));
    }

    @Test void replaceIncrementsVersionAndRecordsBeforeAndAfter() {
        RoleResponse created = roles.createRole(new AddRoleRequest("SUPPORT", "Initial setup"));
        RoleResponse updated = roles.replaceRolePermissions(change("SUPPORT", List.of("book:read"), created.version()));
        assertEquals(created.version() + 1, updated.version());
        assertEquals(Set.of("book:read"), updated.permissionNames());
        assertEquals("book:read", jdbc.queryForObject(
                "SELECT after_permissions FROM authorization_audit WHERE action = 'ROLE_PERMISSIONS_REPLACED'", String.class));
        assertEquals(2, auditCount());
        assertThrows(AuthorizationConflictException.class, () -> roles.replaceRolePermissions(
                change("SUPPORT", List.of("book:update"), created.version())));
        assertEquals(2, auditCount());
    }

    @Test void emptyReplacementIsAnExplicitAuditedRevocation() {
        customRole("SUPPORT", Set.of("book:read"));
        RoleResponse updated = roles.replaceRolePermissions(change("SUPPORT", List.of(), 0));
        assertTrue(updated.permissionNames().isEmpty());
        assertEquals(1, updated.version());
        assertEquals("book:read", jdbc.queryForObject("SELECT before_permissions FROM authorization_audit", String.class));
        assertEquals("", jdbc.queryForObject("SELECT after_permissions FROM authorization_audit", String.class));
    }

    @Test void systemRolesCannotBeCreatedChangedOrDeleted() {
        assertThrows(AccessDeniedException.class, () -> roles.createRole(new AddRoleRequest("ADMIN", "Try recreate")));
        assertThrows(AccessDeniedException.class, () -> roles.replaceRolePermissions(change("MEMBER", List.of(), 0)));
        assertThrows(AccessDeniedException.class, () -> roles.deleteRole("LIBRARIAN", 0L, "Try deletion"));
        assertEquals(0, auditCount());
    }

    @Test void ownRoleAndPrivilegeEscalationAreRejected() {
        customRole("OPERATOR", Set.of("role:permissions:update", "book:read"));
        customRole("SUPPORT", Set.of());
        authenticate(account("operator", "OPERATOR"));
        assertThrows(AccessDeniedException.class, () -> roles.replaceRolePermissions(change("OPERATOR", List.of(), 0)));
        assertThrows(AccessDeniedException.class, () -> roles.replaceRolePermissions(change("SUPPORT", List.of("book:delete"), 0)));
        assertThrows(AccessDeniedException.class, () -> roles.replaceRolePermissions(change("SUPPORT", List.of("role:permissions:update"), 0)));
        assertEquals(0, auditCount());
    }

    @Test void cannotStripPermissionsFromARoleAboveDelegationBoundary() {
        customRole("OPERATOR", Set.of("role:permissions:update", "book:read"));
        customRole("PRIVILEGED", Set.of("book:delete"));
        authenticate(account("operator", "OPERATOR"));
        assertThrows(AccessDeniedException.class, () -> roles.replaceRolePermissions(change("PRIVILEGED", List.of(), 0)));
    }

    @Test void serviceRejectsMissingAuthorityEvenWithoutAController() {
        authenticate(account("member", "MEMBER"));
        assertThrows(AccessDeniedException.class, () -> roles.createRole(new AddRoleRequest("SUPPORT", "Not permitted")));
        SecurityContextHolder.clearContext();
        assertThrows(AccessDeniedException.class, () -> permissions.catalogue());
    }

    @Test void validationRejectsNullUnknownDuplicateAndOversizedInput() {
        customRole("SUPPORT", Set.of());
        assertThrows(ConstraintViolationException.class, () -> roles.replaceRolePermissions(
                new AssignPermissionRequest("SUPPORT", null, 0L, "Missing list")));
        assertThrows(IllegalArgumentException.class, () -> roles.replaceRolePermissions(change("SUPPORT", List.of("made:up"), 0)));
        assertThrows(IllegalArgumentException.class, () -> roles.replaceRolePermissions(change("SUPPORT", List.of("book:read", "BOOK:READ"), 0)));
        assertThrows(ConstraintViolationException.class, () -> roles.createRole(new AddRoleRequest("A".repeat(51), "Too long")));
        assertThrows(ConstraintViolationException.class, () -> roles.createRole(new AddRoleRequest("SUPPORT2", " ")));
        assertEquals(0, auditCount());
    }

    @Test void deletingAssignedRoleConflictsButUnassignedRoleLeavesAuditHistory() {
        customRole("SUPPORT", Set.of("book:read"));
        account("support", "SUPPORT");
        assertThrows(AuthorizationConflictException.class, () -> roles.deleteRole("SUPPORT", 0L, "Still assigned"));
        customRole("UNUSED", Set.of("book:read"));
        roles.deleteRole("UNUSED", 0L, "Remove unused role");
        assertFalse(roleRepository.existsByNameEqualsIgnoreCase("UNUSED"));
        assertEquals("ROLE_DELETED", jdbc.queryForObject("SELECT action FROM authorization_audit", String.class));
    }

    @Test void auditFailureRollsBackPermissionAndVersionChanges() {
        customRole("SUPPORT", Set.of("book:read"));
        jdbc.execute("ALTER TABLE authorization_audit ADD CONSTRAINT reject_test_reason CHECK (reason <> 'FAIL_AUDIT')");
        try {
            assertThrows(DataIntegrityViolationException.class, () -> roles.replaceRolePermissions(
                    new AssignPermissionRequest("SUPPORT", List.of("book:update"), 0L, "FAIL_AUDIT")));
            RoleResponse unchanged = roles.getRoleDetails("SUPPORT");
            assertEquals(0, unchanged.version());
            assertEquals(Set.of("book:read"), unchanged.permissionNames());
            assertEquals(0, auditCount());
        } finally {
            jdbc.execute("ALTER TABLE authorization_audit DROP CONSTRAINT reject_test_reason");
        }
    }

    @Test void competingUpdatesCannotBothCommit() throws Exception {
        customRole("SUPPORT", Set.of());
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch start = new CountDownLatch(1);
        try {
            List<Future<Boolean>> results = new ArrayList<>();
            for (String code : List.of("book:read", "book:update")) {
                results.add(executor.submit(() -> {
                    authenticate(admin);
                    try {
                        assertTrue(start.await(10, TimeUnit.SECONDS));
                        roles.replaceRolePermissions(change("SUPPORT", List.of(code), 0));
                        return true;
                    } catch (AuthorizationConflictException | ConcurrencyFailureException conflict) {
                        return false;
                    } finally { SecurityContextHolder.clearContext(); }
                }));
            }
            start.countDown();
            int successes = 0;
            for (Future<Boolean> result : results) if (result.get(20, TimeUnit.SECONDS)) successes++;
            assertEquals(1, successes);
            assertEquals(1, roles.getRoleDetails("SUPPORT").version());
            assertEquals(1, auditCount());
        } finally { executor.shutdownNow(); }
    }

    @Test void catalogueAndPaginatedResponsesAreBoundedAndImmutable() {
        assertEquals(PermissionCatalog.codes().size(), permissions.catalogue().size());
        var page = roles.listRoles(PaginationRequest.of(1, 2, null, null));
        assertEquals(2, page.items().size());
        assertTrue(page.hasNext());
        assertThrows(UnsupportedOperationException.class, () -> page.items().getFirst().permissionNames().clear());
    }

    @Test void startupVerifierChecksButNeverResetsAdministratorManagedRoles() {
        customRole("SUPPORT", Set.of("book:read"));
        AuthorizationCatalogueVerifier verifier = new AuthorizationCatalogueVerifier(permissionRepository, roleRepository);
        verifier.run(null);
        assertEquals(Set.of("book:read"), roles.getRoleDetails("SUPPORT").permissionNames());
        UserRole member = roleRepository.findByNameEqualsIgnoreCase("MEMBER").orElseThrow();
        member.setPermissions(new HashSet<>());
        roleRepository.saveAndFlush(member);
        assertThrows(IllegalStateException.class, () -> verifier.run(null));
        assertTrue(roleRepository.findByNameEqualsIgnoreCase("MEMBER").orElseThrow().getPermissions().isEmpty());
    }

    @Test void legacyUnknownPermissionsDoNotBecomeAuthorities() {
        UserRole custom = customRole("SUPPORT", Set.of());
        Permission old = permissionRepository.saveAndFlush(Permission.builder().name("admin.create").build());
        custom.setPermissions(new HashSet<>(Set.of(old)));
        roleRepository.saveAndFlush(custom);
        AuthenticatedUser user = AuthenticatedUser.builder().user(account("support", "SUPPORT")).build();
        assertFalse(user.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("admin.create")));
    }

    @Test void httpPermissionsValidationVersionConflictsAndRevocation() throws Exception {
        String token = bearer(admin);
        SecurityContextHolder.clearContext();
        mvc.perform(get("/api/v1/admin/permissions")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/v1/admin/roles").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"SUPPORT\",\"reason\":\"Helpdesk setup\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data.version").value(0));
        mvc.perform(put("/api/v1/admin/roles/permissions").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content("{\"roleName\":\"SUPPORT\",\"permissionNames\":[\"book:read\"]}"))
                .andExpect(status().isBadRequest());
        mvc.perform(put("/api/v1/admin/roles/permissions").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content("{\"roleName\":\"SUPPORT\",\"permissionNames\":[\"book:read\"],\"expectedVersion\":0,\"reason\":\"Read catalogue\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.version").value(1));
        mvc.perform(delete("/api/v1/admin/delete-role").header("Authorization", "Bearer " + token)
                .param("roleName", "SUPPORT").param("expectedVersion", "0").param("reason", "Stale deletion"))
                .andExpect(status().isConflict());
        User support = account("support", "SUPPORT");
        String supportToken = bearer(support);
        SecurityContextHolder.clearContext();
        mvc.perform(get("/api/v1/books").header("Authorization", "Bearer " + supportToken)).andExpect(status().isOk());
        authenticate(admin);
        roles.replaceRolePermissions(change("SUPPORT", List.of(), 1));
        SecurityContextHolder.clearContext();
        // The same JWT is checked against newly loaded role permissions on the very next request.
        mvc.perform(get("/api/v1/books").header("Authorization", "Bearer " + supportToken)).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/admin/permissions").header("Authorization", "Bearer " + supportToken)).andExpect(status().isForbidden());
    }

    private AssignPermissionRequest change(String name, List<String> codes, long version) {
        return new AssignPermissionRequest(name, codes, version, "Reviewed permission change");
    }
    private Set<Permission> entities(Set<String> codes) {
        return codes.stream().map(catalogue::get).collect(Collectors.toCollection(HashSet::new));
    }
    private UserRole customRole(String name, Set<String> codes) {
        return roleRepository.saveAndFlush(UserRole.builder().name(name).permissions(entities(codes)).build());
    }
    private User account(String name, String role) {
        return userRepository.saveAndFlush(User.builder().email(name + "@example.com").enabled(true).emailVerified(true)
                .role(roleRepository.findByNameEqualsIgnoreCase(role).orElseThrow()).build());
    }
    private void authenticate(User user) {
        AuthenticatedUser principal = AuthenticatedUser.builder().user(user).build();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities()));
    }
    private String bearer(User user) {
        String token = jwt.generateAccessToken(user, 900);
        tokenRepository.saveAndFlush(ShepsToken.builder().user(user).token(token).tokenType(TokenType.JWT).build());
        return token;
    }
    private int auditCount() { return jdbc.queryForObject("SELECT COUNT(*) FROM authorization_audit", Integer.class); }
}
