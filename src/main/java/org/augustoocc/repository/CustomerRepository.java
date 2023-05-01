package org.augustoocc.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import org.augustoocc.domain.Customer;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CustomerRepository implements PanacheRepositoryBase<Customer, Long> {
}
