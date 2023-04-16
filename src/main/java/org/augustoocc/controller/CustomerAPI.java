package org.augustoocc.controller;

import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;
import org.augustoocc.domain.Customer;
import org.augustoocc.reactiveStreams.ReactiveCm;
import org.augustoocc.repository.CustomerRepoSpring;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/api/v1/customer")
@Slf4j
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CustomerAPI {

    @Inject
    CustomerRepoSpring customerRepo;

    @Inject
    ReactiveCm reactiveCm;

    @GET
    @Blocking
    public List<Customer> list() {
        log.info("Request received - listing objects");
        return customerRepo.findAll();

    }

    @POST
    @Blocking
    public Response postCustomer(Customer customer) {
        log.info("Request received - putting object in db");
        customerRepo.save(customer);
        return Response.ok().build();
    }

    @DELETE
    @Path("delete/{id}")
    @Blocking
    public Response deleteCustomer (@PathParam("id") Long id) {
        log.info("Deleting object with id: ", id);
        customerRepo.delete(customerRepo.findById(id).get());
        return Response.ok().build();
    }

    @PUT
    @Blocking
    public Response putCustomer(Customer customer) {
        log.info("Merging object with id: ", customer.getId());
        customerRepo.save(customerRepo.findById(customer.getId()).get());
        return Response.ok().build();
    }


    @GET
    @Path("/id/{id}")
    @Blocking
    public Customer getCustumer(@PathParam("id") Long id) {
        log.info("Request received - getting customer");
        return customerRepo.findById(id).get();
    }

    @GET
    @Path("{id}/customer-products")
    @Blocking
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