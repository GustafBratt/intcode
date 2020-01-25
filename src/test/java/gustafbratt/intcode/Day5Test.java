package gustafbratt.intcode;

import com.google.common.io.Resources;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Day5Test {
    @Test
    public void problem1() throws IOException {
        String program = Resources.toString(Resources.getResource("day5.txt"), StandardCharsets.UTF_8);

        Computer c = new Computer(program, new InputOutput(1));
        c.runUntilBlockedOrEnd();
        System.out.println("Final output:" );
        System.out.println(c.getOutput().getFinalOutput());
        assertThat(c.getOutput().getFinalOutput()).isEqualTo(16209841);
    }
    @Test
    public void example1(){
        Computer c = new Computer("1002,4,3,4,33");
        c.runUntilBlockedOrEnd();
        assertThat(c.getMemCell(4)).isEqualTo(99);
    }

    @Test
    void example2(){
        Computer c = new Computer("1101,100,-1,4,0");
        c.printMemoryDump();
        c.runUntilBlockedOrEnd();
        c.printMemoryDump();
        assertThat(c.getMemCell(4)).isEqualTo(99);
    }

    @Test
    void problem2() throws IOException {
        String program = Resources.toString(Resources.getResource("day5.txt"), StandardCharsets.UTF_8);
        Computer c = new Computer(program, new InputOutput(5));
        c.runUntilBlockedOrEnd();
        System.out.println("Final output:" );
        System.out.println(c.getOutput().getFinalOutput());
        assertThat(c.getOutput().getFinalOutput()).isEqualTo(8834787);
    }
}
