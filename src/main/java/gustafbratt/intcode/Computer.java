package gustafbratt.intcode;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.BiConsumer;

public class Computer {
    Integer[] memory;
    InputOutput input;
    InputOutput output;
    int pc = 0;

    State state = State.INIT;

    public Computer(String inputString, InputOutput inputs, InputOutput outputs) {
        String[] strings = inputString.replace("\n","").split(",");
        memory = Arrays.stream(strings).map(Integer::parseInt).toArray(Integer[]::new);
        this.input = inputs;
        this.output = outputs;
        this.state = State.INIT;
    }

    public Computer(String inputString) {
        this(inputString, null, null);
    }

    private Instruction buildInstruction(int pc){
        int num = memory[pc];
        int opcode = num % 100;
        int mode1 = (int) (Math.floor((float)num / 100) % 10);
        int mode2 = (int) (Math.floor((float)num / 1000) % 10);
        int val1 = -1;
        int val2 = -1;
        int val3 = -1;
        try {
            val1 = mode1 == 1 ? memory[pc + 1] : memory[memory[pc + 1]];
            val2 = mode2 == 1 ? memory[pc + 2] : memory[memory[pc + 2]];
            val3 = memory[pc+3];
        }catch(ArrayIndexOutOfBoundsException e){
            //Ignore
        }
        Opcode currentInst = Opcode.getByInt(opcode);
        return new Instruction(currentInst, val1, val2, val3);
    }

    public State run(){
        state = State.RUNNING;
        while(true) {
            Instruction instruction = buildInstruction(pc);
            if(instruction.opcode==Opcode.BYE) {
                state = State.ENDED;
                return state;
            }
            try {
                instruction.opcode.executor.accept(this, instruction);
            } catch (BlockingInputException e){
                state = State.BLOCKED;
                return state;
            }
            pc = pc + instruction.opcode.length;
        }
    }

    public int getCellZero() {
        return memory[0];
    }
    public State getState() {
        return state;
    }


    public void printMemoryDump(){
        System.out.println("=== DUMP ==========");
        int index = 0;
        while(index < memory.length){
            System.out.print(index + "\t");
            Instruction instruction = buildInstruction(index);
            Opcode currentInst = Opcode.getByInt(memory[index] % 100);
            if (null == currentInst){
                System.out.print(memory[index]);
                index++;
            }else {
                System.out.print(currentInst.toString());
                for (int i = 1; i < currentInst.length; i++) {
                    System.out.print("\t" + memory[index + i]);
                }
                index += currentInst.length;
            }
            System.out.println("");
        }
        System.out.println("=== END ===========");
    }

    public void setTwoCells(int i, int i1) {
        memory[1] = i;
        memory[2] = i1;
    }

    public int getMemCell(int i) {
        return memory[i];
    }

}

class Instruction{
    Opcode opcode;
    int val1;
    int val2;
    int addr3;

    public Instruction(Opcode opcode, int val1, int val2, int addr3) {
        this.opcode = opcode;
        this.val1 = val1;
        this.val2 = val2;
        this.addr3 = addr3;
    }

    @Override
    public String toString() {
        return "Instruction{" +
            "opcode=" + opcode +
            ", val1=" + val1 +
            ", val2=" + val2 +
            ", addr3=" + addr3 +
            '}';
    }
}

enum Opcode {
    ADD(1,4, (computer, instruction) -> computer.memory[instruction.addr3] = instruction.val1 + instruction.val2),
    MUL(2,4, (computer, instruction) -> computer.memory[instruction.addr3] = instruction.val1 * instruction.val2),
    INP(3,2, (computer, instruction) -> computer.memory[computer.memory[computer.pc + 1]] = computer.input.read()),
    OUT(4,2, (computer, instruction) -> computer.output.write(computer.memory[computer.memory[computer.pc+1]])),
    JTR(5,3, (computer, instruction) -> {if (instruction.val1!=0) computer.pc = instruction.val2 - 3;} ),
    JFL(6,3, (computer, instruction) -> {if (instruction.val1==0) computer.pc = instruction.val2 - 3;}),
    LST(7,4,(computer, instruction) -> {
        if(instruction.val1 < instruction.val2 ){
            computer.memory[instruction.addr3] = 1;
        }else{
            computer.memory[instruction.addr3] = 0;
        }
    }),
    EQS(8,4,(computer, instruction) -> {
        if(instruction.val1 == instruction.val2 ){
            computer.memory[instruction.addr3] = 1;
        }else{
            computer.memory[instruction.addr3] = 0;
        }
    }),
    BYE( 99,1,null),
    ;
    int opcode;
    int length;
    BiConsumer<Computer, Instruction> executor;
    Opcode(int opcode, int lenght, BiConsumer<Computer, Instruction> executor) {
        this.opcode = opcode;
        this.length = lenght;
        this.executor = executor;
    }

    private static Map<Integer, Opcode> opcodes = Map.of(
        1, ADD,
        2, MUL,
        3, INP,
        4, OUT,
        5, JTR,
        6, JFL,
        7, LST,
        8, EQS,
        99, BYE
    );
    public static Opcode getByInt(int i){
        return opcodes.get(i);
    }
}

class InputOutput {
    LinkedList<Integer> list = new LinkedList<>();
    public void write(int i){
        list.addLast(i);
    }
    public int read() throws BlockingInputException {
        if(list.isEmpty()){
            throw new BlockingInputException();
        }
        return list.pollFirst();
    }
    public int getFinalOutput(){
        return list.getLast();
    }
    public boolean isEmpty(){
        return list.isEmpty();
    }

    @Override
    public String toString() {
        return "InputOutput{" +
            "list=" + list +
            '}';
    }
}
enum State {
    INIT,
    RUNNING,
    BLOCKED,
    ENDED
}