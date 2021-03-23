package com.orlovkiy2.dao.jdbc;

import com.orlovkiy2.dao.GenreDao;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Slf4j
public class JdbcGenreDao implements GenreDao {
    private final String SAVE_GENRE = "INSERT INTO genres (name) VALUES (?)";
    private DataSource dataSource;

    public JdbcGenreDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void saveGenres(List<String> genreList) {

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_GENRE)) {

            connection.setAutoCommit(false);

            for (String genre : genreList) {
                preparedStatement.setString(1, genre);
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            log.info("Sql exception was throw while saving genres", e);
            throw new RuntimeException("Всё пропала! Не будет жанров в БД!");
        }

    }
}
