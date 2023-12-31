package br.com.monteiro.integrationtests.repositories;

import br.com.monteiro.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.monteiro.model.Person;
import br.com.monteiro.repositories.PersonRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    public PersonRepository repository;

    private static Person person;

    @BeforeAll
    public static void setup() {
        person = new Person();
    }

    @Test
    @Order(1)
    public void testFindByName() {

        Pageable pageable = PageRequest.of(
                0,
                6,
                Sort.by(Sort.Direction.ASC, "firstName"));
        person = repository.findPersonsByName("ayr", null).getContent().getFirst();

        assertNotNull(person.getId());
        assertNotNull(person.getFirstName());
        assertNotNull(person.getLastName());
        assertNotNull(person.getAddress());
        assertNotNull(person.getGender());
        assertTrue(person.getEnabled());

        assertEquals(1, person.getId());

        assertEquals("Ayrton", person.getFirstName());
        assertEquals("Senna", person.getLastName());
        assertEquals("São Paulo", person.getAddress());
        assertEquals("Male", person.getGender());

    }

    @Test
    @Order(2)
    public void testDisablePerson() {
        repository.disablePerson(person.getId());
        Pageable pageable = PageRequest.of(
                0,
                6,
                Sort.by(Sort.Direction.ASC, "firstName"));
        person = repository.findPersonsByName("ayr", null).getContent().getFirst();

        assertNotNull(person.getId());
        assertNotNull(person.getFirstName());
        assertNotNull(person.getLastName());
        assertNotNull(person.getAddress());
        assertNotNull(person.getGender());
        assertFalse(person.getEnabled());

        assertEquals(1, person.getId());

        assertEquals("Ayrton", person.getFirstName());
        assertEquals("Senna", person.getLastName());
        assertEquals("São Paulo", person.getAddress());
        assertEquals("Male", person.getGender());

    }

}
