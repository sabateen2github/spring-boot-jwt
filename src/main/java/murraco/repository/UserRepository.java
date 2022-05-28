package murraco.repository;

import murraco.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Integer> {

    boolean existsByUsername(String username);

    Optional<AppUser> findByUsername(String username);

    @Transactional
    void deleteByUsername(String username);

}
