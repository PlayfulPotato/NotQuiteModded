package me.playfulpotato.notquitemodded.sql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {

    protected List<NQMDatabase> databases = new ArrayList<>();

    /**
     * Developers shouldn't run this unless they know exactly what they are doing. This effects the connection of all NQMDatabases, not just the plugin calling it.
     */
    public void shutdownAllDatabases() {
        for (int i = 0; i < databases.size(); i++) {
            NQMDatabase currentDatabase = databases.get(i);
            try {
                currentDatabase.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
