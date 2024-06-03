package me.playfulpotato.notquitemodded.sql;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.concurrent.CompletableFuture;

public abstract class NQMDatabase {
    protected Connection connection;

    /**
     * Handles the making of the database and the closing later. Using NQMDatabases just handles boilerplate code. You still need knowledge on how to handle and use databases correctly.
     * @param plugin The plugin making the database.
     * @param databaseNameAndPath The name and extra pathing of the SQLite database.
     */
    public NQMDatabase(@NotNull Plugin plugin, @NotNull String databaseNameAndPath) {
        try {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }

            connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + "/" + databaseNameAndPath + ".db");
            NotQuiteModded.databaseHandler.databases.add(this);
            AfterConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            plugin.getLogger().info("Failed to connect to database! " + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }
    protected void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
    public abstract void AfterConnection();

    public CompletableFuture<Boolean> keyExists(final String tableName, final String columnName, final String rowSearch) {
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE " + columnName + " = " + rowSearch)) {
                ResultSet resultSet = preparedStatement.executeQuery();
                preparedStatement.close();
                return resultSet.next();
            } catch (SQLException ignored) {}
            return false;
        });
    }

    public CompletableFuture<Boolean> TableExists(final String tableName) {
        CompletableFuture<Boolean> existsFuture = CompletableFuture.supplyAsync(() -> {
            try {
                DatabaseMetaData dbm = connection.getMetaData();
                ResultSet rs = dbm.getTables(null, null, tableName, null);
                return rs.next();
            } catch (SQLException e) {
                return null;
            }
        });
        return existsFuture;
    }

    public CompletableFuture<Boolean> ColumnExists(final String tableName, final String columnName) {
        CompletableFuture<Boolean> existsFuture = CompletableFuture.supplyAsync(() -> {
            try {
                DatabaseMetaData dbm = connection.getMetaData();
                ResultSet rs = dbm.getColumns(null, null, tableName, columnName);
                return rs.next();
            } catch (SQLException e) {
                return null;
            }
        });
        return existsFuture;
    }
}
