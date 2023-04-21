package org.augustoocc.controller;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import lombok.extern.slf4j.Slf4j;
import org.augustoocc.domain.Customer;
import org.augustoocc.reactiveStreams.ReactiveCm;
import org.augustoocc.repository.CustomerReactive;
import org.jboss.resteasy.reactive.RestPath;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/api/v1/customer")
@Slf4j
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CustomerAPI  implements PanacheRepository<Customer> {

    @Inject
    EventBus bus;

    @Inject
    ReactiveCm reactiveCm;

    @Inject
    CustomerReactive customerReactive;

    @GET
    public Uni<List<Customer>> list() {
        return listAll(Sort.by("names"));
    }


    @POST
    public Uni<Response> add(Customer c) {
        return bus.<Response>request("add-customer", c)
                .onItem().transform(Message::body);
    }

    @DELETE
    @Path("delete/{id}")
    public Uni<Response> deleteCustomer (@PathParam("id") Long id) {
        return bus.<Response>request("delete-customer", id)
                .onItem().transform(Message::body);
    }

    @PUT
    public Uni<Response> putCustomer(@RestPath  Long id, Customer customer) {
        customer.id = id;
        return bus.<Response>request("update-customer", customer)
                .onItem().transform(Message::body);
    }


    @GET
    @Path("/id/{id}")
    public Uni<Response> getCustumer(@PathParam("id") Long id) {
        return bus.<Response>request("get-by-id", id)
                .onItem().transform(Message::body);
    }

    @GET
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