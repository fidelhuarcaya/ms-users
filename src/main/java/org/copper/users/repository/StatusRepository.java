package org.copper.users.repository;

import org.copper.users.common.StatusCode;
import org.copper.users.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatusRepository extends JpaRepository<Status, Integer> {
    Optional<Status> findByCode(StatusCode statusCode);

}