package org.augustoocc.validations;

import io.quarkus.vertx.ConsumeEvent;
import org.augustoocc.domain.Customer;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Validations {


    public boolean postValidation(Customer c) {
        if (c.getCode()==null || c.getNames()==null || c.getSurname()==null
                || c.getAccountNumber()==null || c.getAddress()==null || c.getPhone()==null
                || c.getCode().isBlank() || c.getNames().isBlank() || c.getSurname().isBlank()
                || c.getAccountNumber().isBlank() || c.getAddress().isBlank() || c.getPhone().isBlank()) {
            return true;
        } else {
            return false;
        }
    }

}
