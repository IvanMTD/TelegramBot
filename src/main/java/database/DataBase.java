package database;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBase extends BaseService {

    private static DataBase instance = null;

    public DataBase(String dbDriver, String dbUrl) {
        super(dbDriver, dbUrl);
    }

    public String sqlCommandLine(String SQL_Command){
        openConnection();
        String executionMessage = executeCommand(SQL_Command);
        closeConnection();
        return executionMessage;
    }

    public File sqlCommandLineF(String SQL_Command){
        openConnection();
        File file = executeCommandF(SQL_Command);
        closeConnection();
        return file;
    }

    private File executeCommandF(String SQL_Command){
        Statement statement = getStatement();
        File file = new File("./tmp/image.png");
        try {
            if(statement.execute(SQL_Command)){
                ResultSet resultSet = statement.getResultSet();
                while (resultSet.next()) {
                    InputStream data = resultSet.getBinaryStream("image");
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] buffer = new byte[1];
                    while (data.read(buffer) > 0) {
                        fos.write(buffer);
                    }
                    fos.close();
                }
            }
        } catch (SQLException | FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("The command cannot be executed");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    private String executeCommand(String SQL_Command){
        Statement statement = getStatement();
        StringBuilder executionMessage = new StringBuilder();
        try {
            if(statement.execute(SQL_Command)){
                ResultSet resultSet = statement.getResultSet();
                int columns = resultSet.getMetaData().getColumnCount();
                while (resultSet.next()){
                    for (int i = 1; i <= columns; i++){
                        executionMessage.append(resultSet.getString(i)).append(" ");
                    }
                    executionMessage.append("\n");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("The command cannot be executed");
        }

        return executionMessage.toString();
    }

    public static DataBase getInstance() {
        if(instance == null){
            instance = new DataBase("org.h2.Driver","jdbc:h2:./db/database");
        }
        return instance;
    }
}
