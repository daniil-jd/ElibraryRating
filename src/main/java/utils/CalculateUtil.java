package utils;

import dao.TeacherDAO;
import model.Rating2Model;
import model.TeacherModel;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
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

    /**
     * Вычисляет рейтинг кафедры на основе средней оценки по ВУЗу.
     * @return рейинг
     * @throws IOException ошибка чтения файлов из конфига
     * @throws SQLException ошибка подключения к базе
     */
    public static List<Rating2Model> calculateRatingDep2()
            throws IOException, SQLException {
        List<Rating2Model> ratings = new ArrayList<>();
        double average = getAverageOnDepRating();
        for (String dep : teacherDAO.getAllDep(false)) {
            double rating = getRatingDep(teacherDAO.getByDep(dep));
            String mark = getMarkOnRating(average, rating);
            ratings.add(new Rating2Model(dep, average, rating, mark));
        }
        return ratings;
    }

    /**
     * Подсчитывает среднюю оценку рейтинга кафедр.
     * @return средняя оценка
     * @throws IOException ошибка чтения файлов из конфига
     * @throws SQLException ошибка подключения к базе
     */
    public static double getAverageOnDepRating() throws IOException, SQLException {
        double average = 0;
        int count = 0;
        for (String dep : teacherDAO.getAllDep(false)) {
            average += getRatingDep(teacherDAO.getByDep(dep));
            count++;
        }
        average = average / count;
        return average;
    }

    /**
     * Подсчет суммы рейтингов преподов на кафедре.
     * @param teachers преподы
     * @return сумма
     */
    private static double getRatingDep(List<TeacherModel> teachers) {
        double sum = 0;
        for (TeacherModel teacher : teachers) {
            sum += teacher.getRating();
        }
        return sum;
    }

    /**
     * Возвращает оценку кафедры на основе среднего значения.
     * @param average среднее
     * @param rating индивидуальный рейтинг
     * @return оценка
     */
    public static String getMarkOnRating(double average, double rating) {
        if (rating < 0.5 * average) {
            return "Низкий";
        } else if (rating < average && 0.5 * average < rating) {
            return "Ниже среднего";
        } else if (average < rating && rating < 1.5 * average) {
            return "Выше среднего";
        } else if (1.5 * average < rating) {
            return "Высокий";
        }
        return "--";
    }
}
