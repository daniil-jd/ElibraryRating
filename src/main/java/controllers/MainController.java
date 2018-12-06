package controllers;

import dao.TeacherDAO;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import model.RatingModel;
import model.TeacherModel;

import java.io.IOException;
import java.rmi.server.ExportException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainController {

    @FXML
    private TabPane mainTabPane;

    @FXML
    private Tab srcDbTab;

    @FXML
    private TableView<TeacherModel> dbTable = new TableView<>();

    @FXML
    private TableColumn<TeacherModel, Integer> x1Column;

    @FXML
    private TableColumn<TeacherModel, Integer> x2Column;

    @FXML
    private TableColumn<TeacherModel, Integer> x3Column;

    @FXML
    private TableColumn<TeacherModel, Integer> x4Column;

    @FXML
    private TableColumn numberCol = new TableColumn();

    @FXML
    private ComboBox<String> depChooser = new ComboBox<>();

    @FXML
    private TextField mas1;

    @FXML
    private TextField mas2;

    @FXML
    private TextField mas3;

    @FXML
    private  TextField mas4;

    @FXML
    private ComboBox<String> ratingChooser;

    @FXML
    private TableView<TeacherModel> tkTable = new TableView<>();

    @FXML
    private TableColumn numberTkCol = new TableColumn();

    @FXML
    private TableColumn<TeacherModel, Double> ratingTkCol;

    @FXML
    private ComboBox<String> tkChooser = new ComboBox<>();

    @FXML
    private Label borderLabel;

    @FXML
    private TableView<RatingModel> rating1Table = new TableView<>();

    @FXML
    private TableColumn numberRatingCol = new TableColumn();

    @FXML
    private TableColumn<RatingModel, Double> borderRatingCol;

    @FXML
    private TableColumn<RatingModel, Double> ratRatingCol;

    //----Ведение БД------------------------------------------------------------------------------------
    @FXML
    private TextField dbFioFiled;

    @FXML
    private ComboBox<String> dbExistDepBox;

    @FXML
    private TextField dbNewDepField;

    @FXML
    private TextField dbX1Field;

    @FXML
    private TextField dbX2Field;

    @FXML
    private TextField dbX3Field;

    @FXML
    private TextField dbX4Field;

    @FXML
    private ListView<TeacherModel> dbView = new ListView<>();

    private TeacherDAO teacherDAO;

    /**
     * Порог для рассчета рейтинга кафедры.
     */
    private double border = 0.0;

    @FXML
    private void initialize() {
        teacherDAO = new TeacherDAO();
        List<String> ratings = new ArrayList<>();
        ratings.add("Интегральная");
        ratingChooser.setItems(FXCollections.observableArrayList(ratings));

        numberCol.setSortType(TableColumn.SortType.ASCENDING);

        dbTable.sortPolicyProperty().set(
                new Callback<TableView<TeacherModel>, Boolean>() {
                    @Override
                    public Boolean call(TableView<TeacherModel> param) {
                        Comparator<TeacherModel> comparator = new Comparator<TeacherModel>() {
                            @Override
                            public int compare(TeacherModel r1, TeacherModel r2) {
                                if (r1.getRating() > r2.getRating()) {
                                    return 0;
                                } else if (r1.getRating() <= r2.getRating()) {
                                    return 1;
                                }
                                return 1;
                            }
                        };
                        FXCollections.sort(dbTable.getItems(), comparator);
                        return true;
                    }
                });

        tkTable.sortPolicyProperty().set(
                new Callback<TableView<TeacherModel>, Boolean>() {
                    @Override
                    public Boolean call(TableView<TeacherModel> param) {
                        Comparator<TeacherModel> comparator = new Comparator<TeacherModel>() {
                            @Override
                            public int compare(TeacherModel r1, TeacherModel r2) {
                                if (r1.getRating() > r2.getRating()) {
                                    return 0;
                                } else if (r1.getRating() <= r2.getRating()) {
                                    return 1;
                                }
                                return 1;
                            }
                        };
                        FXCollections.sort(tkTable.getItems(), comparator);
                        return true;
                    }
                });

        rating1Table.sortPolicyProperty().set(
                new Callback<TableView<RatingModel>, Boolean>() {
                    @Override
                    public Boolean call(TableView<RatingModel> param) {
                        Comparator<RatingModel> comparator = new Comparator<RatingModel>() {
                            @Override
                            public int compare(RatingModel r1, RatingModel r2) {
                                if (r1.getRating() > r2.getRating()) {
                                    return 0;
                                } else if (r1.getRating() <= r2.getRating()) {
                                    return 1;
                                }
                                return 1;
                            }
                        };
                        FXCollections.sort(rating1Table.getItems(), comparator);
                        return true;
                    }
                });
    }

    /**
     * Изменение секции в табах дб.
     * @param event событие
     */
    public void dbSectionChanged(Event event) throws IOException, SQLException {
        if (teacherDAO == null)
            teacherDAO = new TeacherDAO();

        ObservableList<TeacherModel> teachers
                = FXCollections.observableArrayList(teacherDAO.getAll());
        setNumberTeacherTableView();
        depChooser.setItems(FXCollections.observableArrayList(teacherDAO.getAllDep(true)));
        setTeachersTable(teachers);
    }

    /**
     * Событие выбора кафедры.
     * @param actionEvent не используется
     */
    public void onDepChoose(ActionEvent actionEvent)
            throws IOException, SQLException {
        String selectedDep = depChooser.getSelectionModel().getSelectedItem();
        ObservableList<TeacherModel> teachers = null;

        if ("ВСЕ".equals(selectedDep)) {
            teachers = FXCollections.observableArrayList(
                    teacherDAO.getAll());
        } else {
            teachers = FXCollections.observableArrayList(
                    teacherDAO.getByDep(selectedDep));
        }
        setNumberTeacherTableView();

        setTeachersTable(teachers);
    }

    /**
     * Событие выбора способа выставления рейтинга.
     * @param actionEvent не используется
     */
    public void onRatingChooser(ActionEvent actionEvent)
            throws IOException, SQLException {

    }

    /**
     * Подсчет рейтинга преподавателя.
     * @param actionEvent не используется
     */
    public void onCalculate(ActionEvent actionEvent)
            throws IOException, SQLException {
        double m1 = 1, m2 = 1, m3 = 1, m4 = 1;

        try {
            m1 = Double.parseDouble(mas1.getText());
        } catch (NumberFormatException e) {
//            return;
        }
        try {
            m2 = Double.parseDouble(mas2.getText());
        } catch (NumberFormatException e) {
//            return;
        }
        try {
            m3 = Double.parseDouble(mas3.getText());
        } catch (NumberFormatException e) {
//            return;
        }
        try {
            m4 = Double.parseDouble(mas4.getText());
        } catch (NumberFormatException e) {
//            return;
        }

        if ("Интегральная".equals(
                ratingChooser.getSelectionModel().getSelectedItem())) {
            integrateCalculate(m1, m2, m3, m4);
        } else if (ratingChooser.getSelectionModel().getSelectedItem() == null) {
            integrateCalculate(m1, m2, m3, m4);
        }

        depChooser.getSelectionModel().select("ВСЕ");

        setTeachersTable((FXCollections
                .observableArrayList(teacherDAO.getAll())));
    }

    /**
     * Расчет интегральной оценки преподавателей.
     * @param m1 вес 1
     * @param m2 вес 2
     * @param m3 вес 3
     * @param m4 вес 4
     */
    private void integrateCalculate(double m1, double m2,
                                    double m3, double m4)
            throws IOException, SQLException {
        if (teacherDAO == null) {
            teacherDAO = new TeacherDAO();
        }
        for (TeacherModel teacher : teacherDAO.getAll()) {
            double res = teacher.getX1() * m1 + teacher.getX2() * m2
                    + teacher.getX3() * m3 + teacher.getX4() * m4;
            teacher.setRating(res);
            teacherDAO.update(teacher);
        }
    }

    /**
     * Обновление таблицы с преподавателями.
     * @param teachers преподаватели
     */
    private void setTeachersTable(ObservableList<TeacherModel> teachers) {
        dbTable.setItems(teachers);
        dbTable.sortPolicyProperty().set(
                new Callback<TableView<TeacherModel>, Boolean>() {
                    @Override
                    public Boolean call(TableView<TeacherModel> param) {
                        Comparator<TeacherModel> comparator = new Comparator<TeacherModel>() {
                            @Override
                            public int compare(TeacherModel r1, TeacherModel r2) {
                                if (r1.getRating() > r2.getRating()) {
                                    return 0;
                                } else if (r1.getRating() <= r2.getRating()) {
                                    return 1;
                                }
                                return 1;
                            }
                        };
                        FXCollections.sort(dbTable.getItems(), comparator);
                        return true;
                    }
                });
        dbTable.sort();
        dbTable.refresh();
    }

    /**
     * Установливает номер по порядку в таблице преподавателей.
     */
    private void setNumberTeacherTableView() {

        numberCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TeacherModel, String>, ObservableValue<String>>() {
            @Override public ObservableValue<String> call(TableColumn.CellDataFeatures<TeacherModel, String> p) {
                return new ReadOnlyObjectWrapper((dbTable.getItems().indexOf(p.getValue()) + 1) + "");
            }
        });
        numberCol.setSortable(false);
    }


    //---------Кафедры------------------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * Обработчик события переключения на таб кафедр.
     * @param event не используется
     * @throws IOException ошибка чтения конфигов
     * @throws SQLException ошибка базы
     */
    public void onDepRating(Event event) throws IOException, SQLException {
        onDepRating1(event);
    }

    /**
     * Обработчик события переключения на таб "Рейтинг 1".
     * @param event не используется
     * @throws IOException ошибка чтения конфигов
     * @throws SQLException ошибка базы
     */
    public void onDepRating1(Event event) throws IOException, SQLException {
        ObservableList<TeacherModel> teachers
                = FXCollections.observableArrayList(teacherDAO.getAll());
        setNumberTkTableView();
        tkChooser.setItems(FXCollections.observableArrayList(teacherDAO.getAllDep(false)));

        Callback factory = new Callback<TableColumn<RatingModel, Double>, TableCell<RatingModel, Double>>() {
            @Override
            public TableCell<RatingModel, Double> call(TableColumn<RatingModel, Double> param) {
                return new TableCell<RatingModel, Double>() {
                    @Override
                    protected void updateItem(Double item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(String.format("%.3f", item));
                        }
                    }
                };
            }
        };
        borderRatingCol.setCellFactory(factory);
        ratRatingCol.setCellFactory(factory);
        setTkTable(teachers);
        setRatingDepTable();
    }

    /**
     * Обработка выбора кафедры для таблицы преп-каф.
     * @param actionEvent не используется
     */
    public void onTkChoose(ActionEvent actionEvent)
            throws IOException, SQLException {
        String selectedDep = tkChooser.getSelectionModel().getSelectedItem();
        ObservableList<TeacherModel> teachers
                = FXCollections.observableArrayList(
                        teacherDAO.getByDep(selectedDep));

        setNumberTkTableView();
        setTkTable(teachers);
        //вычисление порога как срзнач оценок преподавателей
        border = teacherDAO.getRatingSumByDep(selectedDep) / teachers.size();
        borderLabel.setText("Порог: " + String.format("%.3f", border));

        //изменение цвета ячеек "Рейтинг"
        Callback factory = new Callback<TableColumn<TeacherModel, Double>, TableCell<TeacherModel, Double>>() {
            @Override
            public TableCell<TeacherModel, Double> call(TableColumn<TeacherModel, Double> param) {
                return new TableCell<TeacherModel, Double>() {

                    @Override
                    protected void updateItem(Double item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(String.format("%.1f", item));
                            if (border > item) {
                                this.setStyle("-fx-text-fill: " + "red" + ";");
                            } else {
                                this.setStyle("-fx-text-fill: " + "black" + ";");
                            }
                        }
                    }
                };
            }
        };
        ratingTkCol.setCellFactory(factory);
    }


    /**
     * Установливает номер по порядку в таблице преподавателей-кафедр.
     */
    private void setNumberTkTableView() {

        numberTkCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TeacherModel, String>, ObservableValue<String>>() {
            @Override public ObservableValue<String> call(TableColumn.CellDataFeatures<TeacherModel, String> p) {
                return new ReadOnlyObjectWrapper((tkTable.getItems().indexOf(p.getValue()) + 1) + "");
            }
        });
        numberTkCol.setSortable(false);
    }

    /**
     * Обновление таблицы с преподавателями-кафедрами.
     * @param teachers преподаватели
     */
    private void setTkTable(ObservableList<TeacherModel> teachers) {
        tkTable.setItems(teachers);
        tkTable.sortPolicyProperty().set(
                new Callback<TableView<TeacherModel>, Boolean>() {
                    @Override
                    public Boolean call(TableView<TeacherModel> param) {
                        Comparator<TeacherModel> comparator = new Comparator<TeacherModel>() {
                            @Override
                            public int compare(TeacherModel r1, TeacherModel r2) {
                                if (r1.getRating() > r2.getRating()) {
                                    return 0;
                                } else if (r1.getRating() <= r2.getRating()) {
                                    return 1;
                                }
                                return 1;
                            }
                        };
                        FXCollections.sort(tkTable.getItems(), comparator);
                        return true;
                    }
                });
        tkTable.refresh();
    }

    /**
     * Рассчитать рейтинг кафедр.
     */
    private void setRatingDepTable()
            throws IOException, SQLException {
        List<String> deps = teacherDAO.getAllDep(false);
        List<RatingModel> ratingsDep = new ArrayList<>();

        for (int i = 0; i < deps.size(); i++) {
            double sum = teacherDAO.getRatingSumByDep(deps.get(i));
            int size = teacherDAO.getByDep(deps.get(i)).size();
            double border = sum / size;
            int active = teacherDAO
                    .getCountOfActiveTeachers(deps.get(i), border);
            double rating = ((double)active / size) * sum;
            ratingsDep.add(new RatingModel(deps.get(i), sum, border, rating));
        }
        setRating1Table(FXCollections.observableArrayList(ratingsDep));
    }

    /**
     * Установливает номер по порядку в таблице рейтинга кафедр.
     */
    private void setNumberRating1TableView() {

        numberRatingCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<RatingModel, String>, ObservableValue<String>>() {
            @Override public ObservableValue<String> call(TableColumn.CellDataFeatures<RatingModel, String> p) {
                return new ReadOnlyObjectWrapper((rating1Table.getItems().indexOf(p.getValue()) + 1) + "");
            }
        });
        numberRatingCol.setSortable(false);
    }

    /**
     * Обновление таблицы с рейтингом кафедр.
     * @param ratings рейтинги кафедр
     */
    private void setRating1Table(ObservableList<RatingModel> ratings) {
        rating1Table.setItems(ratings);
        setNumberRating1TableView();
        rating1Table.refresh();
    }


    //---------Ведение ДБ-----------------------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * Событие переключения на раздел ведения бд.
     * @param event не используется
     */
    public void onDbSection(Event event)
            throws IOException, SQLException {
        if (teacherDAO == null)
            teacherDAO = new TeacherDAO();

        dbExistDepBox.setItems(
                FXCollections.observableArrayList(teacherDAO.getAllDep(false)));

        List<TeacherModel> teachers
                = teacherDAO.getAll();

        dbView.setItems(FXCollections.observableArrayList(teachers));
        dbView.refresh();
    }

    /**
     * Создание/изменение записи в БД.
     * @param actionEvent не используется
     */
    public void onDbCreateOrUpdate(ActionEvent actionEvent)
            throws IOException, SQLException {
        if (dbFioFiled.getText().equals("")
                || (dbExistDepBox.getSelectionModel().getSelectedItem() == null
                && dbNewDepField.getText().equals(""))) {
            showAlertWithHeaderText("Не введены данные",
                    "Не введены данные",
                    "Введите обязательные данные: ФИО, кафедра");
            return;
        }
        int x1 = 0, x2 = 0, x3 = 0, x4 = 0;
        try {
            x1 = Integer.parseInt(dbX1Field.getText());
        } catch (NumberFormatException e) {
        }
        try {
            x2 = Integer.parseInt(dbX2Field.getText());
        } catch (NumberFormatException e) {
        }
        try {
            x3 = Integer.parseInt(dbX3Field.getText());
        } catch (NumberFormatException e) {
        }
        try {
            x4 = Integer.parseInt(dbX4Field.getText());
        } catch (NumberFormatException e) {
        }
        String dep = null;
        if (!dbNewDepField.getText().equals("")) {
            dep = dbNewDepField.getText();
        } else {
            dep = dbExistDepBox.getSelectionModel().getSelectedItem();
        }
        TeacherModel teacher
                = new TeacherModel(dbFioFiled.getText(),
                dep, x1, x2, x3, x4, 0.0);

        if (teacherDAO.getByName(teacher.getName()) != null) {
            //update
            if (teacherDAO.update(teacher) != 1) {
                throw new ExportException("Ведение бд: запись не добавлена/добавлена некорректно."
                        + "\n"
                        + "Преподаватель: " + teacher);
            }
        } else {
            //create
            if (teacherDAO.insert(teacher)) {
                throw new ExportException("Ведение бд: запись не добавлена/добавлена некорректно."
                        + "\n"
                        + "Преподаватель: " + teacher);
            }
        }

        onDbSection(actionEvent);
    }

    /**
     * Сообщение об ошибке ввода данных для создания записи в бд.
     * @param title титул
     * @param header хэдер
     * @param text текст
     */
    private void showAlertWithHeaderText(String title,
                                         String header,
                                         String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);

        alert.showAndWait();
    }

    /**
     * Изменение секции в табах дб.
     * @param actionEvent не используется
     */
    public void onDbDepChooser(ActionEvent actionEvent)
            throws IOException, SQLException {
        //?
    }


    /**
     * Удаление выбранного в лист вью преподавателя.
     * @param actionEvent не используется
     */
    public void onDbDeleteTeacher(ActionEvent actionEvent)
            throws IOException, SQLException {
        TeacherModel teacher = dbView.getSelectionModel().getSelectedItem();
        if (teacher != null) {
            if (teacherDAO.delete(teacher)) {
                throw new ExportException("Ведение бд: не удается удалить запись."
                        + "\n"
                        + "Преподаватель: " + teacher);
            }
        }
        onDbSection(actionEvent);
    }
}
