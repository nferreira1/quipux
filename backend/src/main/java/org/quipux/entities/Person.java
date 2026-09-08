package org.quipux.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "persons", uniqueConstraints = {
        @UniqueConstraint(name = "uk_persons_cpf", columnNames = "cpf"),
        @UniqueConstraint(name = "uk_persons_email", columnNames = "email")
})
public class Person extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    public Long personId;

    @Column(name = "cpf", nullable = false, length = 11)
    public String cpf;

    @Column(name = "first_name", nullable = false, length = 100)
    public String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    public String lastName;

    @Column(name = "email", nullable = false, length = 150)
    public String email;
}