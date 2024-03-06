package com.AuthenticationWithJWT.Authentication.controller;


import com.AuthenticationWithJWT.Authentication.payload.request.AuthenticationRequest;
import com.AuthenticationWithJWT.Authentication.payload.request.RefreshTokenRequest;
import com.AuthenticationWithJWT.Authentication.payload.request.RegisterRequest;
import com.AuthenticationWithJWT.Authentication.payload.response.AuthenticationResponse;
import com.AuthenticationWithJWT.Authentication.payload.response.RefreshTokenResponse;
import com.AuthenticationWithJWT.Authentication.service.AuthenticationService;
import com.AuthenticationWithJWT.Authentication.service.JwtService;
import com.AuthenticationWithJWT.Authentication.service.RefreshTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(@RequestBody AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getMatricule(), request.getPassword()));
        final AuthenticationResponse authenticationResponse = authenticationService.authenticate(request);
        return ResponseEntity.ok().body(authenticationResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(refreshTokenService.generateNewToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshTokenRequest request) {
        refreshTokenService.deleteByToken(request.getRefreshToken());
        return ResponseEntity.ok().build();
    }
}
