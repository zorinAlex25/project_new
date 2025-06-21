package old.util.params;

public class Parameter extends ParameterAbstractClass{
    private String value;

    public Parameter(char varType, String value) {
        super(varType);
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
