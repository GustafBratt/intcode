package gustafbratt.intcode;

import com.google.common.io.Resources;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class Day9Test {
    @Test
    public void example2(){
        InputOutput io = new InputOutput();
        Computer c = new Computer("1102,34915192,34915192,7,4,7,99,0", null, io);
        c.printMemoryDump();
        c.runUntilBlockedOrEnd();
        BigInteger result = io.read();
        System.out.println(result);
        assertThat(result.toString().length()).isGreaterThan(15);
    }

    @Test
    public void example1(){
        String program = "109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99";
        Computer c = new Computer(program);
        c.printMemoryDump();
        c.runUntilBlockedOrEnd();
        System.out.println(c.getOutput());
        assertThat(c.getOutput().toString()).isEqualTo(program);
    }

    @Test
    public void example0() {
        InputOutput out = new InputOutput();
        Computer c = new Computer("109,19,204,-34,99", null, out);
        c.relativeBase = BigInteger.valueOf(2000);
        c.memory.put(BigInteger.valueOf(1985), BigInteger.valueOf(242));
        c.printMemoryDump();
        c.runUntilBlockedOrEnd();
        BigInteger outVal = out.read();
        assertThat(outVal).isEqualTo(242);
    }

    @Test
    public void problem1() throws IOException {
        String program = Resources.toString(Resources.getResource("day9.txt"), StandardCharsets.UTF_8);
        Computer c = new Computer(program, new InputOutput(1));
        c.printMemoryDump();
        c.runUntilBlockedOrEnd();
        assertThat(c.getOutput().list.size()).isEqualTo(1);
        assertThat(c.getOutput().read()).isEqualTo(new BigInteger("3512778005"));
    }
    @Test
    public void problem2() throws IOException {
        String program = Resources.toString(Resources.getResource("day9.txt"), StandardCharsets.UTF_8);
        Computer c = new Computer(program, new InputOutput(2));
        c.printMemoryDump();
        c.runUntilBlockedOrEnd();
        assertThat(c.getOutput().list.size()).isEqualTo(1);
        assertThat(c.getOutput().read()).isEqualTo(new BigInteger("35920"));
    }
}
