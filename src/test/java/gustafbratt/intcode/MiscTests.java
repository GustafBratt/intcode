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

    @Test
    public void testIO(){
        InputOutput io = new InputOutput();
        assertThat(io.isEmpty()).isTrue();
        io.write(7);
        io.write(8);
        io.write(9);
        assertThat(io.isEmpty()).isFalse();
        assertThat(io.toString()).isEqualTo("7,8,9");
        assertThat(io.read()).isEqualTo(7);
        assertThat(io.read()).isEqualTo(8);
        assertThat(io.read()).isEqualTo(9);
        assertThat(io.isEmpty()).isTrue();
    }

    @Test
    public void blockingInupt() {
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
