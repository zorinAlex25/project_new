package old.util;

import java.sql.*;
import java.util.List;

public class ProcedureQuery extends Query {
    public ProcedureQuery(String queryName, String queryString, char type, List<ParameterTemplate> requiredParamsTemplates, String procedureName) {
        super(queryName, queryString, type, requiredParamsTemplates);
        this.hasCursor = true;
    }

    @Override
    public ResultSet executeQuery(Connection connection, List<Parameter> params) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall(this.queryString);
        setParamsToStatement(callableStatement, params);
        callableStatement.execute();
        return null; // Для DELETE/UPDATE не возвращаем ResultSet
    }
}