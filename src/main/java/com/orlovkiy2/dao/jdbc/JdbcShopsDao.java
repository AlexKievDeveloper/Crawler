package com.orlovkiy2.dao.jdbc;

import com.orlovkiy2.dao.ShopDao;
import com.orlovkiy2.entity.Shop;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
public class JdbcShopsDao implements ShopDao {
    private final String SAVE_LINK_TO_IMAGE = "INSERT INTO SHOP_IMAGES (link_to_image) VALUES (?)";
    private final String SAVE_SHOP = "INSERT INTO SHOP (link_to_main_page, link_to_vinyls_catalog, link_to_image, name) VALUES (?, ?, ?, ?)";
    private DataSource dataSource;

    public JdbcShopsDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void saveShops(List<Shop> shops) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement saveShopPreparedStatement = connection.prepareStatement(SAVE_SHOP);
             PreparedStatement saveLinkPreparedStatement = connection.prepareStatement(SAVE_LINK_TO_IMAGE)) {

            connection.setAutoCommit(false);

            for (Shop shop : shops) {
                saveLinkPreparedStatement.setString(1, shop.getLinkToLogo());
                saveLinkPreparedStatement.execute();
                ResultSet resultSet = saveLinkPreparedStatement.getGeneratedKeys();
                long key = resultSet.getLong(1);

                saveShopPreparedStatement.setString(1, shop.getLinkToMainPage());
                saveShopPreparedStatement.setString(2, shop.getLinkToCatalogPage());
                saveShopPreparedStatement.setLong(3, key);
                saveShopPreparedStatement.setString(4, shop.getName());
                saveShopPreparedStatement.addBatch();
            }

            saveShopPreparedStatement.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            log.info("Sql exception was throw while saving shops", e);
            throw new RuntimeException("Всё пропала! Не будет жанров в БД!");
        }
    }

}

