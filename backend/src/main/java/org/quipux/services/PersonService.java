package org.quipux.services;

import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.quipux.clients.NationalizeClient;
import org.quipux.dtos.NationalizeResponse;
import org.quipux.dtos.PersonRequest;
import org.quipux.entities.Person;
import org.quipux.exceptions.ApiException;
import org.quipux.repositories.PersonRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@ApplicationScoped
public class PersonService {

    private static final Logger LOG = Logger.getLogger(PersonService.class);

    @Inject
    PersonRepository personRepository;

    @Inject
    @RestClient
    NationalizeClient nationalizeClient;

    @Transactional
    public Person register(PersonRequest request) {
        String cpf = request.cpf.trim();
        String email = request.email.trim().toLowerCase();

        if (personRepository.existsByCpf(cpf)) {
            throw ApiException.conflict("Já existe uma pessoa cadastrada com este CPF.");
        }
        if (personRepository.existsByEmail(email)) {
            throw ApiException.conflict("Já existe uma pessoa cadastrada com este e-mail.");
        }

        Person person = new Person();
        person.cpf = cpf;
        person.firstName = request.firstName.trim();
        person.lastName = request.lastName.trim();
        person.email = email;

        personRepository.persist(person);
        return person;
    }

    public List<Person> listAll(int page, int size) {
        return personRepository.findAll().page(Page.of(page - 1, size)).list();
    }

    public long countAll() {
        return personRepository.count();
    }

    public Person findByCpf(String cpf) {
        return personRepository.findByCpf(cpf)
                .orElseThrow(() -> ApiException.notFound(
                        "Nenhuma pessoa encontrada com o CPF informado."));
    }

    @Transactional
    public void deleteByCpf(String cpf) {
        Person person = findByCpf(cpf);
        personRepository.delete(person);
    }

    public String findNationalityByCpf(String cpf) {
        Person person = findByCpf(cpf);

        NationalizeResponse response;
        try {
            response = nationalizeClient.findNationalityByName(person.firstName);
        } catch (Exception e) {
            LOG.errorf(e, "Falha ao consultar a API de nacionalidade para o nome '%s'.",
                    person.firstName);
            throw ApiException.unavailable(
                    "Serviço externo de previsão de nacionalidade indisponível no momento.");
        }

        if (response == null || response.country == null || response.country.isEmpty()) {
            throw ApiException.notFound(
                    "Não foi possível prever a nacionalidade para o nome informado.");
        }

        NationalizeResponse.CountryProbability mostLikely = response.country.stream()
                .max(Comparator.comparingDouble(country -> country.probability))
                .orElseThrow();

        String countryName = Locale.of("", mostLikely.countryId)
                .getDisplayCountry(Locale.of("pt", "BR"));

        return countryName.isBlank() ? mostLikely.countryId : countryName;
    }
}
