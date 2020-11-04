# A simple programming language.

This is a simple programming language that has been written for a programming languages course.

Factorial example:

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