# Toy-Language-Interpreter
<br/>
For the Advanced programming methods course in the third semester I wrote a toy language interpreter with a typechecker, multithreading, and a debugger like graphical user interface which shows the step by step execution of a program by displaying the execution stack, symbols table, heap, file table, out list and program ids. Also there is the option to open a new window so that the execution of multiple threads can be seen at the same time.<br/>

![run-program]()

![select-program]()

<a href="https://github.com/ComanacDragos/Toy-Language-Interpreter/tree/main/Interpreter/src/Model/Statements">Statements </a> supported:
<ul>
  <li>
    Control flow statements
    <ul>
      <li>Fork statement</li>
      <li>If statement</li>
      <li>While statement</li>
    </ul>
  </li>
  <li>
    File statements
    <ul>
      <li>Close read file statement</li>
      <li>Open read file statement</li>
      <li>Read file statement</li>
    </ul>
  </li>
  <li>
    Heap statements
    <ul>
      <li>New statement</li>
      <li>Write heap statement</li>
    </ul>
  </li>
  <li>Assign statement</li>
  <li>Compound Statement</li>
  <li>Print statement</li>
  <li>Variable declaration statement</li>
  <li>No operation statement</li>
</ul>
</br>
<a href="https://github.com/ComanacDragos/Toy-Language-Interpreter/tree/main/Interpreter/src/Model/Expressions">Expressions</a> supported:

<ul>
  <li>
    Binary expressions
    <ul>
      <li>
        Arithmetic expression
      </li>
      <li>Logic expression</li>
      <li>Relational expression</li>
    </ul>    
  </li>
  <li>
    Unary expressions
     <ul>
       <li>Read heap expression</li>
    </ul>
  </li>
  <li>Value expression</li>
  <li>Variable expression</li>
</ul>
  
  
  

