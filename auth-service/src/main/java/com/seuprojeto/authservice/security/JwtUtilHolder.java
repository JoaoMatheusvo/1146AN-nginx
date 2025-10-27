package com.seuprojeto.authservice.security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
@Component
public class JwtUtilHolder {
    private static JwtUtil instance;
    @Autowired
    private JwtUtil jwtUtil;
    @PostConstruct
    public void init(){ instance = jwtUtil; }
    public static JwtUtil getInstance(){ return instance; }
}
