package old.util;

import java.sql.*;
import java.util.List;

public class ProcedureQuery extends Query
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

    /*
    CREATE PROCEDURE insert_data(a integer, b integer)
    LANGUAGE SQL
    BEGIN ATOMIC
    INSERT INTO tbl VALUES (a);
    INSERT INTO tbl VALUES (b);
    END;
     */
    @Override
    public ResultSet executeQuery(Connection connection, List<Parameter> params) throws SQLException
    {
        String procedureCreateString = "CREATE OR REPLACE PROCEDURE " + this.procedureName + "("
                + getParamsInLatin(false) + ") LANGUAGE SQL " +
                "AS $$ " + queryString + " $$;"; // запрос на создание функции
        CallableStatement createProcedure = connection.prepareCall(procedureCreateString);
        createProcedure.execute();

        String procedureCallString = "CALL " + this.procedureName + "("
                + getQuestionMarksAsString() + ")";
        CallableStatement callAProcedure = connection.prepareCall(procedureCallString);
        callAProcedure.setString(1, "Инженер-физик");
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
        if (!res.isEmpty())
        {
            res = res + "?";
        }
        return res;
    }

}