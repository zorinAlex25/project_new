package old.util;

import java.sql.*;
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
    public ResultSet executeQuery(Connection conn, List<Parameter> params) throws SQLException {
        String fun = "CREATE OR REPLACE FUNCTION " + functionName + "(" + paramsAsQueryElement() + ")";
        fun = fun + "RETURNS refcursor AS '" + queryString + "'";
        Statement stmt = conn.createStatement();
        stmt.execute(fun);
        stmt.close();
        conn.setAutoCommit(false);

        CallableStatement func = conn.prepareCall("{? = call " + functionName + "(" + paramsAsQueryElement() + ")}");
        func.registerOutParameter(1, Types.OTHER);
        func.execute();
        return (ResultSet) func.getObject(1);
    }
    private String paramsAsQueryElement(){
        String res = "";
        for (int i = 0; i < requiredParamsTemplates.size() - 1; i++)
        {
            ParameterTemplate paramTemplate = requiredParamsTemplates.get(i);
            String paramStr = paramTemplate.getNameAsWholeString() + paramTemplate.getVarTypeAsString()
                    + " "+paramTemplate.getInOutType() + ", ";
            res = res + paramStr;
        }
        ParameterTemplate paramTemplate = requiredParamsTemplates.get(requiredParamsTemplates.size() - 1);
        res = res + paramTemplate.getNameAsWholeString() + paramTemplate.getVarTypeAsString()
                + " " + paramTemplate.getInOutType();
        return res;
    }
    private String getFunctionName() {
        return functionName;
    }
}
