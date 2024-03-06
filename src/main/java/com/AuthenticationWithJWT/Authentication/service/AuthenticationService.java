package com.AuthenticationWithJWT.Authentication.service;


import com.AuthenticationWithJWT.Authentication.payload.request.AuthenticationRequest;
import com.AuthenticationWithJWT.Authentication.payload.request.RegisterRequest;
import com.AuthenticationWithJWT.Authentication.payload.response.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);
    AuthenticationResponse authenticate(AuthenticationRequest request);
}
