package com.moviematch.moviematch.sessionmodels;

import org.springframework.boot.web.server.autoconfigure.ServerProperties.Reactive.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionRepository extends JpaRepository<SessionInformation, Long> {
    List<SessionInformation> findBySessionTitle(String sessionTitle);
}
