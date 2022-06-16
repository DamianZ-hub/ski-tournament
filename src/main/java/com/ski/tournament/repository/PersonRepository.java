package com.ski.tournament.repository;

import com.ski.tournament.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {

    Person findByUsername(String username);

    Person findByActivationCode(String activationCode);

    @Query("Select count(p) from Person p where p.username = ?1")
    Integer checkIfExistsPersonByUsername(String username);
}
