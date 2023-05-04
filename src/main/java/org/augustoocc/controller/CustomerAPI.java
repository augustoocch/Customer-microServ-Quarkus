package org.augustoocc.controller;

import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.augustoocc.domain.Customer;
import org.augustoocc.reactiveStreams.CustomerMessage;
import org.augustoocc.reactiveStreams.ReactiveCm;
import org.augustoocc.data.DataAccessObjects;
import org.augustoocc.repository.CustomerRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Path("/api/v1/customer")
@Slf4j
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
public class CustomerAPI  {

    @Inject
    EventBus bus;

    @Inject
    ReactiveCm reactiveCm;

    @Inject
    DataAccessObjects customerReactive;

    @Inject
    CustomerRepository customerRepo;

    @Inject
    DateTimeFormatter logtimestamp;


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> add(Customer c) {
        return bus.<Customer>request("add-customer", c)
                .invoke(i -> {log.info(LocalDateTime.now(ZoneOffset.UTC).format(logtimestamp));})
                .map(i -> Response.ok(i.body()).build());
    }


    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("update/{id}/")
    public Uni<Response> putCustomer(@PathParam("id") Long id, Customer customer) {
        log.info("Creating update request");
        CustomerMessage cm = new CustomerMessage(id, customer);
        return bus.<Customer>request("update-customer", cm)
                .invoke(i -> {log.info(LocalDateTime.now(ZoneOffset.UTC).format(logtimestamp));})
                .map(i -> Response.ok(i.body()).build());
    }


    @GET
    @Path("/id/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getCustumer(@PathParam("id") Long id) {
        return bus.<Customer>request("get-by-id", id)
                .invoke(i -> {log.info(LocalDateTime.now(ZoneOffset.UTC).format(logtimestamp));})
                .map(i ->  {
                    if(i.body() == null) {
                        return Response.ok().status(400).build();
                    } else {
                        return Response.ok(i.body()).status(200).build();
                    }
                });
    }

    @GET
    public Uni<List<Customer>> list() {
        return customerRepo.listAll(Sort.by("id"));
    }

    @DELETE
    @Path("delete/{id}")
    public Uni<Response> deleteCustomer(@PathParam("id") Long id) {
        log.info("Received delete request for id {}", id);
        return customerReactive.deleteCustomer(id);

    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}/customer-products")
    public Uni<Customer> getProductById(@PathParam("id") Long id) {
       return Uni.combine().all().unis(reactiveCm.getReactiveCustomerStream(id), reactiveCm.listReactiveProducts())
                .combinedWith((customer, listOfProd) -> {
                    customer.getProducts().forEach(productCustomer -> {
                        listOfProd.forEach(originalProduct -> {
                            if(productCustomer.getProduct().equals(originalProduct.getId())) {
                                productCustomer.setName(originalProduct.getName());
                                productCustomer.setDescription(originalProduct.getDescription());

                            }
                        });
                    });
                    return customer;
                });

    }
}