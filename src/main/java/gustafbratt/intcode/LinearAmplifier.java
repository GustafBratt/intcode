package gustafbratt.intcode;

import com.google.common.collect.Collections2;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class LinearAmplifier {
    private final String program;
    final private Integer[] phases;

    public LinearAmplifier(String program, Integer[] phases) {
        this.phases = phases;
        this.program = program;
    }
    private int runAndReturnOutput(){
        BigInteger carry = BigInteger.ZERO;
        for(int phase: phases){
            InputOutput inputs = new InputOutput();
            inputs.write(phase);
            inputs.write(carry);
            InputOutput out = new InputOutput();
            Computer c = new Computer(program, inputs, out);
            c.runUntilBlockedOrEnd();
            carry = out.read();
        }
        return carry.intValue();
    }
    static int findMaxOutput(Integer[] phases, String program){
        List<Integer> integers = Arrays.asList(phases);
        Collection<List<Integer>> permutations = Collections2.permutations(integers);
        int maxResult = 0;
        for(List<Integer> permutation : permutations){
            LinearAmplifier cs = new LinearAmplifier(program, permutation.toArray(new Integer[] {}));
            int result = cs.runAndReturnOutput();
            if(result > maxResult)
                maxResult = result;
        }
        return maxResult;
    }




}
