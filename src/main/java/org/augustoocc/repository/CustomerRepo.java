package org.augustoocc.repository;

import org.augustoocc.domain.Customer;
import org.augustoocc.domain.Product;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class CustomerRepo {

    @Inject
    EntityManager em;

    @Transactional
    public List<Customer> listCustomer() {
        List<Customer> customerstList =  em.createQuery("select c from Customer c").getResultList();
        return customerstList;
    }

    @Transactional
    public Customer getCustomer(Long id) {
        return em.find(Customer.class, id);
    }

    @Transactional
    public void createCustomer(Customer c) {
        em.persist(c);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        Customer c = new Customer();
        c = em.find(Customer.class, id);
        em.remove(c);

    }

    @Transactional
    public void putObject (Customer customer) {
        em.merge(customer);
    }

    @Transactional
    public Product getCustomerProduct() {
        return null;
    }



}
