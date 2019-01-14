package dao;

import model.TeacherModel;
import org.h2.jdbcx.JdbcDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class TeacherDAO {

    private Connection connection;

    private final String SQL_SELECT_ALL = "SELECT * FROM TEACHER";

    private final String SQL_SELECT = "SELECT * FROM TEACHER WHERE name = ?";

    private final String SQL_UPDATE
            = "UPDATE TEACHER SET DEP = ?, X1 = ?, X2 = ?, X3 = ?, X4 = ?, RATING = ? WHERE name = ?";

    private final String SQL_INSERT = "INSERT INTO TEACHER (name, DEP, X1, X2, X3, X4, RATING) VALUES (?,?,?,?,?,?,?)";

    //language=SQL
    private final String SQL_DELETE = "DELETE FROM TEACHER WHERE name = ?";

    //language=SQL
    private final String SQL_SELECT_BY_DEP = "SELECT * FROM TEACHER WHERE DEP = ?";

    private JdbcDataSource setDbSettings() throws IOException {
        JdbcDataSource dataSource = new JdbcDataSource();
        Properties properties = new Properties();

        ClassLoader cl = this.getClass().getClassLoader();
        InputStream stream = cl.getResourceAsStream("db.properties");
        properties.load(stream);

        dataSource.setURL(properties.getProperty("db.url"));
        dataSource.setUser(properties.getProperty("db.username"));

        return dataSource;
    }

    public boolean insert(TeacherModel teacher) throws SQLException, IOException {
        PreparedStatement statement = reconnect().prepareStatement(SQL_INSERT);
        statement.setString(1, teacher.getName());
        statement.setString(2, teacher.getDep());
        statement.setInt(3, teacher.getX1());
        statement.setInt(4, teacher.getX2());
        statement.setInt(5, teacher.getX3());
        statement.setInt(6, teacher.getX4());
        statement.setDouble(7, teacher.getRating());


        boolean res = statement.execute();
        statement.close();
        close();
        return res;
    }

    public List<TeacherModel> getAll() throws SQLException, IOException {
        PreparedStatement statement = reconnect().prepareStatement(SQL_SELECT_ALL);
        ResultSet resultSet = statement.executeQuery();

        List<TeacherModel> teachers = new ArrayList<TeacherModel>();
        while (resultSet.next()) {
            TeacherModel teacher = new TeacherModel(
                    resultSet.getString(6),
                    resultSet.getString(1),
                    resultSet.getInt(2),
                    resultSet.getInt(3),
                    resultSet.getInt(4),
                    resultSet.getInt(5),
                    resultSet.getDouble(7)
                    );
            teachers.add(teacher);
        }
        statement.close();
        close();
        return teachers;
    }

    public TeacherModel getByName(String name) throws SQLException, IOException {
        PreparedStatement statement = reconnect().prepareStatement(SQL_SELECT);
        statement.setString(1, name);
        ResultSet resultSet = statement.executeQuery();

        TeacherModel teacher = null;
        if (resultSet.next()) {
            teacher = new TeacherModel(
                    resultSet.getString(6),
                    resultSet.getString(1),
                    resultSet.getInt(2),
                    resultSet.getInt(3),
                    resultSet.getInt(4),
                    resultSet.getInt(5),
                    resultSet.getDouble(7)
            );
        }
        statement.close();
        close();
        return teacher;
    }

    public int update(TeacherModel teacher) throws SQLException, IOException {
        PreparedStatement statement = reconnect().prepareStatement(SQL_UPDATE);
        statement.setString(1, teacher.getDep());
        statement.setInt(2, teacher.getX1());
        statement.setInt(3, teacher.getX2());
        statement.setInt(4, teacher.getX3());
        statement.setInt(5, teacher.getX4());
        statement.setDouble(6, teacher.getRating());
        statement.setString(7, teacher.getName());
        int res = statement.executeUpdate();
        statement.close();
        close();
        getAll();
        return res;
    }

    public boolean delete(TeacherModel teacher) throws IOException, SQLException {
        PreparedStatement statement = reconnect().prepareStatement(SQL_DELETE);
        statement.setString(1, teacher.getName());

        boolean res = statement.execute();
        statement.close();
        close();
        getAll();
        return res;
    }


    /**
     * Находит уникальные кафедры университета.
     * @param isTeachers true - добавляется "все"
     * @return уникальные кафедры
     * @throws IOException если не нашелся файл настройки бд
     * @throws SQLException если бд не работает/подключилась
     */
    public List<String> getAllDep(boolean isTeachers)
            throws IOException, SQLException {
        List<TeacherModel> teachers = getAll();
        HashMap<String, String> uniqueDeps = new HashMap<>();
        for (TeacherModel teacher : teachers) {
            uniqueDeps.put(teacher.getDep(), teacher.getDep());
        }
        List<String> deps = new ArrayList<>();
        for (String dep : uniqueDeps.values()) {
            deps.add(dep);
        }
        if (isTeachers) {
            deps.add("ВСЕ");
        }
        return deps;
    }

    /**
     * SELECT по кафедре.
     * @param dep кафедра
     * @return списко преподавателей
     * @throws IOException если не нашелся файл настройки бд
     * @throws SQLException если бд не работает/подключилась
     */
    public List<TeacherModel> getByDep(String dep)
            throws SQLException, IOException {
        PreparedStatement statement = reconnect()
                .prepareStatement(SQL_SELECT_BY_DEP);
        statement.setString(1, dep);
        ResultSet resultSet = statement.executeQuery();

        List<TeacherModel> teachers = new ArrayList<>();
        while (resultSet.next()) {
            TeacherModel teacher = new TeacherModel(
                    resultSet.getString(6),
                    resultSet.getString(1),
                    resultSet.getInt(2),
                    resultSet.getInt(3),
                    resultSet.getInt(4),
                    resultSet.getInt(5),
                    resultSet.getDouble(7)
            );
            teachers.add(teacher);
        }
        statement.close();
        close();
        return teachers;
    }

    /**
     * Возвращает сумму рейтингов по кафедре.
     * @param dep кафедра
     * @return сумма
     * @throws IOException если не нашелся файл настройки бд
     * @throws SQLException если бд не работает/подключилась
     */
    public double getRatingSumByDep(String dep)
            throws IOException, SQLException {
        List<TeacherModel> teachers = getByDep(dep);
        double sum = 0.0;
        for (TeacherModel teacher : teachers) {
            sum += teacher.getRating();
        }
        return sum;
    }

    /**
     * Возвращает количество преподавателей кафедры, у которых рейтинг выше порога.
     * @param dep кафедра
     * @param border порог
     * @return количество преподавателей
     * @throws IOException если не нашелся файл настройки бд
     * @throws SQLException если бд не работает/подключилась
     */
    public int getCountOfActiveTeachers(String dep, double border)
            throws IOException, SQLException {
        List<TeacherModel> teachers = getByDep(dep);
        int count = 0;
        for (TeacherModel teacher : teachers) {
            if (teacher.getRating() >= border) {
                count++;
            }
        }
        return count;
    }

    public void close() throws SQLException {
        connection.close();
    }

    private Connection reconnect() throws SQLException, IOException {
        if (connection != null) {
            close();
            connection = null;
        }
        connection = setDbSettings().getConnection();
        return connection;
    }
}
