package gustafbratt.intcode;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

public class BlockingInuptTest {
    @Test
    public void blockingInupt(){
        String program = "3,3,99,0";
        InputOutput in = new InputOutput();
        Computer c = new Computer(program, in, new InputOutput());
        State state = c.run();
        assertThat(state).isEqualTo(State.BLOCKED);
        in.write(242);
        state = c.run();
        assertThat(state).isEqualTo(State.ENDED);
        c.printMemoryDump();
        assertThat(c.getMemCell(3)).isEqualTo(242);
    }
}
