package gustafbratt.intcode;

import com.google.common.io.Resources;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class MiscTests {
    @Test
    public void addDirectToRelative(){
        String program = "21101,1,1,10,99";
        Computer c = new Computer(program);
        c.runUntilBlockedOrEnd();
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
        String program = "3,3,99,0,0";
        InputOutput in = new InputOutput();
        Computer c = new Computer(program, in, new InputOutput());
        State state = c.runUntilBlockedOrEnd();
        assertThat(state).isEqualTo(State.BLOCKED);
        in.write(242);
        state = c.runUntilBlockedOrEnd();
        assertThat(state).isEqualTo(State.ENDED);
        assertThat(c.getMemCell(BigInteger.valueOf(3))).isEqualTo(BigInteger.valueOf(242));
    }

    @Test
    public void testRawDump(){
        String program = "21101,1,1,10,99";
        Computer c = new Computer(program);
        String rawMemoryDump = c.getRawMemoryDump();
        assertThat(rawMemoryDump).contains("0\t21101").contains("4\t99");
    }

    @Test
    public void testDisassembleDump() throws IOException {
        String program = Resources.toString(Resources.getResource("day9.txt"), StandardCharsets.UTF_8);
        Computer c = new Computer(program);
        String disassembledDump = c.getDisassembledDump();
        assertThat(disassembledDump).contains("0    01102 MUL  34463338  34463338       *63");
    }
}
