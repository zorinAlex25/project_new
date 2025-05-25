package old.util;

public class Parameter {
    private char type;
    private String value;

    public static final char TYPE_FLOAT = 'F';
    public static final char TYPE_INT = 'I';
    public static final char TYPE_STRING = 'S';
    public static final char TYPE_DATE = 'D';

    public Parameter(char type, String value) {
        this.type = type;
        this.value = value;
    }

    public char getType() {
        return type;
    }
    public String getValue() {
        return value;
    }
}
