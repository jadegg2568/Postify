package ru.jadegg2568.Postify.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.jadegg2568.Postify.auth.Permissions;
import ru.jadegg2568.Postify.security.UuidUserDetails;

import java.util.UUID;

@Tag(
        name = "Admin Controller V1",
        description = "User permission API"
)
@ApiResponses({
        @ApiResponse(responseCode = "500", description = "Internal server error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
})
@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
public class AdminControllerV1 {
    private final AdminService adminService;

    // PATCH /{uuid}/rights
    @Operation(summary = "Change user permissions", description = "Allows OWNER to promote/demote users")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Successfully updated user permissions"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, authorize"),
            @ApiResponse(responseCode = "403", description = "No permission to change"),
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{uuid}/permissions")
    @PreAuthorize("hasRole('OWNER')")
    public void updateRights(
            @AuthenticationPrincipal UuidUserDetails details,
            @PathVariable UUID uuid,
            @RequestParam Permissions newPermissions) {

        adminService.updatePermissions(details.uuid(), uuid, newPermissions);
    }
}
