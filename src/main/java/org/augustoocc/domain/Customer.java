package org.augustoocc.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class Customer{

   //Cuando usamos panache entity se quita el id
    //porque panache tiene su propio id.
    private Long id;
    private String code;
    private String accountNumber;
    private String names;
    private String surname;
    private String phone;
    private String address;
    @OneToMany(mappedBy = "customer",cascade = {CascadeType.ALL},fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Product> products;

}
