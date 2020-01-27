package gustafbratt.intcode;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.stream.Collectors;

class InputOutput {
    final LinkedList<BigInteger> list = new LinkedList<>();

    public InputOutput(int i) {
        this.write(i);
    }

    public InputOutput() {
    }

    public void write(BigInteger i){
        list.addLast(i);
    }
    public void write(int i){
        list.addLast(BigInteger.valueOf(i));
    }
    public BigInteger read() throws BlockingInputException {
        if(list.isEmpty()){
            throw new BlockingInputException();
        }
        return list.pollFirst();
    }
    public BigInteger getFinalOutput(){
        return list.getLast();
    }
    public boolean isEmpty(){
        return list.isEmpty();
    }

    @Override
    public String toString() {
        return list.stream().map(BigInteger::toString).collect(Collectors.joining(","));
    }
}
