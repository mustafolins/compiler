# A simple programming language.

This is a simple programming language that has been written for a programming languages course.

## Examples

### Control Flow:
```vb
integer x : 3
if x < 4
{
    printl "Less than four!"
}
if x = 3
{
    x : 5
    print "x is greater "
    printl x
}
while x >= 1
{
    x : x - 1
    printl x
}
```

### Factorial function:

```vb
func integer factorial -> integer number
{
    integer result : 1
    while number >= 1
    {
        result : result * number
        number : number - 1
    }
    ret result
}
print "Factorial of 5: "
integer factOf5 : factorial 5
printl factOf5
```

### Object declaration and usage:
```vb
obj person
    string first : "Jon"
    string last : "Van Dam"
    func string getName -> string temp
    {
        ret first + " " + last
    }
endobj
person me : new person
string tempName : me.getName ""
printl tempName
```

## Extended Backus–Naur Form
```xml
<program> ::= { (<object> | <function> | <statement>) }

<end_of_statement> ::= '\n'

<object> ::= obj <id> <end_of_statement>
            { (<assignment> | <function>) }
            endobj <end_of_statement>

<function> ::= func <keyword> <id> -> <keyword> <id> <end_of_statement>
                \{ <end_of_statement>
                { <statement> }
                ret <expresion>
                \} <end_of_statement>

<statement> ::= (<assignment> | <class_accessor> | <function_call>) <end_of_statment>

<assignment> ::= [ <keyword> ] <id> : (<expresion> | <class_initialization>)

<expression> ::= ({ (<id> | <literal> ) }+ [(+ | - | * | /) <expression>]) 
                | <class_accessor>

<class_initialization> ::= new <id>
<class_accessor> ::= <id>\.<id> <literal>

<function_call> ::= <id> (<id> | <literal>)

<literal> ::= ( <string_literal> | <decimal_literal> | <integer_literal> )
```