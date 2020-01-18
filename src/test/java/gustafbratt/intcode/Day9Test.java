package gustafbratt.intcode;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

public class Day9Test {
    @Test
    public void example2(){
        InputOutput io = new InputOutput();
        Computer c = new Computer("1102,34915192,34915192,7,4,7,99,0", null, io);
        c.printMemoryDump();
        c.run();
        BigInteger result = io.read();
        System.out.println(result);
        assertThat(result.toString().length()).isGreaterThan(15);
    }
}
