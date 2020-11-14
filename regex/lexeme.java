package regex;

public class lexeme {
    public lexType type;
    public String value;

    public lexeme(lexType n){
        type = n;
    }

    public lexeme(lexType n, String v){
        type = n;
        value = v;
    }

    public String toString(){
        return type + ": " + value;
    }
}