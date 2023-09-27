package org.example.data;

import org.example.db.MySQLConnection;
import org.example.model.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class People implements PeopleInterface {

    private Connection connection;

    public People() {
        connection = MySQLConnection.getConnection();
    }

    @Override
    public Person create(Person person) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO person (first_name, last_name) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, person.getFirstName());
            statement.setString(2, person.getLastName());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                return  null;
            }

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1);
                person.setId(id);
                return person;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Collection<Person> findAll() {
        List<Person> people = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM person");

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("person_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                Person person = new Person(id, firstName, lastName);
                people.add(person);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return people;
    }

    public Person findById(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM person WHERE person_id = ?");
            statement.setInt(1,id);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int personId = resultSet.getInt("person_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                return new Person(personId, firstName,lastName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Collection <Person> findByName(String name) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM person WHERE first_name = ? OR last_name = ?");
            statement.setString(1, name);
            statement.setString(2, name);

            ResultSet resultSet = statement.executeQuery();
            List<Person> people = new ArrayList<>();
            while (resultSet.next()) {
                int personId = resultSet.getInt("person_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                people.add(new Person(personId, firstName, lastName));
            }
            return people;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public Person update(Person updatedPerson) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE person SET first_name = ?, last_name = ? WHERE person_id = ?");
            statement.setString(1, updatedPerson.getFirstName());
            statement.setString(2, updatedPerson.getLastName());
            statement.setInt(3, updatedPerson.getId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                return updatedPerson;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean deleteById(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM person WHERE person_id = ?");
            statement.setInt(1, id);

            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
