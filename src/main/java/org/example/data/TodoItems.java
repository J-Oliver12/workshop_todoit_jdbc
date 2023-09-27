package org.example.data;

import org.example.db.MySQLConnection;
import org.example.model.Person;
import org.example.model.Todo;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TodoItems implements TodoItemsInterface {

    private Connection connection;

    public TodoItems() {
        connection = MySQLConnection.getConnection();
    }

    @Override
    public Todo create(Todo todo) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO todo_item (title, description, deadline, done, assignee_id) " +
                    "VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, todo.getTitle());
            statement.setString(2, todo.getDescription());
            statement.setDate(3, todo.getDeadline() != null ? Date.valueOf(todo.getDeadline()) : null);
            statement.setBoolean(4, todo.isDone());
            statement.setInt(5, todo.getAssignee() != null ? todo.getAssignee().getId() : 0 );

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                return null;
            }

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1);
                todo.setId(id);
                return todo;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Collection<Todo> findAll() {
        List<Todo> todos = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("select * FROM todo_item");

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("todo_id");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                Date deadline = resultSet.getDate("deadline");
                boolean done = resultSet.getBoolean("done");
                int assigneeId = resultSet.getInt("assignee_id");

                Person assignee = assigneeId > 0 ? new Person(assigneeId, "", "") : null;

                Todo todo = new Todo(id, title, description, deadline != null ? deadline.toLocalDate() : null, done, assignee);
                todos.add(todo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return todos;
    }

    @Override
    public Todo findById(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM todo_item WHERE todo_id = ?");
            statement.setInt(1, id);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return extractTodoFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Collection<Todo> findByDoneStatus(boolean done) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM todo_item WHERE done = ?");
            statement.setBoolean(1, done);

            ResultSet resultSet = statement.executeQuery();
            List<Todo> todos = new ArrayList<>();
            while (resultSet.next()) {
                todos.add(extractTodoFromResultSet(resultSet));
            }
            return todos;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public Collection<Todo> findByAssignee(int assigneeId) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM todo_item WHERE assignee_id = ?");
            statement.setInt(1, assigneeId);

            ResultSet resultSet = statement.executeQuery();
            List<Todo> todos = new ArrayList<>();
            while (resultSet.next()) {
                todos.add(extractTodoFromResultSet(resultSet));
            }
            return todos;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  new ArrayList<>();
    }

    @Override
    public Collection<Todo> findByAssignee(Person assignee) {
        List<Todo> todos = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM todo_item WHERE assignee_id = ?");

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                extractTodoFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  todos;
    }

    @Override
    public Collection<Todo> findByUnassignedTodoItems() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM todo_item WHERE assignee_id IS NULL");

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Todo> todos = new ArrayList<>();
            while (resultSet.next()) {
                todos.add(extractTodoFromResultSet(resultSet));
            }
            return todos;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public Todo update(Todo updatedTodo) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE todo_item SET title = ?, description = ?, deadline = ?, done = ?, assignee_id = ? WHERE todo_id = ?");
            statement.setString(1,updatedTodo.getTitle());
            statement.setString(2, updatedTodo.getDescription());
            statement.setDate(3, updatedTodo.getDeadline() != null ? Date.valueOf(updatedTodo.getDeadline()) : null);
            statement.setBoolean(4, updatedTodo.isDone());

            if (updatedTodo.getAssignee() != null) {
                statement.setInt(5, updatedTodo.getAssignee().getId());
            } else {
                statement.setNull(5, Types.INTEGER);
            }

            statement.setInt(6, updatedTodo.getId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                return updatedTodo;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean deleteById(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM todo_item WHERE todo_id = ?");
            statement.setInt(1,id);

            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Todo extractTodoFromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("todo_id");
        String title = resultSet.getString("title");
        String description = resultSet.getString("description");
        Date deadline = resultSet.getDate("deadline");
        boolean done = resultSet.getBoolean("done");
        int assigneeId = resultSet.getInt("assignee_id");

        Person assignee = assigneeId > 0 ? new Person(assigneeId, "", "") : null;

        return new Todo(id, title, description, deadline != null ? deadline.toLocalDate() : null, done, assignee);
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
