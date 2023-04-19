package org.augustoocc.repository;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;
import org.augustoocc.domain.Customer;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.*;

@Slf4j
public class CustomerReactive implements PanacheRepository<Customer> {


    @ConsumeEvent("add-customer")
    private Uni<Response> addCustomer(Customer c) {
        log.info("Deleting object with id: ", c.id);
        return persist(c).onItem().transform(i-> Response.ok().status(CREATED).build());
    }

    @ConsumeEvent("delete-customer")
    private Uni<Response> deleteCustomer(Long id) {
        log.info("Deleting object with id: ", id);
        return delete("id", id)
                .onFailure()
                .invoke(i -> Response.ok().status(NOT_FOUND).build())
                .onItem()
                .transform(i -> Response.ok().status(ACCEPTED).build());
    }

    @ConsumeEvent("update-customer")
    private Uni<Response> updateCustomer(Customer customer) {
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
    private Uni<Customer> getById(Long id) {
        log.info("Request received - getting customer");
        return findById(id).onItem()
                        .ifNotNull()
                        .transform(i -> Response.ok(findById(id)).status(ACCEPTED).build().readEntity(Customer.class))
                        .onFailure().invoke(i -> Response.ok(NOT_FOUND).build());
    }





}
