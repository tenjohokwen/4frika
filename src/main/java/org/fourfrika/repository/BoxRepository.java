package org.fourfrika.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.fourfrika.domain.Box;

public interface BoxRepository extends JpaRepository<Box, Long> {

}
