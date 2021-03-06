package com.tonioostblok.garageapi.controllers;

import com.tonioostblok.garageapi.entities.User;
import com.tonioostblok.garageapi.models.AuthenticationRequest;
import com.tonioostblok.garageapi.models.AuthenticationResponse;
import com.tonioostblok.garageapi.models.Response;
import com.tonioostblok.garageapi.services.UserService;
import com.tonioostblok.garageapi.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtTokenUtil;;

    @RequestMapping(value = "/auth", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception{
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        }catch(Exception e){
            return ResponseEntity.badRequest().body(new Response("The username or password is not correct.", null));
        }
        final User userDetails = userService.loadUserByUsername(authenticationRequest.getUsername());

        final String jwt = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @RequestMapping(value = "/me", method = RequestMethod.GET)
    public ResponseEntity<?> me(HttpServletRequest request){
        try{
            User user = userService.getUser(Integer.parseInt((String) request.getAttribute("user_id")));
            return ResponseEntity.ok(user);
        }catch(Exception e){
            return ResponseEntity.badRequest().body("{\"error\":\"Something went wrong while trying to fetch the user.\"}");
        }
    }

}
