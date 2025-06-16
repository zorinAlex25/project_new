package old.util.params;

public abstract class ParameterAbstractClass {
    // ТИП ПЕРЕМЕННОЙ
    public static final char TYPE_FLOAT = 'F';
    public static final char TYPE_INT = 'I';
    public static final char TYPE_STRING = 'S';
    public static final char TYPE_DATE = 'D';

    // ВВОДИТ ИЛИ ВЫВОДИТ ДАННЫЕ
    public static final String IN_AND_OUT = "INOUT";
    public static final String IN = "IN";
    public static final String OUT = "OUT";

    private char varType; // тип переменной

    public ParameterAbstractClass(char varType) {
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
    public char getVarType()
    {
        return varType;
    }
}
