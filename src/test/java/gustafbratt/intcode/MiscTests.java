package gustafbratt.intcode;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

public class MiscTests {
    @Test
    public void addDirectToRelative(){
        String program = "21101,1,1,10,99";
        Computer c = new Computer(program);
        c.printMemoryDump();
        c.run();
        c.printMemoryDump();
        assertThat(c.memory.get(BigInteger.valueOf(10))).isEqualTo(BigInteger.TWO);
    }
}
