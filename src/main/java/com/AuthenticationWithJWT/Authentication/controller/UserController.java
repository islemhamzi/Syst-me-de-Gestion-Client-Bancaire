package com.AuthenticationWithJWT.Authentication.controller;

import com.AuthenticationWithJWT.Authentication.dto.DocumentDto;
import com.AuthenticationWithJWT.Authentication.dto.UserDto;
import com.AuthenticationWithJWT.Authentication.service.DocumentService;
import com.AuthenticationWithJWT.Authentication.service.JwtService;
import com.AuthenticationWithJWT.Authentication.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final DocumentService documentService;

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @GetMapping("/api/v1/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", responses = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }


    @GetMapping("/api/v1/users/verify-token")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('CHEF_AGENCE') or hasRole('TFJO')")
    public ResponseEntity<String> verifyToken(HttpServletRequest request) {
        String token = jwtService.getJwtFromCookies(request);
        if (token != null) {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return ResponseEntity.ok("Roles: " + claims.get("roles"));
        } else {
            return ResponseEntity.badRequest().body("Token is missing or invalid");
        }
    }


    @PutMapping("/api/v1/users/{matriculeLdap}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserRoles(@PathVariable String matriculeLdap, @RequestBody List<String> roles) {
        try {
            userService.updateUserRoles(matriculeLdap, roles);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/api/v1/users/{matriculeLdap}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserStatus(@PathVariable String matriculeLdap, @RequestBody Map<String, String> status) {
        try {
            userService.updateUserStatus(matriculeLdap, status.get("status"));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }




}
