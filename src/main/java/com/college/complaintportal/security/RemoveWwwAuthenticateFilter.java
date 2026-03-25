package com.college.complaintportal.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

/**
 * Removes WWW-Authenticate from all responses so the browser never shows
 * the "Sign in to access this site" Basic Auth dialog.
 */
public class RemoveWwwAuthenticateFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(request, new HttpServletResponseWrapperNoWwwAuth(response));
    }

    private static class HttpServletResponseWrapperNoWwwAuth extends jakarta.servlet.http.HttpServletResponseWrapper {
        public HttpServletResponseWrapperNoWwwAuth(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void setHeader(String name, String value) {
            if ("WWW-Authenticate".equalsIgnoreCase(name)) return;
            super.setHeader(name, value);
        }

        @Override
        public void addHeader(String name, String value) {
            if ("WWW-Authenticate".equalsIgnoreCase(name)) return;
            super.addHeader(name, value);
        }

        @Override
        public boolean containsHeader(String name) {
            if ("WWW-Authenticate".equalsIgnoreCase(name)) return false;
            return super.containsHeader(name);
        }

        @Override
        public Collection<String> getHeaderNames() {
            return super.getHeaderNames().stream()
                    .filter(n -> !"WWW-Authenticate".equalsIgnoreCase(n))
                    .toList();
        }
    }
}
