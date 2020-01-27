package gustafbratt.intcode;

import com.google.common.collect.Collections2;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class FeedbackAmplifier {

    static InputOutput runAmplifiers(Integer[] phases, String program) {
        Computer[] computers = new Computer[phases.length];
        InputOutput latestIo = new InputOutput();
        for(int i = 0; i < computers.length - 1; i++){
            latestIo.write(phases[i]);
            computers[i] = new Computer(program, latestIo, new InputOutput());
            latestIo = computers[i].getOutput();
        }
        latestIo.write(phases[computers.length-1]);
        computers[computers.length-1] = new Computer(program, latestIo, computers[0].getInput());
        computers[0].getInput().write(0);

        while(computers[computers.length - 1].getState()!= State.ENDED) {
            for(Computer computer : computers){
                computer.runUntilBlockedOrEnd();
            }
        }
        return computers[computers.length-1].getOutput();
    }

    static int findMaxOutput(Integer[] phases, String program){
        List<Integer> integers = Arrays.asList(phases);
        Collection<List<Integer>> permutations = Collections2.permutations(integers);
        int maxResult = 0;
        for(List<Integer> permutation : permutations){
            int result = runAmplifiers(permutation.toArray(new Integer[] {}), program).read().intValue();
            if(result > maxResult)
                maxResult = result;
        }
        return maxResult;
    }
}
