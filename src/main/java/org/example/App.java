package org.example;

import org.example.data.People;
import org.example.data.TodoItems;
import org.example.model.Person;
import org.example.model.Todo;

import java.time.LocalDate;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {

        People people = new People();


        /*Person newPerson = new Person(0,"John", "Doe");

        Person insertedPerson = people.create(newPerson);

        if (insertedPerson != null) {
            System.out.println("New person created with ID: " + insertedPerson.getId());
        } else {
            System.out.println("Failed to insert the new person.");
        }
        people.closeConnection();


        Person newPerson = new Person(1,"Test", "Testsson");

        Person insertedPerson = people.create(newPerson);

        if (insertedPerson != null) {
            System.out.println("New person created with ID: " + insertedPerson.getId());
        } else {
            System.out.println("Failed to insert the new person.");
        }
        people.closeConnection();
        */


        TodoItems todoItems = new TodoItems();

        Person assignee = new Person(1, "Test", "Testsson");

        Todo newTodo = new Todo(
                0,"Finish G46 Project", "Complete the G2 project task", LocalDate.of(2023, 10, 15), false,assignee);

        Todo insertedTodo = todoItems.create(newTodo);

        if (insertedTodo != null) {
            System.out.println("New Todo created and inserted with ID: " + insertedTodo.getId());
        } else {
            System.out.println("Failed to insert the new Todo. ");
        }

        todoItems.closeConnection();
    }
}
