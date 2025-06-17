package old.util.queries;

import old.util.params.Parameter;
import old.util.params.ParameterTemplate;

import java.sql.*;
import java.util.List;

public class ProcedureQuery extends AbstractQuery
{
    /**
     * type:
     * C - create,
     * R - read,
     * U - update,
     * D - delete
     *
     * @param name
     * @param queryString // здесь это блок кода вместе код функции
     * @param type
     * @param requiredParamsTemplates
     */
    private String procedureName;

    public ProcedureQuery(String queryName, String queryString, char type, List<ParameterTemplate> requiredParamsTemplates, String procedureName)
    {
        super(queryName, queryString, type, requiredParamsTemplates);
        this.procedureName = procedureName;
        this.hasCursor = true; // Указываем, что это хранимая процедура с курсором
    }

    @Override
    public ResultSet executeQuery(Connection connection, List<Parameter> params) throws SQLException
    {
        String procedureCreateString = "CREATE OR REPLACE PROCEDURE " + this.procedureName + "("
                + getParamsInLatin(false) + ") LANGUAGE plpgsql " +
                "AS $$ " + queryString + " $$;"; // запрос на создание функции
        CallableStatement createProcedure = connection.prepareCall(procedureCreateString);
        createProcedure.execute();

        String procedureCallString = "CALL " + this.procedureName + "("
                + getQuestionMarksAsString() + ")";
        CallableStatement callAProcedure = connection.prepareCall(procedureCallString);
        callAProcedure.setString(1, "Главный инженер");
        callAProcedure.execute();

        // setParamsToStatement(createProcedure, params);
        // createProcedure.registerOutParameter(1,Types.OTHER);

        return null; // процедура не возвращает результата
    }

    private String getQuestionMarksAsString()
    {
        String res = "";
        for (int i = 0; i < this.requiredParamsTemplates.size() - 1; i++)
        {
            res = res + "?, ";
        }
        if (!this.requiredParamsTemplates.isEmpty())
        {
            res = res + "?";
        }
        return res;
    }

}