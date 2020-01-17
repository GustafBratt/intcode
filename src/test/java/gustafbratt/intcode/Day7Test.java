package gustafbratt.intcode;

import com.google.common.io.Resources;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Day7Test {
    Integer[] phasesPart1 = new Integer[] {0, 1, 2, 3, 4};
    Integer[] phasesPart2 = new Integer[] {5, 6, 7, 8, 9};

    @Test
    public void example1(){
        String program = "3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0";
        int max = ComputerSeries.runAllCombinations(phasesPart1, program);
        assertThat(max).isEqualTo(43210);
    }

    @Test
    public void example2(){
        String program = "3,23,3,24,1002,24,10,24,1002,23,-1,23,101,5,23,23,1,24,23,23,4,23,99,0,0";
        int max = ComputerSeries.runAllCombinations(phasesPart1, program);
        assertThat(max).isEqualTo(54321);
    }

    @Test
    public void example3(){
        String program = "3,31,3,32,1002,32,10,32,1001,31,-2,31,1007,31,0,33,1002,33,7,33,1,33,31,31,1,32,31,31,4,31,99,0,0,0";
        int max = ComputerSeries.runAllCombinations(phasesPart1, program);
        assertThat(max).isEqualTo(65210);
    }

    @Test
    public void problem1() throws IOException {
        String program = Resources.toString(Resources.getResource("day7.txt"), StandardCharsets.UTF_8);
        int max = ComputerSeries.runAllCombinations(phasesPart1, program);
        assertThat(max).isEqualTo(77500);

    }
}
