package old.util;

public class ParameterTemplate {

    // ТИП ПЕРЕМЕННОЙ
    public static final char TYPE_FLOAT = 'F';
    public static final char TYPE_INT = 'I';
    public static final char TYPE_STRING = 'S';
    public static final char TYPE_DATE = 'D';

    // ВВОДИТ ИЛИ ВЫВОДИТ ДАННЫЕ
    public static final String IN_AND_OUT = "INOUT";
    public static final String IN = "IN";
    public static final String OUT = "OUT";

    private String name;
    private String nameInLatin;
    private char varType;
    private String inOutType = IN; // значение по умолчанию - in

    /*
    public ParameterTemplate(String name, char varType, String inOutType)
    {
        this.name = name;
        this.varType = varType;
        this.inOutType = inOutType;
    }
     */

    public ParameterTemplate(String name, String nameInLatin, char varType)
    {
        this.name = name;
        this.nameInLatin = nameInLatin;
        this.varType = varType;
    }

    public String getVarTypeAsString(){
        switch (varType){
            case TYPE_DATE :
                return "date";
            case TYPE_FLOAT:
                return "float";
            case TYPE_STRING:
                return "varchar (1000)";
            case TYPE_INT:
                return "integer";
            default:
                throw new IllegalArgumentException("Unknown Type");
        }
    }
    // getNameAsWholeString возвращает название переменной как целую строку
    public String getNameAsWholeString(){
        String string = name.trim().replace(".","");
        return string.replaceAll("\\s+", " ").replaceAll(" ","_");
    }

    public String getName()
    {
        return name;
    }

    public String getNameInLatin()
    {
        return nameInLatin;
    }

    public char getVarType()
    {
        return varType;
    }
    public String getInOutType()
    {
        return inOutType;
    }
}
