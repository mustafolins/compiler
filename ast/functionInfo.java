package ast;

public class functionInfo {
    public String name;
    public boolean inClass;

    public functionInfo(String name, boolean inside){
        this.name = name;
        inClass = inside;
    }
}
