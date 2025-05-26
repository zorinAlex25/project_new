package old.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SimpleQuery extends Query{
    /**
     * type:
     * C - create,
     * R - read,
     * U - update,
     * D - delete
     *
     * @param name
     * @param queryString
     * @param type
     * @param requiredParamsTemplates
     */
    public SimpleQuery(String name, String queryString, Character type, List<ParameterTemplate> requiredParamsTemplates) {
        super(name, queryString, type, requiredParamsTemplates);
    }

    @Override
    public ResultSet executeQuery(Connection connection, List<Parameter> params) throws SQLException {
        paramQuantityCheck(params); // проверка кол-ва параметров
        PreparedStatement statement = connection.prepareStatement(this.queryString);
        statement = setParamsToStatement(statement, params);
        if (this.getType() == Query.TYPE_DELETE || this.getType() == Query.TYPE_UPDATE){ // Query.TYPE_CREATE
            statement.executeUpdate();
            return null;
        } else {
            ResultSet resultSet = statement.executeQuery();
            return resultSet;
        }
    }
}
