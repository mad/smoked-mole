This code convert simple math to bytecode

`$ java Mole <sm file>`

after that, generated RunnableMath.class with some methods

one line sm code ("expression ;") is mathN method in this class

Example:

```
(455 + 3 + 7*2)*3/3;
```

Bytecode:

```
  // Method descriptor #9 ()V
  // Stack: 1, Locals: 1
  public RunnableMath();
    0  aload_0 [this]
    1  invokespecial java.lang.Object() [11]
    4  return


  // Method descriptor #13 ()I
  // Stack: 20, Locals: 20
  public int math0();
     0  iconst_0
     1  istore_1
     2  ldc <Integer 455> [14]
     4  ldc <Integer 3> [15]
     6  iadd
     7  ldc <Integer 7> [16]
     9  ldc <Integer 2> [17]
    11  imul
    12  iadd
    13  ldc <Integer 3> [15]
    15  imul
    16  ldc <Integer 3> [15]
    18  idiv
    19  istore_1
    20  iload_1
    21  ireturn

}
```


NOTE:

When wrong package path, try using `JTB_OPTIONS=-p mole` in
`.settings/sf.eclipse.javacc.prefs`

or

Append ` -p mole` to `Properties->JavaCC Options->jtb options->JTB_OPTIONS`
