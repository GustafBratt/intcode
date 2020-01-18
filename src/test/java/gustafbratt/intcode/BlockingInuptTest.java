package gustafbratt.intcode;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

public class BlockingInuptTest {
    @Test
    public void blockingInupt(){
        String program = "3,3,99,0";
        InputOutput in = new InputOutput();
        Computer c = new Computer(program, in, new InputOutput());
        c.printMemoryDump();
        State state = c.run();
        assertThat(state).isEqualTo(State.BLOCKED);
        in.write(242);
        state = c.run();
        assertThat(state).isEqualTo(State.ENDED);
        c.printMemoryDump();
        assertThat(c.getMemCell(BigInteger.valueOf(3))).isEqualTo(BigInteger.valueOf(242));
    }
}
