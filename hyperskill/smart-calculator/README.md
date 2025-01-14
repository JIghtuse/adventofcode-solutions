# Smart Calculator
Implementation of [Hyperskill project](https://hyperskill.org/projects/88?goal=347). Calculator supports following features:
* evaluates expressions with operators of different precedence: `^ * / - +`;
* parentheses to group parts of an expression `()`;
* variables to store computed expressions: `a = 2`;
* commands: `/exit`, `/help`;
* big numbers.

Implemented in [Kotlin](https://kotlinlang.org/).

## Usage
Reads expressions from stdin, writes evaluation result to stdout. Expression should be inserted as a single line.
To exit calculator, type command `/exit`. Another available command is `/help`.


Following is example of a calculator session. Empty lines added to clearly separate one expression input and output.

    5
    5
    
    2+2
    4
    
    -2 + 4 - 5 + 6
    3
    
    9 +++ 10 -- 8
    27
    
    123+
    Invalid expression
    
    a=3
    b = 4
    c =5
    
    b - c + 4 - a
    0
    
    v
    Unknown variable
    
    a*2+b*3+c*(2+3)
    43
    
    2*2^3
    16
    
    112234567890 + 112234567890 * (10000000999 - 999)
    1122345679012234567890
    
    /exit
    Bye!
