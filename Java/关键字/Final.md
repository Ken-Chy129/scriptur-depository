# Final修饰局部变量

Java语言里final关键字有多种用途，其核心都是标识“不可变”，具体在不同的场景下的含义不相同。
* 修饰类：标识类不允许被继承
* 修饰方法：标识方法在派生类中不允许被重写
* 修饰变量：标识变量一旦赋值不允许被修改
  * 编译时常量（const）：存放于常量池的变量永远不会被回收
  * 运行时不变量（readonly）：标识变量不允许被修改

## 字节码差异


局部变量声明时带不带final关键字修饰，访问的效率都是一样的。因为局部变量的final修饰在字节码层级没有任何体现，如下test1和test2编译出来的字节码是一样的。

```java
public void test1() {
    final int x = getX();
    System.out.println(x);
}

public void test2() {
    int x = getX();
    System.out.println(x);
}
```

而对于编译时常量，在编译时会做常量折叠，因此如下test3和test4编译出的代码不一致。

```java
public int test3() {
    int x = 1;
    int y = 2;
    return x + y;
}

public int test4() {
    final int x = 1;
    final int y = 2;
    return x + y;
}
```

test4因为识别到x和y都是常量，因此编译后会进行折叠，不需要再进行局部变量的访问，字节码差别如下：
```text
// access flags 0x1
  public test3()I
   L0
    LINENUMBER 28 L0
    ICONST_1
    ISTORE 1
   L1
    LINENUMBER 29 L1
    ICONST_2
    ISTORE 2
   L2
    LINENUMBER 30 L2
    ILOAD 1
    ILOAD 2
    IADD
    IRETURN
   L3
    LOCALVARIABLE this Lcn/ken/TestFinal; L0 L3 0
    LOCALVARIABLE x I L1 L3 1
    LOCALVARIABLE y I L2 L3 2
    MAXSTACK = 2
    MAXLOCALS = 3

  // access flags 0x1
  public test4()I
   L0
    LINENUMBER 34 L0
    ICONST_1
    ISTORE 1
   L1
    LINENUMBER 35 L1
    ICONST_2
    ISTORE 2
   L2
    LINENUMBER 36 L2
    ICONST_3
    IRETURN
   L3
    LOCALVARIABLE this Lcn/ken/TestFinal; L0 L3 0
    LOCALVARIABLE x I L1 L3 1
    LOCALVARIABLE y I L2 L3 2
    MAXSTACK = 1
    MAXLOCALS = 3
```
可以看到ILOAD 1、ILOAD 2、IADD 三个指令直接合并成了ICONST_3。

> 当然在Hotspot这种高性能的JVM中并没有太大的影响，因为在进行JIT编译后，这两个方法的差异会被消除，最终都会进行常量折叠。

## 一致性

先把成员或静态变量读到局部变量里可以保证一定程度的一致性，例如：在同一个方法里连续两次访问静态变量A.x可能会得到不一样的值，因为可能会有并发读写；但如果先有final int x = A.x然后连续两次访问局部变量x的话，那读到的值肯定会是一样的。这种做法的好处通常在有数据竞态但略微不同步没什么问题的场景下，例如说有损计数器之类的。

## 性能优化

先把成员或静态变量读到局部变量里可以一定程度上提升性能。

```java
public static void main(String[] args) {
    Test test = new Test(new Object());
    System.out.println(test.obj);;
    System.out.println(test.obj);;
    System.out.println(test.obj);;

    final Object b = test.obj;
    System.out.println(b);
    System.out.println(b);
    System.out.println(b);
}

static class Test {
    public Object obj;

    public Test(Object obj) {
        this.obj = obj;
    }
}
```

以上方法编译成字节码之后前三行打印语句和后三行打印语句的差异如下：

```
LINENUMBER 43 L3
GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
ALOAD 1
GETFIELD cn/ken/TestFinal$Test.obj : Ljava/lang/Object;
INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/Object;)V

LINENUMBER 46 L5
GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
ALOAD 2
INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/Object;)V
```

可以看到下面的操作少了一次GETFIELD，减少了生成的字节码，理论上这就是一种极端的字节码层面的优化。使用局部变量承接之后，后续读取值只需要从栈中获取，否则需要访问堆去获取数据。

因此对于final类型（即不需要读取到堆中实时的值）的情况，可以先将值加载到局部变量中进行读取。



参阅：

* JVM对于声明为final的局部变量（local var）做了哪些性能优化？ - RednaxelaFX的回答 - 知乎
  https://www.zhihu.com/question/21762917/answer/19239387
* JVM对于声明为final的局部变量（local var）做了哪些性能优化？ - Adastra的回答 - 知乎
  https://www.zhihu.com/question/21762917/answer/2847997879
