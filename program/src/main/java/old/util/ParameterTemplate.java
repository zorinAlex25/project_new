package old.util;

public class ParameterTemplate {
    private String name;
    private char type;

    public static final char TYPE_FLOAT = 'F';
    public static final char TYPE_INT = 'I';
    public static final char TYPE_STRING = 'S';
    public static final char TYPE_DATE = 'D';

    public ParameterTemplate(String name, Character type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }
    public char getType() {
        return type;
    }
}
