package org.augustoocc.reactiveStreams;

import lombok.Getter;
import lombok.Setter;
import org.augustoocc.domain.Customer;


@Getter
@Setter
public class CustomerMessage {
        private Long id;
        private Customer customer;

        public CustomerMessage(Long id, Customer customer) {
            this.id = id;
            this.customer = customer;
        }
}

