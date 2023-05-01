package org.augustoocc.validations;

import io.quarkus.vertx.ConsumeEvent;
import org.augustoocc.domain.Customer;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Validations {


    public boolean postValidation(Customer c) {
        if (c.getCode()==null || c.getNames()==null || c.getSurname()==null || c.getAccountNumber()==null || c.getAddress()==null || c.getPhone()==null) {
            return true;
        } else {
            return false;
        }
    }

}
