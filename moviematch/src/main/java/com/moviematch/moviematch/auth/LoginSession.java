
package com.moviematch.moviematch.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class LoginSession {

    private static final String USER_ID = "userId";
    private static final String USER_ROLE = "userRole";
    public static void login(HttpServletRequest request,
                             long userId, String role) {
        HttpSession session = request.getSession(true);
        request.changeSessionId();
        session.setAttribute(USER_ID, userId);
        session.setAttribute(USER_ROLE, role);
    }
    public static boolean isLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        return session != null &&
               session.getAttribute(USER_ID) != null;
    }
    public static void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
