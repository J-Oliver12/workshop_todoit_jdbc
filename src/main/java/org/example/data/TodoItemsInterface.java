package org.example.data;

import org.example.model.Person;
import org.example.model.Todo;

import java.util.Collection;

public interface TodoItemsInterface {
    Todo create(Todo todo);
    Collection<Todo> findAll();
    Todo findById(int id);
    Collection<Todo> findByDoneStatus(boolean done);
    Collection<Todo> findByAssignee(int assigneeId);
    Collection<Todo> findByAssignee(Person assignee);

    Collection<Todo> findByUnassignedTodoItems();

    Todo update(Todo updatedTodo);
    boolean deleteById(int id);
}
