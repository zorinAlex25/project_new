package old.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ProcedureQuery extends Query{
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
    private String functionName; // название процедуры вместе с переменными

    public ProcedureQuery(String name, String queryString, Character type, List<ParameterTemplate> requiredParamsTemplates, String functionName) {
        super(name, queryString, type, requiredParamsTemplates);
        this.functionName = functionName;
    }

    @Override
    public void getStatement(Connection connection, List<Parameter> params) throws SQLException {

    }

    public String getFunctionName() {
        return functionName;
    }
}
