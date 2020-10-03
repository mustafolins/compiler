package regex;

import java.util.function.Predicate;

public class condition {
    public Predicate<Character> condition;
    public boolean isMultiple;

    public condition(Predicate<Character> predicate, boolean multiple){
        condition = predicate;
        isMultiple = multiple;
    }
}
