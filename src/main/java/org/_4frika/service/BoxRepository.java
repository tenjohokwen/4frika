package org._4frika.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org._4frika.domain.Box;

public interface BoxRepository extends JpaRepository<Box, Long> {

}
