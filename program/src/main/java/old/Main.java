package old;

import java.sql.*;
import java.util.Calendar;

public class Main {
    private static final String PROTOCOL = "jdbc:postgresql://";        // URL-prefix
    private static final String DRIVER = "org.postgresql.Driver";       // Driver name
    private static final String URL_LOCALE_NAME = "localhost/";         // ваш компьютер + порт по умолчанию
    // мой IP на ноуте 172.18.64.1
    private static final String URL_REMOTE = "10.242.65.114:5050/";     // IP-адрес кафедрального сервера + явно порт (по умолчанию)

    private static final String DATABASE_NAME = "science_center";          // FIXME имя базы

    public static final String DATABASE_URL = PROTOCOL + URL_LOCALE_NAME + DATABASE_NAME;
    public static final String USER_NAME = "postgres";                  // FIXME имя пользователя
    public static final String DATABASE_PASS = "postgres";              // FIXME пароль базы данных

    public static void main(String[] args) {
        // создаём объект класса Calendar
        // он пригодится позже при задании дат
        Calendar calendar = Calendar.getInstance();

        /*
        Hibernate - на след. неделе

        Плюс балл:
        Реализовать интерфейс (JavaFX/Swing)
        Реализовать ORM - ПО для работы с БД из кода
         */


        // проверка возможности подключения
        checkDriver();
        checkDB();
        System.out.println("Подключение к базе данных | " + DATABASE_URL + "\n");

        // попытка открыть соединение с базой данных, которое java-закроет перед выходом из try-with-resources
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
            if (false) {
                // SELECT запросы
                getTypeAndModelByMinPrice(connection, 50000);
                // задаем год, месяц и день даты
                calendar.set(2022, Calendar.MARCH, 27);
                Date minDate = (Date) calendar.getTime();

                getProjectAndDeviceNamesByMinStartDate(connection, minDate);
                getEmployeesByGroupNum(connection, 1);

                calendar.set(2023, Calendar.AUGUST, 4);
                minDate = (Date) calendar.getTime();
                calendar.set(2024, Calendar.APRIL, 1);
                Date maxDate = (Date) calendar.getTime();
                getOpHistoryByDates(connection, minDate, maxDate);
                getEmployeesByMaxID(connection, 6);

                // SET запрос
                calendar.set(2021, Calendar.APRIL, 14);
                Date date = (Date) calendar.getTime();
                setStatusChange(connection, date, "В работе", 2, 4);

                // DELETE запрос
                deleteEducationByTraining(connection, "Юридическое");

                // UPDATE запросы
                moveEmployeeToAnotherGroup(connection, "Илья", "Попов", "Гаврилович", 5);
                changeEmployeePaymentByGroupFunction(connection, 20, "Исследования в области физ. и эл.");

                // SELECT запросы
                getProjectsWithDates(connection);
                getStepsWithDates(connection);
                getOperationsWithDates(connection);
                getStepsDistributionGroups(connection);
                getOperationsDistributionEmployees(connection);
            }
        } catch (SQLException e) {
            // При открытии соединения, выполнении запросов могут возникать различные ошибки
            // Согласно стандарту SQL:2008 в ситуациях нарушения ограничений уникальности (в т.ч. дублирования данных) возникают ошибки соответствующие статусу (или дочерние ему): SQLState 23000 - Integrity Constraint Violation
            if (e.getSQLState().startsWith("23")) {
                System.out.println("Произошло дублирование данных");
            } else throw new RuntimeException(e);
        }
    }

    private static void getTypeAndModelByMinPrice(Connection connection, Integer minPrice) throws SQLException {
        String columnName0 = "type code", columnName1 = "model code";

        // значения ячеек
        Integer param0 = null, param1 = null;

        PreparedStatement statement = connection.prepareStatement(
                "SELECT t_code AS \"type code\", m_code AS \"model code\" FROM device\n" +
                        "WHERE price >= ?"
        );
        statement.setInt(1, minPrice);

        ResultSet rs = statement.executeQuery(); // выполняем запроса на поиск и получаем список ответов

        while (rs.next()) {  // пока есть данные, продвигаться по ним
            param0 = rs.getInt(columnName0); // значение ячейки, можно получить по имени; по умолчанию возвращается строка
            param1 = rs.getInt(columnName1); // если точно уверены в типе данных ячейки, можно его сразу преобразовать
            System.out.println(param0 + " | " + param1);
        }
    }

    private static void getProjectAndDeviceNamesByMinStartDate(Connection connection, Date minDate) throws SQLException {
        String columnName0 = "project name", columnName1 = "device name", columnName2 = "project start date";

        String param0 = null, param1 = null;
        Date param2 = null;

        PreparedStatement statement = connection.prepareStatement(
                "SELECT project.pr_name AS \"project name\", device.dev_name AS \"device name\", " +
                        "project.s_date AS \"project start date\"\n" +
                        "FROM project\n" +
                        "INNER JOIN device\n" +
                        "ON device.pr_id = project.id\n" +
                        "WHERE project.s_date > ?\n"
        );
        statement.setDate(1, minDate);

        ResultSet rs = statement.executeQuery(); // выполняем запроса на поиск и получаем список ответов

        while (rs.next()) {
            param0 = rs.getString(columnName0);
            param1 = rs.getString(columnName1);
            param2 = rs.getDate(columnName2);
            System.out.println(param0 + " | " + param1 + " | " + param2);
        }
    }

    private static void getEmployeesByGroupNum(Connection connection, int groupNum) throws SQLException {
        String columnName0 = "name", columnName1 = "surname", columnName2 = "patronymic";

        String param0 = null, param1 = null, param2 = null;

        PreparedStatement statement = connection.prepareStatement(
                "SELECT employee.empl_name AS \"name\", employee.empl_surname AS \"surname\", " +
                        "employee.empl_patronymic AS \"patronymic\"\n" +
                        "FROM employee\n" +
                        "INNER JOIN empl_group\n" +
                        "ON employee.gr_id = empl_group.id\n" +
                        "WHERE empl_group.id = ?\n");
        statement.setInt(1, groupNum);

        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            param0 = rs.getString(columnName0);
            param1 = rs.getString(columnName1);
            param2 = rs.getString(columnName2);
            System.out.println(param0 + " | " + param1 + " | " + param2);
        }
    }

    private static void getOpHistoryByDates(Connection connection, Date minDate, Date maxDate) throws SQLException {
        String columnName0 = "operation name", columnName1 = "id", columnName2 = "change date", columnName3 = "status";

        String param0 = null, param3 = null;
        Integer param1 = null;
        Date param2 = null;

        PreparedStatement statement = connection.prepareStatement(
                "SELECT operation.op_name AS \"operation name\", operation.id, " +
                        "op_stat_change.cng_date AS \"change date\", status.value AS \"status\"\n" +
                        "FROM operation\n" +
                        "INNER JOIN op_stat_change\n" +
                        "ON operation.id = op_stat_change.op_id\n" +
                        "INNER JOIN status\n" +
                        "ON op_stat_change.stat_id = status.id\n" +
                        "WHERE operation.start_date >= ? AND operation.start_date <= ?\n");
        statement.setDate(1, minDate);
        statement.setDate(2, maxDate);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            param0 = rs.getString(columnName0);
            param1 = rs.getInt(columnName1);
            param2 = rs.getDate(columnName2);
            param3 = rs.getString(columnName3);
            System.out.println(param0 + " | " + param1 + " | " + param2 + " | " + param3);
        }
    }

    private static void getEmployeesByMaxID(Connection connection, int maxID) throws SQLException {
        String columnName0 = "name", columnName1 = "surname", columnName2 = "patronymic", columnName3 = "qualification", columnName4 = "training";

        String param0 = null, param1 = null, param2 = null, param3 = null, param4 = null;

        PreparedStatement statement = connection.prepareStatement(
                "SELECT employee.empl_name AS \"name\", employee.empl_surname AS \"surname\", employee.empl_patronymic AS \"patronymic\", " +
                        "education.qualification, education.training\n" +
                        "FROM employee\n" +
                        "INNER JOIN empl_edu_list\n" +
                        "ON employee.id = empl_edu_list.empl_id\n" +
                        "INNER JOIN education\n" +
                        "ON empl_edu_list.edu_id = education.id\n" +
                        "WHERE employee.id <= ?\n");
        statement.setInt(1, maxID);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            param0 = rs.getString(columnName0);
            param1 = rs.getString(columnName1);
            param2 = rs.getString(columnName2);
            param3 = rs.getString(columnName3);
            param4 = rs.getString(columnName4);
            System.out.println(param0 + " | " + param1 + " | " + param2 + " | " + param3 + " | " + param4);
        }
    }

    private static void setStatusChange(Connection connection, Date date, String statValue, int opID, int employeeID) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("DO $$\n" +
                "DECLARE\n" +
                "stat_id_var int;\n" +
                "BEGIN\n" +
                "SELECT status.id INTO STRICT stat_id_var FROM status WHERE status.value = ?;\n" +
                "INSERT INTO op_stat_change (cng_date, op_id, stat_id, empl_id)\n" +
                "VALUES (?, ?, stat_id_var, ?);\n" +
                "END $$;\n");
        statement.setString(1, statValue);
        statement.setDate(2, date);
        statement.setInt(3, opID);
        statement.setInt(4, employeeID);
        statement.executeUpdate();
    }

    private static void deleteEducationByTraining(Connection connection, String training) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("DO $$\n" +
                "DECLARE\n" +
                "edu_id_var int;\n" +
                "BEGIN\n" +
                "SELECT education.id INTO STRICT edu_id_var FROM education WHERE education.training = ?;\n" +
                "DELETE FROM empl_edu_list WHERE empl_edu_list.edu_id = edu_id_var;\n" +
                "DELETE FROM education WHERE education.id = edu_id_var;\n" +
                "END $$;\n");
        statement.setString(1, training);
        statement.executeUpdate();
    }

    private static void moveEmployeeToAnotherGroup(Connection connection, String name, String surname,
                                                   String patronymic, int nextGroupID) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("UPDATE employee\n" +
                "SET employee.gr_id = ?\n" +
                "WHERE employee.empl_surname = ?\n" +
                "AND employee.empl_name = ?\n" +
                "AND employee.empl_patronymic = ?;\n");
        statement.setInt(1, nextGroupID);
        statement.setString(2, surname);
        statement.setString(3, name);
        statement.setString(4, patronymic);
        statement.executeUpdate();
    }

    private static void changeEmployeePaymentByGroupFunction(Connection connection, int percent, String grPurpose) throws SQLException {
        if (percent <= -100) {
            System.out.println("Ошибка - работники не могут работать без денег");
        } else {
            PreparedStatement statement = connection.prepareStatement("UPDATE employee\n" +
                    "SET payment = payment * ?\n" +
                    "FROM empl_group\n" +
                    "WHERE empl_group.purpose = ? AND employee.gr_id = empl_group.id;\n");
            statement.setFloat(1, (float) (100 + percent) / 100);
            statement.setString(2, grPurpose);
            statement.executeUpdate();
        }
    }

    private static void getProjectsWithDates(Connection connection) throws SQLException {
        String columnName0 = "project_id", columnName1 = "project_name",
                columnName2 = "project_start_date", columnName3 = "project_end_date";

        Integer param0 = null;
        String param1 = null;
        Date param2 = null, param3 = null;

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT project.id AS project_id, project.pr_name AS project_name," +
                "project.s_date AS project_start_date, MAX(stage.end_date) AS project_end_date\n" +
                "FROM project\n" +
                "JOIN stage ON project.id = stage.pr_id\n" +
                "GROUP BY project_id, project_name, project_start_date\n" +
                "ORDER BY project_id\n");
        while (rs.next()) {
            param0 = rs.getInt(columnName0);
            param1 = rs.getString(columnName1);
            param2 = rs.getDate(columnName2);
            param3 = rs.getDate(columnName3);
            System.out.println(param0 + " | " + param1 + " | " + param2 + " | " + param3);
        }
    }

    private static void getStepsWithDates(Connection connection) throws SQLException {
        String columnName0 = "step_id", columnName1 = "step_name",
                columnName2 = "step_start_date", columnName3 = "step_end_date";

        Integer param0 = null;
        String param1 = null;
        Date param2 = null, param3 = null;

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT step.id AS step_id, step.step_name AS step_name, " +
                "step.start_date AS step_start_date, step.end_date AS step_end_date\n" +
                "FROM step\n");
        while (rs.next()) {
            param0 = rs.getInt(columnName0);
            param1 = rs.getString(columnName1);
            param2 = rs.getDate(columnName2);
            param3 = rs.getDate(columnName3);
            System.out.println(param0 + " | " + param1 + " | " + param2 + " | " + param3);
        }
    }

    private static void getOperationsWithDates(Connection connection) throws SQLException {
        String columnName0 = "operation_id", columnName1 = "operation_name",
                columnName2 = "operation_start_date", columnName3 = "operation_end_date";

        Integer param0 = null;
        String param1 = null;
        Date param2 = null, param3 = null;

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("DO $$\n" +
                "DECLARE\n" +
                "t_query text;\n" +
                "c_cursor CONSTANT refcursor := '_cursor';\n" +
                "id_completed INT;\n" +
                "BEGIN\n" +
                "SELECT status.id INTO STRICT id_completed\n" +
                "FROM status WHERE status.value = 'Выполнена';\n" +
                "t_query := 'SELECT op.id AS operation_id, op.op_name AS operation_name, op.start_date AS operation_start_date, MAX(stat_ch.cng_date) AS operation_end_date\n" +
                "FROM operation AS op\n" +
                "JOIN op_stat_change AS stat_ch ON op.id = stat_ch.op_id\n" +
                "WHERE stat_id = $1\n" +
                "GROUP BY stat_ch.op_id, operation_start_date, operation_name, operation_id;';\n" +
                "OPEN c_cursor FOR EXECUTE t_query USING id_completed;\n" +
                "END$$;\n" +
                "FETCH ALL FROM _cursor;");
        while (rs.next()) {
            param0 = rs.getInt(columnName0);
            param1 = rs.getString(columnName1);
            param2 = rs.getDate(columnName2);
            param3 = rs.getDate(columnName3);
            System.out.println(param0 + " | " + param1 + " | " + param2 + " | " + param3);
        }
    }

    private static void getStepsDistributionGroups(Connection connection) throws SQLException {
        String columnName0 = "step name", columnName1 = "group name";

        String param0 = null, param1 = null;

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT step.step_name AS \"step name\", gr.gr_name AS \"group name\"\n" +
                "FROM empl_group AS gr\n" +
                "JOIN operation AS op ON gr.id = op.gr_id\n" +
                "JOIN step ON step.id = op.step_id;");
        while (rs.next()) {
            param0 = rs.getString(columnName0);
            param1 = rs.getString(columnName1);
            System.out.println(param0 + " | " + param1);
        }
    }

    private static void getOperationsDistributionEmployees(Connection connection) throws SQLException {
        String columnName0 = "operation name", columnName1 = "id", columnName2 = "employee name",
                columnName3 = "employee surname", columnName4 = "employee patronymic";

        String param0 = null, param2 = null, param3 = null, param4 = null;
        Integer param1 = null;

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(
                "SELECT op.op_name AS \"operation name\", empl.id AS \"id\", empl.empl_name AS \"employee name\", " +
                        "empl.empl_surname AS \"employee surname\", empl.empl_patronymic AS \"employee patronymic\"\n" +
                        "FROM operation AS op\n" +
                        "JOIN op_stat_change AS stat_ch ON op.id = stat_ch.op_id\n" +
                        "JOIN employee AS empl ON stat_ch.empl_id = empl.id\n" +
                        "GROUP BY op.op_name, empl.id, empl.empl_name, empl.empl_surname, empl.empl_patronymic;");
        while (rs.next()) {
            param0 = rs.getString(columnName0);
            param1 = rs.getInt(columnName1);
            param2 = rs.getString(columnName2);
            param3 = rs.getString(columnName3);
            param4 = rs.getString(columnName4);
            System.out.println(param0 + " | " + param1 + " | " + param2 + " | " + param3 + " | " + param4);
        }
    }

    // region // Проверка окружения и доступа к базе данных
    public static void checkDriver() {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println("Нет JDBC-драйвера! Подключите JDBC-драйвер к проекту согласно инструкции.");
            throw new RuntimeException(e);
        }
    }

    public static void checkDB() {
        try {
            Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS);
        } catch (SQLException e) {
            System.out.println("Нет базы данных! Проверьте имя базы, путь к базе или разверните локально резервную копию согласно инструкции");
            throw new RuntimeException(e);
        }
    }

    // endregion
}
