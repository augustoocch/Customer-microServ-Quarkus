package org.augustoocc.repository;

import org.augustoocc.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepoSpring extends JpaRepository<Customer, Long> {
}
