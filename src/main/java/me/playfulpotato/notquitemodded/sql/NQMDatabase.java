package me.playfulpotato.notquitemodded.sql;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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


}
