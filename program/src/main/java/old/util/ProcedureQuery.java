package old.util;

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
     * @param queryString
     * @param type
     * @param requiredParamsTemplates
     */
    public ProcedureQuery(String name, String queryString, Character type, List<ParameterTemplate> requiredParamsTemplates) {
        super(name, queryString, type, requiredParamsTemplates);
    }
}
