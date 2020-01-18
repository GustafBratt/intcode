package gustafbratt.intcode;

import com.google.common.collect.Collections2;

import java.math.BigInteger;
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
        BigInteger carry = BigInteger.ZERO;
        for(int phase: phases){
            InputOutput inputs = new InputOutput();
            inputs.write(phase);
            inputs.write(carry);
            InputOutput out = new InputOutput();
            Computer c = new Computer(program, inputs, out);
            c.run();
            carry = out.read();
        }
        return carry.intValue();
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

    static InputOutput run5Computers(Integer[] phases, String program) {
        InputOutput io1 = new InputOutput();
        InputOutput io2 = new InputOutput();
        InputOutput io3 = new InputOutput();
        InputOutput io4 = new InputOutput();
        InputOutput io5 = new InputOutput();
        io1.write(phases[0]);
        io1.write(0);
        io2.write(phases[1]);
        io3.write(phases[2]);
        io4.write(phases[3]);
        io5.write(phases[4]);
        Computer ampA = new Computer(program, io1, io2);
        Computer ampB = new Computer(program, io2, io3);
        Computer ampC = new Computer(program, io3, io4);
        Computer ampD = new Computer(program, io4, io5);
        Computer ampE = new Computer(program, io5, io1);
        while(ampE.getState()!= State.ENDED){
            ampA.run();
            ampB.run();
            ampC.run();
            ampD.run();
            ampE.run();
            System.out.println("Looping");
        }
        return io1;
    }

    static int runAllCombinationsPartB(Integer[] phases, String program){
        List<Integer> integers = Arrays.asList(phases);
        Collection<List<Integer>> permutations = Collections2.permutations(integers);
        int maxResult = 0;
        for(List<Integer> permutation : permutations){
            int result = run5Computers(permutation.toArray(new Integer[] {}), program).read().intValue();
            if(result > maxResult)
                maxResult = result;
        }
        return maxResult;
    }


}
