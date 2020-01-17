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
    @Test void prob2example1() throws IOException {
        String program = "3,26,1001,26,-4,26,3,27,1002,27,2,27,1,27,26,27,4,27,1001,28,-1,28,1005,28,6,99,0,0,5";
        InputOutput io1 = ComputerSeries.run5Computers(new Integer[]{9,8,7,6,5}, program);
        System.out.println(io1);
        assertThat(io1.read()).isEqualTo(139629729);
    }

    @Test void prob2example2() throws IOException {
        String program = "3,52,1001,52,-5,52,3,53,1,52,56,54,1007,54,5,55,1005,55,26,1001,54," +
            "-5,54,1105,1,12,1,53,54,53,1008,54,0,55,1001,55,1,55,2,53,55,53,4," +
            "53,1001,56,-1,56,1005,56,6,99,0,0,0,0,10";
        InputOutput io1 = ComputerSeries.run5Computers(new Integer[]{9,7,8,5,6}, program);
        System.out.println(io1);
        assertThat(io1.read()).isEqualTo(18216);
    }

    @Test
    void problem2() throws IOException {
        String program = Resources.toString(Resources.getResource("day7.txt"), StandardCharsets.UTF_8);
        int result = ComputerSeries.runAllCombinationsPartB(phasesPart2, program);
        System.out.println(result);
    }
}
