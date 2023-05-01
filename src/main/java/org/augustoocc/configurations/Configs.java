package org.augustoocc.configurations;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class Configs {

    @Produces
    DateTimeFormatter dateTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
    }
}
