package regex;

public class lexeme {
    public String name;
    public String value;

    public lexeme(String n){
        name = n;
    }

    public lexeme(String n, String v){
        name = n;
        value = v;
    }

    public String toString(){
        return name + ": " + value;
    }
}
