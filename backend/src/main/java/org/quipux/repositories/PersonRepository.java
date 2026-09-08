package org.quipux.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.quipux.entities.Person;

import java.util.Optional;

@ApplicationScoped
public class PersonRepository implements PanacheRepository<Person> {

    public Optional<Person> findByCpf(String cpf) {
        return find("cpf", cpf).firstResultOptional();
    }

    public boolean existsByCpf(String cpf) {
        return count("cpf", cpf) > 0;
    }

    public boolean existsByEmail(String email) {
        return count("lower(email) = ?1", email.toLowerCase()) > 0;
    }
}
