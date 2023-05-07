package org.augustoocc.exceptions;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class NotFoundEx extends RuntimeException{

        @Inject
        DateTimeFormatter logtimestamp;

        public NotFoundEx() {
        }

        public NotFoundEx(String message) {
            super(message);
        }

        public NotFoundEx notFoundToDelete(String info) {
            return new NotFoundEx("The object has null values, " + info + LocalDateTime.now(ZoneOffset.UTC).format(logtimestamp));
        }

        public NotFoundEx notFoundProduct(String info) {
            return new NotFoundEx("The Product microservice is unreacheble, " + info + LocalDateTime.now(ZoneOffset.UTC).format(logtimestamp));
        }

}
