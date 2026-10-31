
package com.moviematch.moviematch.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginRequiredInterceptorTest {
    @Test
    void loggedInUserCanAccessPage() throws Exception {

        HttpServletRequest request =
                mock(HttpServletRequest.class);
        HttpServletResponse response =
                mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1L);
        LoginRequiredInterceptor interceptor =
                new LoginRequiredInterceptor();
        boolean result =
                interceptor.preHandle(request, response, new Object());
        assertTrue(result);
        verify(response, never()).sendRedirect(anyString());
    }
    @Test
    void loggedOutUserIsRedirected() throws Exception {
        HttpServletRequest request =
                mock(HttpServletRequest.class);
        HttpServletResponse response =
                mock(HttpServletResponse.class);
        when(request.getSession(false)).thenReturn(null);
        when(request.getContextPath()).thenReturn("");
        LoginRequiredInterceptor interceptor =
                new LoginRequiredInterceptor();
        boolean result =
                interceptor.preHandle(request, response, new Object());
        assertFalse(result);
        verify(response).sendRedirect("/login");
    }
}
