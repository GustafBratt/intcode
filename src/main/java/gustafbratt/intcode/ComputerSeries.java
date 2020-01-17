package gustafbratt.intcode;

import com.google.common.collect.Collections2;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ComputerSeries {
    private final String program;
    private Integer[] phases;

    public ComputerSeries(String program, Integer[] phases) {
        this.phases = phases;
        this.program = program;
    }
    int runReturnOutput(){
        int carry = 0;
        for(int phase: phases){
            InputOutput inputs = new InputOutput();
            inputs.write(phase);
            inputs.write(carry);
            InputOutput out = new InputOutput();
            Computer c = new Computer(program, inputs, out);
            c.run();
            carry = out.read();
        }
        return carry;
    }
    static int runAllCombinations(Integer[] phases, String program){
        List<Integer> integers = Arrays.asList(phases);
        Collection<List<Integer>> permutations = Collections2.permutations(integers);
        int maxResult = 0;
        for(List<Integer> permutation : permutations){
            ComputerSeries cs = new ComputerSeries(program, permutation.toArray(new Integer[] {}));
            int result = cs.runReturnOutput();
            if(result > maxResult)
                maxResult = result;
        }
        return maxResult;
    }
}
