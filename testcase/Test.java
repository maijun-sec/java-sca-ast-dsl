
import java.util.List;
import java.util.logging.*;

public class Test {
    private static final AbcLogger abcLogger = new LogManager().getLogger("123");
    private static AbcLogger a1Logger = new LogManager().getLogger("123");
    private final AbcLogger a2Logger = new LogManager().getLogger("123");
    private AbcLogger a3Logger = new LogManager().getLogger("123");
    private static int x = 5;

    /**
     * password: xx,xx
     * @param args
     */
    public static void main(String[] args) {
        try {
            int x = 5 / 0;
        } catch (Exception e) {
            return;
        }

        try {
            int x = 5 / 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            int x = 5 / 0;
        } catch (Exception e) {
        }
    }

    public void xx1() {
        try {
            int x = 5 / 0;
        } catch (Exception e) {
        }
    }

    public void debugA(List<String> args) {
        System.out.println("hello");
    }
}

class AbcLogger extends Logger {

}