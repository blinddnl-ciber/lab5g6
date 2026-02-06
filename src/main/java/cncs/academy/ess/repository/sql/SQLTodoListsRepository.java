package cncs.academy.ess.repository.sql;

import cncs.academy.ess.model.TodoList;
import cncs.academy.ess.repository.TodoListsRepository;

import java.sql.*;
import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;

import java.util.ArrayList;

public class SQLTodoListsRepository implements TodoListsRepository {
    private final BasicDataSource dataSource;

    public SQLTodoListsRepository(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public TodoList findById(int todoListId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT * FROM lists WHERE id = ?")) {
            stmt.setInt(1, todoListId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTodoList(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find TODO list by ID", e);
        }
        return null;
    }

    @Override
    public List<TodoList> findAll() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT * FROM lists");
             ResultSet rs = stmt.executeQuery()) {
            List<TodoList> todoLists = new ArrayList<>();
            while (rs.next()) {
                todoLists.add(mapResultSetToTodoList(rs));
            }
            return todoLists;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all TODO lists", e);
        }
    }

    @Override
    public List<TodoList> findAllByUserId(int userId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT * FROM lists WHERE owner_id = ?")){
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<TodoList> todoLists = new ArrayList<>();
                while (rs.next()) {
                    todoLists.add(mapResultSetToTodoList(rs));
                }
                return todoLists;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find TODO lists by user ID", e);
        }
    }

    @Override
    public int save(TodoList todo) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "INSERT INTO lists (name,owner_id) VALUES (?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, todo.getName());
            stmt.setInt(2, todo.getOwnerId());
            stmt.executeUpdate();
            int generatedId = 0;
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
            }
            return generatedId;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save TODO list", e);
        }
    }

    @Override
    public void update(TodoList todo) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "UPDATE lists SET name = ? WHERE id = ?")) {
            stmt.setString(1, todo.getName());
            stmt.setInt(2, todo.getListId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save user", e);
        }
    }

    @Override
    public boolean deleteById(int todoListId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement("DELETE FROM lists WHERE id = ?")) {
            stmt.setInt(1, todoListId);
            int done = stmt.executeUpdate();
            return done > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete TODO list", e);
        }
    }

    private TodoList mapResultSetToTodoList(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        int owner_id = rs.getInt("owner_id");
        return new TodoList(id, name, owner_id);
    }
}