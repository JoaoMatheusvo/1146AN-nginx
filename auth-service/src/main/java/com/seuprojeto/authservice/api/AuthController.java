package com.seuprojeto.authservice.api;
import com.seuprojeto.authservice.domain.Role;
import com.seuprojeto.authservice.domain.User;
import com.seuprojeto.authservice.repository.UserRepository;
import com.seuprojeto.authservice.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }
    public static record SignupRequest(String username, String password, Set<String> roles) {}
    public static record LoginRequest(String username, String password) {}
    public static record TokenResponse(String token) {}
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest req) {
        if (userRepository.findByUsername(req.username()).isPresent()) {
            return ResponseEntity.badRequest().body("username already exists");
        }
        Set<Role> granted = Optional.ofNullable(req.roles()).orElse(Set.of("ROLE_CUSTOMER"))
                .stream().map(String::toUpperCase).map(r->{
                    try{return Role.valueOf(r);}catch(Exception e){return null;}
                }).filter(Objects::nonNull).collect(Collectors.toSet());
        if (granted.isEmpty()) granted = Set.of(Role.ROLE_CUSTOMER);
        User u = new User(req.username(), passwordEncoder.encode(req.password()), granted);
        userRepository.save(u);
        return ResponseEntity.ok("user created");
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        var opt = userRepository.findByUsername(req.username());
        if (opt.isEmpty()) return ResponseEntity.status(401).body("invalid credentials");
        var user = opt.get();
        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            return ResponseEntity.status(401).body("invalid credentials");
        }
        var roles = user.getRoles().stream().map(Enum::name).collect(Collectors.toList());
        String token = jwtUtil.generateToken(user.getUsername(), roles);
        return ResponseEntity.ok(new TokenResponse(token));
    }
}
