package me.playfulpotato.notquitemodded.block;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import me.playfulpotato.notquitemodded.sql.NQMDatabase;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BlockDatabase extends NQMDatabase {

    public BlockDatabase() {
        super(NotQuiteModded.GetPlugin(), "Blocks");
    }
    @Override
    public void AfterConnection() {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS blockKeys (" +
                    "blockType TEXT NOT NULL PRIMARY KEY, " +
                    "nextID BIGINT NOT NULL DEFAULT 0)"
            );
        } catch (SQLException ignored) {}
    }
    public CompletableFuture<Boolean> CreateNewChunkTable(Chunk chunk) {
        final String worldUID = chunk.getWorld().getUID().toString().replace('-', '_');
        final String chunkKey = String.valueOf(chunk.getChunkKey());
        final String newTableName = TableNameFromChunk(chunk);;
        CompletableFuture<Boolean> attemptSuccess = CompletableFuture.supplyAsync(() -> {
            final String possibleNewWorldTable = "world_" + worldUID;
            try (Statement statement = connection.createStatement()) {
                try (Statement statement2 = connection.createStatement()) {
                    statement2.execute("CREATE TABLE IF NOT EXISTS " + possibleNewWorldTable + " (" +
                            "chunkKey STRING NOT NULL PRIMARY KEY, " +
                            "nextSemantic INTEGER NOT NULL DEFAULT 0)");
                }

                if (!keyExists(possibleNewWorldTable, "chunkKey", chunkKey).join()) {
                    try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + possibleNewWorldTable + " (chunkKey, nextSemantic) VALUES (?, ?)")) {
                        preparedStatement.setString(1, chunkKey);
                        preparedStatement.setInt(2, 0);
                        preparedStatement.executeUpdate();
                    }
                }
                statement.execute("CREATE TABLE IF NOT EXISTS " + newTableName + " (" +
                        "semanticID INTEGER NOT NULL PRIMARY KEY, " +
                        "x INTEGER NOT NULL, " +
                        "y INTEGER NOT NULL, " +
                        "z INTEGER NOT NULL, " +
                        "blockType TEXT NOT NULL, " +
                        "blockID BIGINT NOT NULL)"
                );
                statement.close();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        });
        return attemptSuccess;
    }

    public CompletableFuture<List<Pair<String, Pair<int[], Long>>>> RetrieveAllBlockDataInChunk(Chunk chunk) {
        final String tableName = TableNameFromChunk(chunk);;

        CompletableFuture<Boolean> exists = TableExists(tableName);

        try {
            if (!exists.join()) {
                return CompletableFuture.supplyAsync(() -> null);
            }
        } catch (Exception ignored) {}

        CompletableFuture<List<Pair<String, Pair<int[], Long>>>> obtainFuture = CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + tableName)) {
                ResultSet results = preparedStatement.executeQuery();

                List<Pair<String, Pair<int[], Long>>> finalData = new ArrayList<>();
                while (results.next()) {
                    int[] numberValues = new int[4];
                    numberValues[0] = results.getInt("x");
                    numberValues[1] = results.getInt("y");
                    numberValues[2] = results.getInt("z");
                    numberValues[3] = results.getInt("semanticID");
                    String blockIdentifier = results.getString("blockType");
                    long uniqueID = results.getLong("blockID");
                    finalData.add(Pair.of(blockIdentifier, Pair.of(numberValues, uniqueID)));
                }
                preparedStatement.close();
                return finalData;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
        return obtainFuture;
    }

    public CompletableFuture<Boolean> CreateNewBlockTypeTable(final String blockStorageKeyTemp, final int intCount, final int stringCount, final int entityCount) {
        final String blockStorageKey = "block_" + blockStorageKeyTemp.replace(':', '_');
        if (TableExists(blockStorageKey).join()) {
            CompletableFuture<Boolean> futureBoolean =  CompletableFuture.supplyAsync(() -> {
                try {
                    // Create extra columns in table if need be.
                    for (int index = 0; index < intCount; index++) {
                        if (!ColumnExists(blockStorageKey, "int" + index).join()) {
                            try(Statement statement = connection.createStatement()) {
                                statement.execute("ALTER TABLE " + blockStorageKey + " ADD int" + index + " INTEGER NOT NULL DEFAULT 0");
                            }
                        }
                    }
                    for (int index = 0; index < stringCount; index++) {
                        if (!ColumnExists(blockStorageKey, "string" + index).join()) {
                            try(Statement statement = connection.createStatement()) {
                                statement.execute("ALTER TABLE " + blockStorageKey + " ADD string" + index + " TEXT");
                            }
                        }
                    }
                    for (int index = 0; index < entityCount; index++) {
                        if (!ColumnExists(blockStorageKey, "entityUUID" + index).join()) {
                            try(Statement statement = connection.createStatement()) {
                                statement.execute("ALTER TABLE " + blockStorageKey + " ADD entityUUID" + index + " TEXT NOT NULL");
                            }
                        }
                    }

                    // Opposite applies here, check for, and if they exist, delete 6 extra in the chain of each.
                    for (int index = intCount; index < intCount + 6; index++) {
                        if (ColumnExists(blockStorageKey, "int" + index).join()) {
                            try(Statement statement = connection.createStatement()) {
                                statement.execute("ALTER TABLE " + blockStorageKey + " DROP int" + index);
                            }
                        }
                    }
                    for (int index = stringCount; index < stringCount + 6; index++) {
                        if (ColumnExists(blockStorageKey, "string" + index).join()) {
                            try(Statement statement = connection.createStatement()) {
                                statement.execute("ALTER TABLE " + blockStorageKey + " DROP string" + index);
                            }
                        }
                    }
                    for (int index = entityCount; index < entityCount + 6; index++) {
                        if (ColumnExists(blockStorageKey, "entityUUID" + index).join()) {
                            try(Statement statement = connection.createStatement()) {
                                statement.execute("ALTER TABLE " + blockStorageKey + " DROP entityUUID" + index);
                            }
                        }
                    }
                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            });
            return futureBoolean;
        } else {
            CompletableFuture<Boolean> futureBoolean =  CompletableFuture.supplyAsync(() -> {
                try (Statement statement = connection.createStatement()) {
                    StringBuilder statementString = new StringBuilder("CREATE TABLE IF NOT EXISTS " + blockStorageKey + " (" +
                            "blockID BIGINT NOT NULL PRIMARY KEY");
                    if (intCount == 0 && stringCount == 0 && entityCount == 0) {
                        statementString.append(")");
                    } else {
                        statementString.append(", ");
                    }
                    for (int index = 0; index < intCount; index++) {
                        statementString.append("int").append(index).append(" INTEGER NOT NULL DEFAULT 0");
                        if (index == (intCount - 1) && stringCount == 0 && entityCount == 0) {
                            statementString.append(")");
                        } else {
                            statementString.append(", ");
                        }
                    }
                    for (int index = 0; index < stringCount; index++) {
                        statementString.append("string").append(index).append(" TEXT");
                        if (index == (stringCount - 1) && entityCount == 0) {
                            statementString.append(")");
                        } else {
                            statementString.append(", ");
                        }
                    }
                    for (int index = 0; index < entityCount; index++) {
                        statementString.append("entityUUID").append(index).append(" TEXT NOT NULL");
                        if (index == (entityCount - 1)) {
                            statementString.append(")");
                        } else {
                            statementString.append(", ");
                        }
                    }
                    statement.execute(statementString.toString());

                    try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO blockKeys (blockType, nextID) VALUES (?, ?)")) {
                        preparedStatement.setString(1, blockStorageKeyTemp);
                        preparedStatement.setLong(2, 1);
                        preparedStatement.execute();
                    }
                    statement.close();
                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            });
            return futureBoolean;
        }
    }

    public CompletableFuture<Pair<Pair<int[], String[]>, UUID[]>> ObtainAllUniqueInformationAboutBlock(final String blockStorageKeyTemp, final long ID, final int intCount, final int stringCount, final int entityCount) {
        final String blockStorageKey = "block_" + blockStorageKeyTemp.replace(':', '_');
        if (TableExists(blockStorageKey).join()) {
            CompletableFuture<Pair<Pair<int[], String[]>, UUID[]>> dataLookup = CompletableFuture.supplyAsync(() -> {
                try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + blockStorageKey + " WHERE blockID = ?")) {
                    preparedStatement.setLong(1, ID);
                    ResultSet results = preparedStatement.executeQuery();

                    Pair<Pair<int[], String[]>, UUID[]> finalData = null;
                    if (results.next()) {
                        int[] integerValues = new int[intCount];
                        String[] stringValues = new String[stringCount];
                        UUID[] UUIDValues = new UUID[entityCount];
                        for (int index = 0; index < intCount; index++) {
                            integerValues[index] = results.getInt("int" + index);
                        }
                        for (int index = 0; index < stringCount; index++) {
                            stringValues[index] = results.getString("string" + index);
                        }
                        for (int index = 0; index < entityCount; index++) {
                            UUIDValues[index] = UUID.fromString(results.getString("entityUUID" + index));
                        }
                        finalData = Pair.of(Pair.of(integerValues, stringValues), UUIDValues);
                    }
                    preparedStatement.close();
                    return finalData;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            });
            return dataLookup;
        } else {
            return CompletableFuture.supplyAsync(() -> null);
        }
    }

    public CompletableFuture<HashMap<Long, Pair<Pair<int[], String[]>, UUID[]>>> ObtainAllUniqueInformationAboutBlockType(final String blockStorageKeyTemp, final int intCount, final int stringCount, final int entityCount) {
        final String blockStorageKey = "block_" + blockStorageKeyTemp.replace(':', '_');
        if (TableExists(blockStorageKey).join()) {
            CompletableFuture<HashMap<Long, Pair<Pair<int[], String[]>, UUID[]>>> dataLookup = CompletableFuture.supplyAsync(() -> {
                try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + blockStorageKey)) {
                    ResultSet results = preparedStatement.executeQuery();

                    HashMap<Long, Pair<Pair<int[], String[]>, UUID[]>> finalData = new HashMap<>();
                    while (results.next()) {
                        int[] integerValues = new int[intCount];
                        String[] stringValues = new String[stringCount];
                        UUID[] UUIDValues = new UUID[entityCount];
                        for (int index = 0; index < intCount; index++) {
                            integerValues[index] = results.getInt("int" + index);
                        }
                        for (int index = 0; index < stringCount; index++) {
                            stringValues[index] = results.getString("string" + index);
                        }
                        for (int index = 0; index < entityCount; index++) {
                            UUIDValues[index] = UUID.fromString(results.getString("entityUUID" + index));
                        }
                        Pair<Pair<int[], String[]>, UUID[]> pairData = Pair.of(Pair.of(integerValues, stringValues), UUIDValues);
                        long blockID = results.getLong("blockID");
                        finalData.put(blockID, pairData);
                    }
                    preparedStatement.close();
                    return finalData;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            });
            return dataLookup;
        } else {
            return CompletableFuture.supplyAsync(() -> null);
        }
    }

    public CompletableFuture<int[]> ObtainIntegersFromBlock(final String blockStorageKeyTemp, final long ID, final int intCount) {
        final String blockStorageKey = "block_" + blockStorageKeyTemp.replace(':', '_');
        if (TableExists(blockStorageKey).join()) {
            CompletableFuture<int[]> dataLookup = CompletableFuture.supplyAsync(() -> {
                try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + blockStorageKey + " WHERE blockID = ?")) {
                    preparedStatement.setLong(1, ID);
                    ResultSet results = preparedStatement.executeQuery();

                    int[] finalData = new int[intCount];
                    if (results.next()) {
                        for (int index = 0; index < intCount; index++) {
                            finalData[index] = results.getInt("int" + index);
                        }
                    }
                    preparedStatement.close();
                    return finalData;
                } catch (SQLException e) {
                    return null;
                }
            });
            return dataLookup;
        } else {
            return CompletableFuture.supplyAsync(() -> null);
        }
    }

    public CompletableFuture<String[]> ObtainStringsFromBlock(final String blockStorageKeyTemp, final long ID, final int stringCount) {
        final String blockStorageKey = "block_" + blockStorageKeyTemp.replace(':', '_');
        if (TableExists(blockStorageKey).join()) {
            CompletableFuture<String[]> dataLookup = CompletableFuture.supplyAsync(() -> {
                try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + blockStorageKey + " WHERE blockID = ?")) {
                    preparedStatement.setLong(1, ID);
                    ResultSet results = preparedStatement.executeQuery();

                    String[] finalData = new String[stringCount];
                    if (results.next()) {
                        for (int index = 0; index < stringCount; index++) {
                            finalData[index] = results.getString("string" + index);
                        }
                    }
                    preparedStatement.close();
                    return finalData;
                } catch (SQLException e) {
                    return null;
                }
            });
            return dataLookup;
        } else {
            return CompletableFuture.supplyAsync(() -> null);
        }
    }

    public CompletableFuture<UUID[]> ObtainUUIDsFromBlock(final String blockStorageKeyTemp, final long ID, final int UUIDCount) {
        final String blockStorageKey = "block_" + blockStorageKeyTemp.replace(':', '_');
        if (TableExists(blockStorageKey).join()) {
            CompletableFuture<UUID[]> dataLookup = CompletableFuture.supplyAsync(() -> {
                try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + blockStorageKey + " WHERE blockID = ?")) {
                    preparedStatement.setLong(1, ID);
                    ResultSet results = preparedStatement.executeQuery();

                    UUID[] finalData = new UUID[UUIDCount];
                    if (results.next()) {
                        for (int index = 0; index < UUIDCount; index++) {
                            finalData[index] = UUID.fromString(results.getString("entityUUID" + index));
                        }
                    }
                    preparedStatement.close();
                    return finalData;
                } catch (SQLException e) {
                    return null;
                }
            });
            return dataLookup;
        } else {
            return CompletableFuture.supplyAsync(() -> null);
        }
    }

    public CompletableFuture<Pair<Integer, Long>> WriteNewBlockData(final String blockStorageKey, Chunk chunk, final int[] integerData, final String[] stringData, final UUID[] uuidData, final Location location) {
        if (!TableExistsOffChunk(chunk).join()) {
            Boolean newTableResult = CreateNewChunkTable(chunk).join();
            if (newTableResult == null || !newTableResult) {
                return CompletableFuture.supplyAsync(() -> null);
            }
        }
        location.toBlockLocation();
        final int x = location.getBlockX();
        final int y = location.getBlockY();
        final int z = location.getBlockZ();
        final String blockStorageTableName = "block_" + blockStorageKey.replace(':', '_');
        if (TableExists(blockStorageTableName).join()) {
            final String worldUID = chunk.getWorld().getUID().toString().replace('-', '_');
            final String chunkKey = String.valueOf(chunk.getChunkKey());
            final String chunkTableName = TableNameFromChunk(chunk);
            CompletableFuture<Pair<Integer, Long>> writeFuture = CompletableFuture.supplyAsync(() -> {
                try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + chunkTableName + " (semanticID, x, y, z, blockType, blockID) VALUES (?, ?, ?, ?, ?, ?)")) {
                    final String worldTableName = "world_" + worldUID;

                    int semanticID = 0;
                    try (PreparedStatement worldStatement1 = connection.prepareStatement("SELECT * FROM " + worldTableName + " WHERE chunkKey = ?")) {
                        worldStatement1.setString(1, chunkKey);
                        ResultSet resultSet = worldStatement1.executeQuery();
                        if (resultSet.next()) {
                            semanticID = resultSet.getInt("nextSemantic");
                            try (PreparedStatement worldStatement2 = connection.prepareStatement("UPDATE " + worldTableName + " SET nextSemantic = ? WHERE chunkKey = ?")) {
                                worldStatement2.setInt(1, semanticID+1);
                                worldStatement2.setString(2, chunkKey);
                                worldStatement2.execute();
                            }
                        }
                    }
                    long blockID = 0;
                    try (PreparedStatement blockKeyStatement1 = connection.prepareStatement("SELECT * FROM blockKeys WHERE blockType = ?")) {
                        blockKeyStatement1.setString(1, blockStorageKey);
                        ResultSet resultSet = blockKeyStatement1.executeQuery();
                        if (resultSet.next()) {
                            blockID = resultSet.getLong("nextID");
                            try (PreparedStatement blockKeyStatement2 = connection.prepareStatement("UPDATE blockKeys SET nextID = ? WHERE blockType = ?")) {
                                blockKeyStatement2.setLong(1, blockID+1);
                                blockKeyStatement2.setString(2, blockStorageKey);
                                blockKeyStatement2.execute();
                            }
                        }
                    }
                    StringBuilder stringBuilder = new StringBuilder("INSERT INTO " + blockStorageTableName + " (blockID, ");
                    int questionMarkCount = 1;
                    for (int index = 0; index < integerData.length; index++) {
                        stringBuilder.append("int").append(index);
                        questionMarkCount++;
                        if (index == (integerData.length - 1) && stringData.length == 0 && uuidData.length == 0) {
                            stringBuilder.append(")");
                        } else {
                            stringBuilder.append(", ");
                        }
                    }
                    for (int index = 0; index < stringData.length; index++) {
                        stringBuilder.append("string").append(index);
                        questionMarkCount++;
                        if (index == (stringData.length - 1) && uuidData.length == 0) {
                            stringBuilder.append(")");
                        } else {
                            stringBuilder.append(", ");
                        }
                    }
                    for (int index = 0; index < uuidData.length; index++) {
                        stringBuilder.append("entityUUID").append(index);
                        questionMarkCount++;
                        if (index == (uuidData.length - 1)) {
                            stringBuilder.append(")");
                        } else {
                            stringBuilder.append(", ");
                        }
                    }
                    stringBuilder.append(" VALUES (");
                    for (int i = 0; i < questionMarkCount; i++) {
                        stringBuilder.append("?");
                        if (i == (questionMarkCount - 1)) {
                            stringBuilder.append(")");
                        } else {
                            stringBuilder.append(", ");
                        }
                    }

                    try (PreparedStatement blockSpecificDataStatement = connection.prepareStatement(stringBuilder.toString())) {
                        int currentIndex = 1;
                        blockSpecificDataStatement.setLong(currentIndex, blockID);
                        for (int specificInteger : integerData) {
                            currentIndex++;
                            blockSpecificDataStatement.setInt(currentIndex, specificInteger);
                        }
                        for (String specificString : stringData) {
                            currentIndex++;
                            blockSpecificDataStatement.setString(currentIndex, specificString);
                        }
                        for (UUID specificUUID : uuidData) {
                            currentIndex++;
                            blockSpecificDataStatement.setString(currentIndex, specificUUID.toString());
                        }
                        blockSpecificDataStatement.execute();
                    }

                    preparedStatement.setInt(1, semanticID);
                    preparedStatement.setInt(2, x);
                    preparedStatement.setInt(3, y);
                    preparedStatement.setInt(4, z);
                    preparedStatement.setString(5, blockStorageKey);
                    preparedStatement.setLong(6, blockID);
                    preparedStatement.execute();
                    preparedStatement.close();
                    return Pair.of(semanticID, blockID);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            });
            return writeFuture;
        } else {
            return CompletableFuture.supplyAsync(() -> null);
        }
    }

    public CompletableFuture<Boolean> DeleteBlockData(final String blockStorageKey, final long deletionID, final int deletionSemanticID, Chunk chunk) {
        final String blockStorageTableName = "block_" + blockStorageKey.replace(':', '_');
        final String chunkTableName = TableNameFromChunk(chunk);
        CompletableFuture<Boolean> deletionFuture = CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM " + chunkTableName + " WHERE semanticID = ?")) {
                preparedStatement.setInt(1, deletionSemanticID);
                preparedStatement.execute();
                try (PreparedStatement preparedStatement2 = connection.prepareStatement("DELETE FROM " + blockStorageTableName + " WHERE blockID = ?")) {
                    preparedStatement2.setLong(1, deletionID);
                    preparedStatement2.execute();
                }

                preparedStatement.close();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        });
        return deletionFuture;
    }

    public CompletableFuture<Boolean> WriteUniqueBlockInformation(final String blockStorageKey, final long writeID, final int[] integerData, final String[] stringData, final UUID[] uuidData) {
        final String blockStorageTableName = "block_" + blockStorageKey.replace(':', '_');
        CompletableFuture<Boolean> writeFuture = CompletableFuture.supplyAsync(() -> {
            StringBuilder stringBuilder = new StringBuilder("UPDATE " + blockStorageTableName + " SET ");
            for (int i = 0; i < integerData.length; i++) {
                stringBuilder.append("int").append(i).append(" = ?");
                if (stringData.length > 0 || uuidData.length > 0) {
                    stringBuilder.append(", ");
                } else if (i != (integerData.length-1)) {
                    stringBuilder.append(", ");
                }
            }
            for (int i = 0; i < stringData.length; i++) {
                stringBuilder.append("string").append(i).append(" = ?");
                if (uuidData.length > 0) {
                    stringBuilder.append(", ");
                } else if (i != (stringData.length-1)) {
                    stringBuilder.append(", ");
                }
            }
            for (int i = 0; i < uuidData.length; i++) {
                stringBuilder.append("entityUUID").append(i).append(" = ?");
                if (i != (uuidData.length-1)) {
                    stringBuilder.append(", ");
                }
            }
            stringBuilder.append(" WHERE blockID = ?");

            try (PreparedStatement statement = connection.prepareStatement(stringBuilder.toString())) {
                int currentIndex = 0;
                for (int specificInteger : integerData) {
                    currentIndex++;
                    statement.setInt(currentIndex, specificInteger);
                }
                for (String specificString : stringData) {
                    currentIndex++;
                    statement.setString(currentIndex, specificString);
                }
                for (UUID specificUUID : uuidData) {
                    currentIndex++;
                    statement.setString(currentIndex, specificUUID.toString());
                }
                currentIndex++;
                statement.setLong(currentIndex, writeID);

                statement.execute();
                statement.close();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        });
        return writeFuture;
    }
    private String TableNameFromChunk(Chunk chunk) {
        final String worldUID = chunk.getWorld().getUID().toString().replace('-', '_');
        return ("chunk_" + chunk.getChunkKey() + "_" + worldUID).replace('-', '_');
    }

    public CompletableFuture<Boolean> TableExistsOffChunk(Chunk chunk) {
        return TableExists(TableNameFromChunk(chunk));
    }
}
