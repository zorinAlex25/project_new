package old;

import old.util.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

import java.util.List;
import java.util.ArrayList;

public class DBInterface extends JFrame {
    private JTable table; // отображает результат запроса
    private JScrollPane scrollPane; // для прокрутки таблицы
    private JButton fetchButton; // кнопка для выполнения запроса
    private JButton exportButton; // кнопка для экспорта данных (таблицы)
    private JTextArea queryArea; // для ввода SQL-запроса
    private JLabel queryLabel; // заголовок (отобр. текста)
    private JComboBox<String> queryComboBox; // выводит список команд (готовых запросов к БД)

    private List<JTextField> inputFields = new ArrayList<>(); // для ввода 1-го и 2-го параметра
    private List<JLabel> inputLabels = new ArrayList<>(); // для вывода 1-го и 2-го параметра

    private JPanel inputPanel; // контейнер для размещения и группировки компонентов (полей)

    private static final List<Query> queries = getQueries();

    private String[] queryNames = getQueryNames();

    public DBInterface() {
        setTitle("Интерфейс для работы с БД");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // корректный выход из программы
        setLayout(new BorderLayout()); // для размещения компонентов
        setIconImage(new ImageIcon("icon.png").getImage()); // иконка окна

        // Панель для выбора запроса и фильтров
        JPanel topPanel = new JPanel(new GridBagLayout()); // для размещения и группировки компонентов (полей) по сетке (GridBagLayout)
        GridBagConstraints gbc = new GridBagConstraints(); // для управления расположением компонентов
        gbc.insets = new Insets(10, 10, 10, 10); // отступы

        queryLabel = new JLabel("Введите SQL-запрос:");
        queryLabel.setFont(new Font("Arial", Font.BOLD, 14)); // шрифт
        queryArea = new JTextArea(5, 30); // перенос строк и столбцов
        queryArea.setFont(new Font("Arial", Font.PLAIN, 14)); // шрифт
        queryArea.setLineWrap(true); // перенос строк
        queryArea.setWrapStyleWord(true); // перенос слов
        JScrollPane queryScrollPane = new JScrollPane(queryArea); // для прокрутки вниз


        fetchButton = new JButton("Выполнить"); // создание кнопки
        fetchButton.setFont(new Font("Arial", Font.BOLD, 14));
        fetchButton.setBackground(new Color(50, 150, 255));
        fetchButton.setForeground(Color.WHITE); // цвет текста на кнопке
        fetchButton.setOpaque(true); // непрозрачность кнопки
        fetchButton.setBorderPainted(false); // отключает рамку вокруг кнопки
        fetchButton.setFocusPainted(false); // отключает обводку фокуса
        fetchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // меняет курсор мыши при наведении
        fetchButton.setPreferredSize(new Dimension(120, 35)); // размер кнопки
        fetchButton.addActionListener(e -> executeQuery()); // добавляем слушателя для кнопки -> заставляем кнопку реагировать на нажатие

        exportButton = new JButton("Экспортировать в CSV");
        exportButton.setFont(new Font("Arial", Font.BOLD, 14));
        exportButton.setBackground(new Color(60, 180, 60));
        exportButton.setForeground(Color.WHITE);
        exportButton.setOpaque(true);
        exportButton.setBorderPainted(false);
        exportButton.setFocusPainted(false);
        exportButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        exportButton.setPreferredSize(new Dimension(150, 35));
        exportButton.addActionListener(e -> exportToCSV());

        inputPanel = new JPanel(new GridLayout(2, 2, 5, 5)); // для группировки элементов

        for (int i = 0; i < 2; i++) {
            JLabel inputLabel = new JLabel("Параметр " + (i + 1) + ":");
            inputLabel.setFont(new Font("Arial", Font.BOLD, 14));
            inputLabels.add(inputLabel);

            JTextField inputField = new JTextField(10);
            inputField.setFont(new Font("Arial", Font.PLAIN, 14));
            inputFields.add(inputField);

            // добавление элементов в inputPanel
            inputPanel.add(inputLabel);
            inputPanel.add(inputField);
        }

        // Панель для выбора запроса через выпадающий список с SQL-запросами
        JLabel queryComboBoxLabel = new JLabel("Выберите запрос:");
        queryComboBoxLabel.setFont(new Font("Arial", Font.BOLD, 14));

        queryComboBox = new JComboBox<>(queryNames);
        queryComboBox.setSelectedIndex(0);
        queryComboBox.setFont(new Font("Arial", Font.PLAIN, 14));

        gbc.gridx = 0;
        gbc.gridy = 0;
        topPanel.add(queryLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        topPanel.add(queryScrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        topPanel.add(queryComboBoxLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        topPanel.add(queryComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        topPanel.add(inputPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        topPanel.add(fetchButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        topPanel.add(exportButton, gbc);

        // Таблица для отображения данных
        table = new JTable();
        table.setFillsViewportHeight(true); // заполнит оставшееся пространство после кнопок
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(100, 180, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setRowHeight(25); // высота строки
        scrollPane = new JScrollPane(table);
        scrollPane.setBackground(Color.LIGHT_GRAY);

        add(topPanel, BorderLayout.NORTH); //
        add(scrollPane, BorderLayout.CENTER);

        // Обработка выбора запроса
        queryComboBox.addActionListener(e -> updateInputFieldsVisibility()); // показывает поля для ввода
    }

    private Query selectQueryByName(String queryName) {
        for (Query query : queries) {
            if (queryName.equalsIgnoreCase(query.getQueryName())) {
                return query;
            }
        }
        throw new IllegalArgumentException("Запрос не найден");
        // возвращает IllegalArgumentException, если запрос не найден
    }

    private void updateInputFieldsVisibility() {
        String selectedQueryName = (String) queryComboBox.getSelectedItem(); // выбранный запрос в качестве строки
        if (selectedQueryName.equalsIgnoreCase(queryNames[0])){
            for (int i = 0; i < inputFields.size(); i++) {
                inputLabels.get(i).setVisible(false);
                inputFields.get(i).setVisible(false);
            }
            return;
        }
        Query selectedQuery = selectQueryByName(selectedQueryName);
        List<ParameterTemplate> parameterTemplates = selectedQuery.getRequiredParamsTemplates();

        int paramsTemplatesQuantity;
        if (parameterTemplates == null) {
            paramsTemplatesQuantity = 0;
        } else {
            paramsTemplatesQuantity = parameterTemplates.size();
        }

        for (int i = 0; i < paramsTemplatesQuantity; i++) {
            inputLabels.get(i).setText(parameterTemplates.get(i).getName());
            inputLabels.get(i).setVisible(true);
            inputFields.get(i).setVisible(true);
        }
        for (int i = paramsTemplatesQuantity; i < inputFields.size(); i++) {
            inputLabels.get(i).setVisible(false);
            inputFields.get(i).setVisible(false);
        }
    }

    private void executeQuery() {
        String selectedQueryName = (String) queryComboBox.getSelectedItem();
        if (selectedQueryName == null) {
            throw new IllegalArgumentException("Неизвестный запрос");
        } else {
            try {
                if (selectedQueryName.equalsIgnoreCase(queryNames[0])) {
                    Connection connection = DriverManager.getConnection(Main.DATABASE_URL, Main.USER_NAME, Main.DATABASE_PASS);
                    PreparedStatement statement = connection.prepareStatement(queryArea.getText());
                    // запускаем запрос
                    ResultSet resultSet = statement.executeQuery();
                    displayResultSet(resultSet);
                } else {
                    Query selectedQuery = selectQueryByName(selectedQueryName);

                    List<ParameterTemplate> parameterTemplates = selectedQuery.getRequiredParamsTemplates();
                    List<Parameter> params = new ArrayList<>();
                    if (parameterTemplates != null) {
                        for (int i = 0; i < parameterTemplates.size(); i++) {
                            params.add(new Parameter(parameterTemplates.get(i).getVarType(), inputFields.get(i).getText().trim()));
                        }
                    }
                    Connection connection = DriverManager.getConnection(Main.DATABASE_URL, Main.USER_NAME, Main.DATABASE_PASS);

                    ResultSet resultSet;
                    System.out.println(selectedQuery.getType());
                    if (selectedQuery.getHasCursor()){
                        System.out.println("Query With Cursor");
                        CallableStatement statement = (CallableStatement) selectedQuery.getPreparedStatement(connection,params);
                        resultSet = handleAStatement(selectedQuery, statement);
                    } else {
                        System.out.println("Query Without Cursor");
                        PreparedStatement statement = selectedQuery.getPreparedStatement(connection, params);
                        resultSet = handleAStatement(selectedQuery, statement);
                    }
                    displayResultSet(resultSet);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Некорректное число: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Ошибка выполнения запроса: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                // throw new RuntimeException(e);
            }
        }
    }
    private ResultSet handleAStatement (Query selectedQuery, PreparedStatement preparedStatement) throws SQLException {
        if (selectedQuery.getType() == Query.TYPE_DELETE || selectedQuery.getType() == Query.TYPE_UPDATE){
            preparedStatement.executeUpdate();
            return null;
        } else {
            return preparedStatement.executeQuery();
        }
    }
    private ResultSet handleAStatement(Query selectedQuery, CallableStatement callableStatement) throws SQLException {
        System.out.println("Callable Statement handled");
        if (Character.toString(selectedQuery.getType()).equalsIgnoreCase("U")) {
            return null;
        }
        callableStatement.registerOutParameter(1, Types.OTHER); // Cursor is of type "OTHER"
        callableStatement.execute();
        return (ResultSet) callableStatement.getObject(1);
    }
    private void displayResultSet(ResultSet rs) throws SQLException {
        if (rs == null || !rs.next()){
            System.out.println("rs = null");
            return;
        }
        rs.beforeFirst();
        System.out.println("rs != null");
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        DefaultTableModel model = new DefaultTableModel();
        for (int i = 1; i <= columnCount; i++) {
            model.addColumn(metaData.getColumnName(i));
        }


        while (rs.next()) {
            Object[] row = new Object[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                row[i - 1] = rs.getObject(i);
            }
            model.addRow(row);
        }
        table.setModel(model);
    }

    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser(); // окно с выбором имени файла
        fileChooser.setDialogTitle("Сохранить как CSV");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV файлы (*.csv)", "csv")); // фильтр выводимых файлов

        int userSelection = fileChooser.showSaveDialog(this); // диалоговое окно для сохранения файла
        if (userSelection == JFileChooser.APPROVE_OPTION) // нажата кнопка "сохранить"
        {
            String fileName = fileChooser.getSelectedFile().getPath(); // имя файла
            if (!fileName.endsWith(".csv")) // добавление расширения
            {
                fileName += ".csv";
            }

            try (FileWriter writer = new FileWriter(fileName)) // поток для записи в файл
            {
                DefaultTableModel model = (DefaultTableModel) table.getModel(); // модель таблицы
                int columnCount = model.getColumnCount(); // считает кол-во столбцов

                // Записываем заголовки
                for (int i = 0; i < columnCount; i++) {
                    writer.write(model.getColumnName(i));
                    if (i < columnCount - 1) {
                        writer.write(",");
                    }
                }
                writer.write("\n");

                // Записываем данные
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < columnCount; j++) {
                        writer.write(model.getValueAt(i, j).toString());
                        if (j < columnCount - 1) {
                            writer.write(",");
                        }
                    }
                    writer.write("\n");
                }

                JOptionPane.showMessageDialog(this, "Таблица успешно экспортирована в CSV!", "Успех", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException | ClassCastException e) {
                JOptionPane.showMessageDialog(this, "Ошибка экспорта: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static List<Query> getQueries() {
        List<Query> queries = new ArrayList<Query>();

        ParameterTemplate groupId = new ParameterTemplate("ID группы", ParameterTemplate.TYPE_INT);
        ParameterTemplate minPrice = new ParameterTemplate("Мин. цена", ParameterTemplate.TYPE_INT);

        queries.add(new SimpleQuery("Вывести всех сотрудников", "SELECT * FROM employee", Query.TYPE_READ, null));
        queries.add(new SimpleQuery("Вывести проекты с датами",
                "SELECT project.id AS project_id, project.pr_name AS project_name," +
                        "project.s_date AS project_start_date, MAX(stage.end_date) AS project_end_date\n" +
                        "FROM project\n" +
                        "JOIN stage ON project.id = stage.pr_id\n" +
                        "GROUP BY project_id, project_name, project_start_date\n" +
                        "ORDER BY project_id\n", Query.TYPE_READ, null));
        // !!!!!!
        queries.add(new SimpleQuery("Вывести операции с датами",
                "SELECT op.id AS operation_id, \n" +
                "op.op_name AS operation_name, \n" +
                "op.start_date AS operation_start_date, \n" +
                "MAX(stat_ch.cng_date) AS operation_end_date\n" +
                "FROM operation AS op\n" +
                "JOIN op_stat_change AS stat_ch ON op.id = stat_ch.op_id\n" +
                "WHERE stat_id = (SELECT id FROM status WHERE value = 'Выполнена')\n" +
                "GROUP BY op.id, op.op_name, op.start_date\n" +
                "ORDER BY op.id;", Query.TYPE_READ, null));

        queries.add(new SimpleQuery("Вывести сотрудников по группе",
                "SELECT employee.empl_name AS \"name\", employee.empl_surname AS \"surname\", " +
                        "employee.empl_patronymic AS \"patronymic\"\n" +
                        "FROM employee\n" +
                        "INNER JOIN empl_group\n" +
                        "ON employee.gr_id = empl_group.id\n" +
                        "WHERE empl_group.id = ?\n", Query.TYPE_READ, List.of(groupId)));

        queries.add(new SimpleQuery("Вывести устройства по минимальной цене",
                "SELECT t_code AS \"type code\", m_code AS \"model code\" FROM device\n" +
                        "WHERE price >= ?", Query.TYPE_READ, List.of(minPrice)));

        ParameterTemplate qualification = new ParameterTemplate("Квалификация", ParameterTemplate.TYPE_STRING);
        queries.add(new ProcedureQuery("Удалить все образования по квалификации",
                "DO $$\n" +
                        "DECLARE\n" +
                        "  r int;\n" +
                        "  str character varying(5000);\n" +
                        "BEGIN\n" +
                        "  -- Получаем параметр через SELECT INTO\n" +
                        "  SELECT $1 INTO str;\n" +
                        "  FOR r IN SELECT id FROM education WHERE qualification = str\n" +
                        "  LOOP\n" +
                        "    DELETE FROM empl_edu_list WHERE edu_id = r;\n" +
                        "    DELETE FROM education WHERE id = r;\n" +
                        "  END LOOP;\n" +
                        "END $$;\n",
                Query.TYPE_DELETE,
                List.of(qualification),"remove_edu_by_qual"));

        ParameterTemplate workerID = new ParameterTemplate("ID работника",ParameterTemplate.TYPE_INT);
        queries.add(new SimpleQuery("Перевести сотрудника с нужным ID в другую группу",
                "UPDATE employee\n" +
                "SET gr_id = ?\n" +
                "WHERE id = ?;\n", Query.TYPE_UPDATE, List.of(groupId,workerID)));
        /*
        "Вывести всех сотрудников",
        "Вывести проекты с датами",
        "Вывести операции с датами",
        "Вывести сотрудников по группе",
        "Вывести устройства по минимальной цене",
        "Удалить все образования по подготовке"
         */
        return queries;
    }

    public String[] getQueryNames() {
        String[] queryNames = new String[queries.size() + 1];
        queryNames[0] = "Свой запрос (только вывод данных)";
        for (int i = 1; i < queries.size() + 1; i++) {
            queryNames[i] = queries.get(i - 1).getQueryName();
        }
        return queryNames;
    }

    public static void main(String[] args) {
        // Swing не поддерживает многопоточность
        SwingUtilities.invokeLater(() -> // гарантирует что UI-интерфейс запускается в этом потоке правильно
        {
            DBInterface frame = new DBInterface();
            frame.setVisible(true);
        });
    }
}