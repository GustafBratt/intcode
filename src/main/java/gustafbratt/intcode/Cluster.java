package gustafbratt.intcode;

public class Cluster {
    Computer[] computers;
    public Cluster(int numberOfComputers, String program, InputOutput input, InputOutput output){
        computers = new Computer[numberOfComputers];
        InputOutput previous = input;
        for(int i = 0 ; i < computers.length-1; i++){
            InputOutput next = new InputOutput();
            computers[i] = new Computer(program, previous, next);
            previous = next;
        }
        computers[computers.length] = new Computer(program, previous, output);
    }
}
