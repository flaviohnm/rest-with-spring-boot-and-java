package br.com.monteiro.repositories;

import br.com.monteiro.model.Person;
import br.com.monteiro.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    @Modifying
    @Query("UPDATE Person p p.enabled = false WHERE p.id = p.id")
    void disablePerson(@Param("id") Long id);

}
