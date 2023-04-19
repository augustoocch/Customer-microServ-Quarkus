package org.augustoocc.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(
        uniqueConstraints=
        @UniqueConstraint(columnNames={"customer", "product"})
)
public class Product extends PanacheEntity {

    //En este caso se deja el id, para vincularlo al producto
    //Por ello le ponemos que es de tipo transitivo
    @Transient
    private Long id;
    @ManyToOne
    @JoinColumn(name = "customer", referencedColumnName = "id")
    @JsonBackReference
    private Customer customer;
    @Column
    private Long product;
    @Transient
    private String name;
    @Transient
    private String code;
    @Transient
    private String description;
}

    //Transit, quiere decir que los datos no se guardan en la base de datos
    //del microservicio
