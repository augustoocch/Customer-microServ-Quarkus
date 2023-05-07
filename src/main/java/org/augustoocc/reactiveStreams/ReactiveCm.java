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
import org.augustoocc.exceptions.NotFoundEx;
import org.augustoocc.repository.CustomerRepository;

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
    CustomerRepository customerRepo;

    private WebClient webClient;

    @Inject
    NotFoundEx notFoundEx;

    @PostConstruct
    void initialize() {
        this.webClient = WebClient.create(vertxReactive,
                new WebClientOptions().setDefaultHost("localhost")
                        .setDefaultPort(8080).setSsl(false).setTrustAll(true));
    }

    public Uni<Customer> getReactiveCustomerStream(Long id) {
        Uni<Customer> item = customerRepo.findById(id);
        return item;
    }

    public Uni<List<Product>> listReactiveProducts() {
        return webClient.get(8081, "localhost", "/api/v1/product").send()
                .onFailure().invoke(response -> log.error("ERROR ->", notFoundEx.notFoundProduct("Method listReactiveProducts in ReactiveCm: ")))
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
