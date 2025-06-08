package cn.ken;

/**
 * @author Ken-Chy129
 * @date 2025/5/19
 */
public class TestFinal {

    private static final String a = "123";

    private static String b;

    static {
        b = "456";
    }

    // 编译后赋值操作会在clinit里进行，静态初始化块的逻辑也会在其中
    // 先后顺序取决于代码里的先后顺序
    private static String c = "234";

    public void test1() {
        // 局部变量的final修饰在字节码层级没有任何体现，即test1和test2编译出来的字节码是一样的
        final int x = 1;
        System.out.println(x);
    }

    public void test2() {
        int x = 1;
        System.out.println(x);
    }

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

    public static void main(String[] args) {
        Test test = new Test(new Object());
        System.out.println(test.obj);;
        System.out.println(test.obj);;
        System.out.println(test.obj);;

        Object b = test.obj;
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
}
