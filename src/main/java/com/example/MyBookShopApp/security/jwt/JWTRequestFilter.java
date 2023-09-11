package com.example.MyBookShopApp.security.jwt;

import com.example.MyBookShopApp.security.model.BookstoreUserDetails;
import com.example.MyBookShopApp.security.service.UserDetailService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JWTRequestFilter extends OncePerRequestFilter {
    private final UserDetailService userDetailsService;
    private final JWTUtil jwtUtil;

    @Autowired
    public JWTRequestFilter(UserDetailService userDetailsService, JWTUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = null;
            String username = null;
            Cookie[] cookies = httpServletRequest.getCookies();

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("token")) {
                        token = cookie.getValue();
                        username = jwtUtil.extractUsername(token);
                    }

                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        BookstoreUserDetails userDetails =
                                (BookstoreUserDetails) userDetailsService.loadUserByUsername(username);
                        if (jwtUtil.validateToken(token, userDetails)) {
                            UsernamePasswordAuthenticationToken authenticationToken =
                                    new UsernamePasswordAuthenticationToken(
                                            userDetails, null, userDetails.getAuthorities());
                            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

                            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        }
                    }
                }
            }
            filterChain.doFilter(httpServletRequest,httpServletResponse);
        } catch (ExpiredJwtException jwtException) {
            removeAuthentication(httpServletRequest, httpServletResponse);
        } catch (Exception ex) {
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void removeAuthentication(HttpServletRequest httpServletRequest,
                                      HttpServletResponse httpServletResponse) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        new SecurityContextLogoutHandler().logout(httpServletRequest, httpServletResponse, authentication);

        Cookie cookie = new Cookie("token", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        httpServletResponse.addCookie(cookie);
        httpServletResponse.sendRedirect("/signin");
    }
}