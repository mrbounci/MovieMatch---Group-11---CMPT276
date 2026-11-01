
package com.moviematch.moviematch.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginSessionTest {
    @Test
    void userNotLoggedInWithoutSession() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getSession(false)).thenReturn(null);
        assertFalse(LoginSession.isLoggedIn(request));
    }

    @Test
    void loginStoresUserInformation() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getSession(true)).thenReturn(session);
        LoginSession.login(request, 1L, "USER");
        verify(request).changeSessionId();
        verify(session).setAttribute("userId", 1L);
        verify(session).setAttribute("userRole", "USER");
    }
    @Test
    void logoutInvalidatesSession() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        LoginSession.logout(request);
       verify(session).invalidate();
    }
}
