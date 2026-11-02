
package com.moviematch.moviematch.auth;

import com.moviematch.moviematch.models.users;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginControllerTest {
    @Test
    void correctLoginSucceeds() {
        LoginUserRepository repository = mock(LoginUserRepository.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        String hash = new BCryptPasswordEncoder().encode("Movie123!");
        users user = new users("mustafa", "Mustafa", hash);
        user.setUid(1);
        when(repository.findByUsername("mustafa"))
                .thenReturn(Optional.of(user));
        when(request.getSession(true)).thenReturn(session);
        LoginController controller = new LoginController(repository);
        String result = controller.login("mustafa", "Movie123!", request);
        assertEquals("redirect:/", result);
        verify(session).setAttribute("userId", 1L);
    }

    @Test
    void wrongPasswordIsRejected() {
        LoginUserRepository repository = mock(LoginUserRepository.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        String hash = new BCryptPasswordEncoder().encode("Movie123!");
        users user = new users("mustafa", "Mustafa", hash);
        when(repository.findByUsername("mustafa"))
                .thenReturn(Optional.of(user));
        LoginController controller = new LoginController(repository);
        String result = controller.login("mustafa", "wrong", request);
        assertEquals("redirect:/login?error", result);
        verify(request, never()).getSession(true);
    }

    @Test
    void unknownUsernameIsRejected() {
        LoginUserRepository repository = mock(LoginUserRepository.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(repository.findByUsername("unknown"))
                .thenReturn(Optional.empty());
        LoginController controller = new LoginController(repository);
        String result = controller.login("unknown", "password", request);
        assertEquals("redirect:/login?error", result);
        verify(request, never()).getSession(true);
    }
}
