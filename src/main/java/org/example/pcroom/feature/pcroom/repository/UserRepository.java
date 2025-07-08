package org.example.pcroom.feature.pcroom.repository;

import org.example.pcroom.feature.pcroom.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
