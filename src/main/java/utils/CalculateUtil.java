package utils;

import dao.TeacherDAO;
import model.TeacherModel;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Administrator on 08.12.2018.
 */
public class CalculateUtil {

    private static TeacherDAO teacherDAO = new TeacherDAO();

    /**
     * Рассчет рейтинга преподавателя по методу Макс знач показателя
     * @throws IOException если не может найти файл с параметрами
     * @throws SQLException если не может найти бд
     */
    public static void maxAvailableRating()
            throws IOException, SQLException {
        List<TeacherModel> teachers = teacherDAO.getAll();
        int x1 = 0, x2 = 0, x3 = 0, x4 = 0;
        //max
        for (TeacherModel teacher : teachers) {
            if (x1 < teacher.getX1()) {
                x1 = teacher.getX1();
            }
            if (x2 < teacher.getX2()) {
                x2 = teacher.getX2();
            }
            if (x3 < teacher.getX3()) {
                x3 = teacher.getX3();
            }
            if (x4 < teacher.getX4()) {
                x4 = teacher.getX4();
            }
        }

        //расчет оценки по формуле a = sum(xi/max_xi) / 4
        for (int i = 0; i < teachers.size(); i++) {
            teachers.get(i).setRating(
                    (((double) teachers.get(i).getX1() / x1)
                            + ((double) teachers.get(i).getX2() / x2)
                            + ((double) teachers.get(i).getX3() / x3)
                            + ((double) teachers.get(i).getX4() / x4)) / 4);
            teacherDAO.update(teachers.get(i));
        }
    }
}
