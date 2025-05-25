package old.util;

import java.sql.*;
import java.util.List;

public class Query {
    protected String name; // название запроса
    protected String queryString; // хранит сам SQL-запрос в виде строки
    protected List<ParameterTemplate> requiredParamsTemplates; // хранит типы параметров
    protected List<Parameter> params; // хранит параметры
    protected char type;
    protected boolean hasCursor = false;

    // обозначают типы запросов
    public static final Character TYPE_CREATE = 'C';
    public static final Character TYPE_READ = 'R';
    public static final Character TYPE_UPDATE = 'U';
    public static final Character TYPE_DELETE = 'D';
    /**
    type:
    C - create,
    R - read,
    U - update,
    D - delete
     **/

    public Query(String name, String queryString, Character type, List<ParameterTemplate> requiredParamsTemplates) {
        this.name = name;
        this.queryString = queryString;
        this.params = null;
        this.type = type;
        this.requiredParamsTemplates = requiredParamsTemplates;
    }

    public PreparedStatement getPreparedStatement(Connection connection, List<Parameter> params) throws SQLException {
        paramQuantityCheck(params); // проверка кол-ва параметров
        PreparedStatement statement = connection.prepareStatement(this.queryString);
        statement = setParamsToStatement(statement, params);
        return statement;
    }

    protected PreparedStatement setParamsToStatement(PreparedStatement statement, List<Parameter> params) throws SQLException {
        this.params = params;
        for (int i = 0; i < this.params.size(); i++) {
            int j = i + 1;
            Parameter parameter = this.params.get(i);
            char type = parameter.getType();
            paramTypeCheck(type, parameter);
            if (type == Parameter.TYPE_INT) {
                statement.setInt(j, Integer.parseInt(parameter.getValue()));
            } else if (type == Parameter.TYPE_STRING) {
                statement.setString(j, parameter.getValue()); // !!!!! ЗДЕСЬ ОШИБКА
            } else if (type == Parameter.TYPE_DATE) {
                statement.setDate(j, Date.valueOf(parameter.getValue()));
            } else if (type == Parameter.TYPE_FLOAT) {
                statement.setFloat(j, Float.parseFloat(parameter.getValue()));
            } else throw new IllegalArgumentException("Введён неизвестный тип данных в качестве параметра");
        }
        return statement;
    }

    protected void paramQuantityCheck(List<Parameter> params){
        if (!(requiredParamsTemplates == null || requiredParamsTemplates.isEmpty())) {
            if (params == null || params.isEmpty()) {
                throw new IllegalArgumentException("У запроса нет параметров");
            } else if (params.size() < requiredParamsTemplates.size()) {
                throw new IllegalArgumentException("Недостаточно параметров");
            }
        }
    }

    private void paramTypeCheck(Character type, Parameter param){
        if (param.getType() != type){
            throw new IllegalArgumentException("Введён параметр неверного типа");
        }
    }

    public boolean getHasCursor() {
        return hasCursor;
    }
    public Character getType() {
        return type;
    }
    public String getName() {
        return name;
    }
    public String getQueryString() {
        return queryString;
    }
    public List<ParameterTemplate> getRequiredParamsTemplates() {
        return requiredParamsTemplates;
    }
    public List<Parameter> getParams() {
        return params;
    }
}
