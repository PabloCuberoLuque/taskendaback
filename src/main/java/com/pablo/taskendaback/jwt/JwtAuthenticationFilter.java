package com.pablo.taskendaback.jwt;

import com.pablo.taskendaback.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.WebConnection;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

@NoArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, jakarta.servlet.FilterChain filterChain) throws jakarta.servlet.ServletException, java.io.IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;

       try{
           String jwt = getJWT(request);

           if(jwt != null){
               username = jwtUtil.extractUsername(jwt);
           }

           if (username != null && SecurityContextHolder.getContext().getAuthentication() == null){
               UserDetails userDetails = userService.loadUserByUsername(username);

               if (jwtUtil.validateToken(jwt,userDetails)){
                   UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                   SecurityContextHolder.getContext().setAuthentication(authenticationToken);
               }
           }
       }catch (Exception e){
           System.out.println(e.getMessage());
       }

        filterChain.doFilter(request, response);
    }

    private String getJWT(HttpServletRequest request){
        Cookie cookie = WebUtils.getCookie(request,"jwt");
        return cookie != null ? cookie.getValue() : null;
    }
}
