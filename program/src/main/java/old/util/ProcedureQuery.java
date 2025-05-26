package old.util;

import java.sql.*;
import java.util.List;

public class ProcedureQuery extends Query {
    /**
     * type:
     * C - create,
     * R - read,
     * U - update,
     * D - delete
     *
     * @param name
     * @param queryString // здесь это просто код функции
     * @param type
     * @param requiredParamsTemplates
     */
    private String procedureName;

    public ProcedureQuery(String queryName, String queryString, char type, List<ParameterTemplate> requiredParamsTemplates, String procedureName) {
        super(queryName, queryString, type, requiredParamsTemplates);
        this.procedureName = procedureName;
        this.hasCursor = true; // Указываем, что это хранимая процедура с курсором
    }

    @Override
    public ResultSet executeQuery(Connection connection, List<Parameter> params) throws SQLException {
        connection.setAutoCommit(false);

        CallableStatement callableStatement = connection.prepareCall(this.queryString);
        setParamsToStatement(callableStatement, params);
        callableStatement.execute();

        connection.commit();
        return callableStatement.getObject(1, ResultSet.class); // Получаем результат как ResultSet
    }
}