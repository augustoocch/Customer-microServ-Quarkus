package org.augustoocc.reactiveStreams;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import lombok.extern.slf4j.Slf4j;
import org.augustoocc.domain.Customer;
import org.augustoocc.domain.Product;
import org.augustoocc.repository.CustomerRepo;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ApplicationScoped
public class ReactiveCm {


    @Inject
    Vertx vertxReactive;


    @Inject
    CustomerRepo customerRepo;

    private WebClient webClient;


    @PostConstruct
    void initialize() {
        this.webClient = WebClient.create(vertxReactive,
                new WebClientOptions().setDefaultHost("localhost")
                        .setDefaultPort(8080).setSsl(false).setTrustAll(true));
    }

    //Se utiliza Uni por que es una clase reactiva de mutiny
    //Aca vamos a contruir el cluiente reactivo
    //Create from nos permite crear un stream a partir de algo
    // el item es para crear un stream a partir de un item. Este item es el objeto de negocio
    public Uni<Customer> getReactiveCustomerStream(Long id) {
        Customer customer = customerRepo.getCustomer(id);
        Uni<Customer> item = Uni.createFrom().item(customer);
        return item;
    }

    //El send encia la peticion del metodo get.
    public Uni<List<Product>> listReactiveProducts() {
        return webClient.get(8081, "localhost", "/api/v1/product").send()
                .onFailure().invoke(response -> log.error("Failure getting the List of Products", response))
                .onItem().transform(response -> {
                    List<Product> list = new ArrayList<>();
                    JsonArray objects = response.bodyAsJsonArray();
                    objects.forEach(o -> {
                        log.info("Total objects: ", objects);
                        ObjectMapper om = new ObjectMapper();
                        Product product = null;
                        try {
                            product = om.readValue(o.toString(), Product.class);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                        list.add(product);
                    });
                    return list;
                });
    }

}
