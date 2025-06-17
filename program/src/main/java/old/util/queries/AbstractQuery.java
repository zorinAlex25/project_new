package old.util.queries;

import old.util.params.Parameter;
import old.util.params.ParameterTemplate;

import java.sql.*;
import java.util.List;

public abstract class AbstractQuery
{
    protected String queryName; // название запроса
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
     * type:
     * C - create,
     * R - read,
     * U - update,
     * D - delete
     **/

    public AbstractQuery(String queryName, String queryString, Character type, List<ParameterTemplate> requiredParamsTemplates)
    {
        this.queryName = queryName;
        this.queryString = queryString;
        this.params = null;
        this.type = type;
        this.requiredParamsTemplates = requiredParamsTemplates;
    }

    public abstract ResultSet executeQuery(Connection connection, List<Parameter> params) throws SQLException;

    public PreparedStatement getPreparedStatement(Connection connection, List<Parameter> params) throws SQLException
    {
        paramQuantityCheck(params); // проверка кол-ва параметров
        PreparedStatement statement = connection.prepareStatement(this.queryString);
        statement = setParamsToStatement(statement, params);
        return statement;
    }

    protected PreparedStatement setParamsToStatement(PreparedStatement statement, List<Parameter> params) throws SQLException
    {
        this.params = params;
        for (int i = 0; i < this.params.size(); i++)
        {
            int j = i + 1;
            Parameter parameter = this.params.get(i);
            char type = parameter.getVarType();
            paramTypeCheck(type, parameter);
            if (type == Parameter.TYPE_INT)
            {
                statement.setInt(j, Integer.parseInt(parameter.getValue()));
            } else if (type == Parameter.TYPE_STRING)
            {
                statement.setString(j, parameter.getValue()); // !!!!! ЗДЕСЬ ОШИБКА
            } else if (type == Parameter.TYPE_DATE)
            {
                statement.setDate(j, Date.valueOf(parameter.getValue()));
            } else if (type == Parameter.TYPE_FLOAT)
            {
                statement.setFloat(j, Float.parseFloat(parameter.getValue()));
            } else throw new IllegalArgumentException("Введён неизвестный тип данных в качестве параметра");
        }
        return statement;
    }

    protected void paramQuantityCheck(List<Parameter> params)
    {
        if (!(requiredParamsTemplates == null || requiredParamsTemplates.isEmpty()))
        {
            if (params == null || params.isEmpty())
            {
                throw new IllegalArgumentException("У запроса нет параметров");
            } else if (params.size() < requiredParamsTemplates.size())
            {
                throw new IllegalArgumentException("Недостаточно параметров");
            }
        }
    }

    protected void paramTypeCheck(Character type, Parameter param)
    {
        if (param.getVarType() != type)
        {
            throw new IllegalArgumentException("Введён параметр неверного типа");
        }
    }

    protected String getParamsInLatin(boolean getInOutType) // строка с параметрами функции
    {
        StringBuilder res = new StringBuilder();
        int paramsQuantity = this.requiredParamsTemplates.size();
        String param;
        if (paramsQuantity > 0)
        {
            for (int i = 0; i < paramsQuantity; i++)
            {
                ParameterTemplate parameterTemplate = this.requiredParamsTemplates.get(i);
                param = parameterTemplate.getNameInLatin();
                if (getInOutType) {
                    if (parameterTemplate.getInOutType() != ParameterTemplate.IN) {
                        param = parameterTemplate.getInOutType() + " " + param;
                    }
                }
                param = param + " " + parameterTemplate.getVarTypeAsString();

                res.append(param).append(", ");
            }
            return res.substring(0, res.length() - 2);
        }
        return "";
    }

    public boolean getHasCursor()
    {
        return hasCursor;
    }

    public Character getType()
    {
        return type;
    }

    public String getQueryName()
    {
        return queryName;
    }

    public String getQueryString()
    {
        return queryString;
    }

    public List<ParameterTemplate> getRequiredParamsTemplates()
    {
        return requiredParamsTemplates;
    }

    public List<Parameter> getParams()
    {
        return params;
    }
}
