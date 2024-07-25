package com.AuthenticationWithJWT.Authentication.controller;

import com.AuthenticationWithJWT.Authentication.handlers.ErrorResponse;
import com.AuthenticationWithJWT.Authentication.payload.request.AuthenticationRequest;
import com.AuthenticationWithJWT.Authentication.payload.request.RefreshTokenRequest;
import com.AuthenticationWithJWT.Authentication.payload.request.RegisterRequest;
import com.AuthenticationWithJWT.Authentication.payload.response.AuthenticationResponse;
import com.AuthenticationWithJWT.Authentication.payload.response.RefreshTokenResponse;
import com.AuthenticationWithJWT.Authentication.service.AuthenticationService;
import com.AuthenticationWithJWT.Authentication.service.JwtService;
import com.AuthenticationWithJWT.Authentication.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationController(AuthenticationService authenticationService, RefreshTokenService refreshTokenService, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.authenticationService = authenticationService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        AuthenticationResponse authenticationResponse = authenticationService.register(request);
        return ResponseEntity.ok().body(authenticationResponse);
    }

    @PostMapping("/authenticate")
    @Operation(
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized",
                            responseCode = "401",
                            content = {@Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")}
                    )
            }
    )
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request) {
        try {
            AuthenticationResponse authenticationResponse = authenticationService.authenticate(request);
            ResponseCookie jwtCookie = jwtService.generateJwtCookie(authenticationResponse.getAccessToken());
            ResponseCookie refreshTokenCookie = refreshTokenService.generateRefreshTokenCookie(authenticationResponse.getRefreshToken());
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                    .body(authenticationResponse);
        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Votre compte est désactivé"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Le matricule ou le mot de passe est incorrect"));
        }
    }

    @PostMapping("/refresh-token")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'CHEF_AGENCE', 'TFJO')")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            RefreshTokenResponse response = refreshTokenService.generateNewToken(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String refreshToken = refreshTokenService.getRefreshTokenFromCookies(request);
        if (refreshToken != null) {
            refreshTokenService.deleteByToken(refreshToken);
        }
        ResponseCookie jwtCookie = jwtService.getCleanJwtCookie();
        ResponseCookie refreshTokenCookie = refreshTokenService.getCleanRefreshTokenCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .build();
    }

    @PostMapping("/refresh-token-cookie")
    public ResponseEntity<Void> refreshTokenCookie(HttpServletRequest request) {
        String refreshToken = refreshTokenService.getRefreshTokenFromCookies(request);
        RefreshTokenResponse refreshTokenResponse = refreshTokenService
                .generateNewToken(new RefreshTokenRequest(refreshToken));
        ResponseCookie newJwtCookie = jwtService.generateJwtCookie(refreshTokenResponse.getAccessToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, newJwtCookie.toString())
                .build();
    }

    @GetMapping("/info")
    public Authentication getAuthentication(@RequestBody AuthenticationRequest request) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getMatricule(), request.getPassword()));
    }
}
