package com.moviematch.moviematch.auth;
import com.moviematch.moviematch.models.users;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LoginUserRepository 
    extends JpaRepository<users, Integer> {
    Optional<users> findByUsername(String username);
}
