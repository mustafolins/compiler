package regex;

public class lexeme {
    public lexType name;
    public String value;

    public lexeme(lexType n){
        name = n;
    }

    public lexeme(lexType n, String v){
        name = n;
        value = v;
    }

    public String toString(){
        return name + ": " + value;
    }
}