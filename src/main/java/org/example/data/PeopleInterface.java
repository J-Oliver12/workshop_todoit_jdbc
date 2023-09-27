package org.example.data;

import org.example.model.Person;

import java.util.Collection;

public interface PeopleInterface {

    Person create(Person person);
    Collection<Person> findAll();
    Person findById(int id);
    Collection<Person> findByName(String name);
    Person update(Person updatedPerson);
    boolean deleteById(int id);

}
