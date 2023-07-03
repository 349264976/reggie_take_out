import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


public class test1 {

    @Test
    public void test(){
        int i  = 1;
        i = i++;
        i =i++;
        System.out.println(i);
        System.out.println(i);
    }

}
