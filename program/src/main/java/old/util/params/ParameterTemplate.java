package old.util.params;

public class ParameterTemplate extends ParameterAbstractClass{

    private String name;
    private String nameInLatin;
    private String inOutType = IN; // значение по умолчанию - in

    public ParameterTemplate(String name, String nameInLatin, char varType)
    {
        super(varType);
        this.name = name;
        this.nameInLatin = nameInLatin;
    }

    public ParameterTemplate(String name, String nameInLatin, char varType, String inOutType) {
        super(varType);
        this.name = name;
        this.nameInLatin = nameInLatin;
        this.inOutType = inOutType;
    }

    // возвращает название переменной как целую строку
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

    public String getInOutType()
    {
        return inOutType;
    }
}
