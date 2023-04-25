package org.augustoocc.repository;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;
import org.augustoocc.domain.Customer;
import org.augustoocc.exceptions.NotWritableEx;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import java.time.Duration;
import java.util.List;

import static javax.ws.rs.core.Response.Status.*;

@Slf4j
@ApplicationScoped
public class CustomerReactive implements PanacheRepository<Customer> {

    @Inject
    NotWritableEx exception;

    @ConsumeEvent("add-customer")
    public Uni<Customer> addCustomer(Customer c) throws NotWritableEx{
        log.info("Adding customer with id: ", c.getNames());
        return Panache.withTransaction(c::persist)
                .replaceWith(c)
                .onFailure().invoke(i -> new NotWritableEx("Not possible to add the customer due to error"));
    }

    @ConsumeEvent("delete-customer")
    public void deleteCustomer(Long id) {
        log.info("Deleting object with id: ", id);
        deleteById(id)
                .ifNoItem().after(Duration.ofMillis(500))
                .failWith(()-> new NotFoundException("Customer not found"));
    }

    @ConsumeEvent("update-customer")
    public Uni<Response> updateCustomer(Customer customer) {
        if(customer == null || customer.getCode() == null) {
            throw new WebApplicationException("Product code was not set on the request", HttpResponseStatus.UNPROCESSABLE_ENTITY.code());
        }
        log.info("Merging object with id: ", customer.id);
        return findById(customer.id)
                    .onItem().ifNotNull().invoke(entity -> {
                        entity.setNames(customer.getNames());
                        entity.setAccountNumber(customer.getAccountNumber());
                        entity.setCode(customer.getCode());
                })
                    .onItem().ifNotNull().transform(entity -> Response.ok().status(ACCEPTED).build())
                    .onFailure().invoke(i -> Response.ok(NOT_FOUND).build());
    }


    @ConsumeEvent("get-by-id")
    public Uni<Customer> getById(Long id) {
        log.info("Request received - getting customer");
        return findById(id).onItem()
                        .ifNotNull()
                        .transform(i -> Response.ok().status(ACCEPTED).build().readEntity(Customer.class))
                        .onFailure().invoke(i -> Response.ok(NOT_FOUND).build());
    }





}
