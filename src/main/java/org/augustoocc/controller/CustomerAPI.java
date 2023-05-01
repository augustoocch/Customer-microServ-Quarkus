package org.augustoocc.controller;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import lombok.extern.slf4j.Slf4j;
import org.augustoocc.domain.Customer;
import org.augustoocc.reactiveStreams.CustomerMessage;
import org.augustoocc.reactiveStreams.ReactiveCm;
import org.augustoocc.repository.CustomerReactive;
import org.augustoocc.repository.CustomerRepository;
import org.jboss.resteasy.reactive.RestPath;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static org.jboss.resteasy.reactive.RestResponse.StatusCode.NOT_FOUND;

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
    CustomerReactive customerReactive;

    @Inject
    CustomerRepository customerRepo;
    @Inject
    DateTimeFormatter logtimestamp;

    @GET
    public Uni<List<Customer>> list() {
        return customerRepo.listAll(Sort.by("id"));
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> add(Customer c) {
        return bus.<Customer>request("add-customer", c)
                .invoke(i -> {log.info(LocalDateTime.now(ZoneOffset.UTC).format(logtimestamp));})
                .map(i -> Response.ok(i.body()).build());
    }

    @DELETE
    @Path("delete/{id}")
    public Uni<Response> deleteCustomer(@PathParam("id") Long id) {
        return bus.<Response>request("delete-customer", id)
                .map(Message::body)
                .onItem().transformToUni(response -> Uni.createFrom().item(() -> response))
                .onFailure().recoverWithItem(Response.serverError().build());
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> putCustomer(@RestPath  Long id, Customer customer) {
        return bus.<Customer>request("update-customer", customer)
                .invoke(i -> {log.info(LocalDateTime.now(ZoneOffset.UTC).format(logtimestamp));})
                .map(i -> Response.ok(i.body()).build());
    }


    @GET
    @Path("/id/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getCustumer(@PathParam("id") Long id, Customer c) {
        CustomerMessage cm = new CustomerMessage(id, c);
        return bus.<Customer>request("get-by-id", cm)
                .invoke(i -> {log.info(LocalDateTime.now(ZoneOffset.UTC).format(logtimestamp));})
                .map(i -> Response.ok(i.body()).build());
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