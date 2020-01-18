package gustafbratt.intcode;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.TWO;
import static java.math.BigInteger.ZERO;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.BiConsumer;

public class Computer {
    Map<BigInteger, BigInteger> memory;
    InputOutput input;
    InputOutput output;
    BigInteger pc = ZERO;

    State state = State.INIT;

    public Computer(String inputString, InputOutput inputs, InputOutput outputs) {
        String[] strings = inputString.replace("\n","").split(",");
        memory = new HashMap<>();
        BigInteger[] programArray = Arrays.stream(strings).map(BigInteger::new).toArray(BigInteger[]::new);
        BigInteger index = ZERO;
        for(BigInteger cell : programArray){
            memory.put(index, cell);
            index = index.add(ONE);
        }
        this.input = inputs;
        this.output = outputs;
        this.state = State.INIT;
    }

    public Computer(String inputString) {
        this(inputString, null, null);
    }

    private Instruction buildInstruction(BigInteger pc){
        int num = memory.get(pc).intValue();
        int opcode = num % 100;
        int mode1 = (int) (Math.floor((float)num / 100) % 10);
        int mode2 = (int) (Math.floor((float)num / 1000) % 10);
        BigInteger val1 = ZERO;
        BigInteger val2 = ZERO;
        BigInteger val3 = ZERO;
        try {
            val1 = mode1 == 1 ? memory.get(pc.add(ONE)) : memory.get(memory.get(pc.add(ONE)));
            val2 = mode2 == 1 ? memory.get(pc.add(TWO)) : memory.get(memory.get(pc.add(TWO)));
            val3 = memory.get(pc.add(BigInteger.TWO).add(ONE));
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
            pc = pc.add(BigInteger.valueOf(instruction.opcode.length));
        }
    }

    public BigInteger getCellZero() {
        return memory.get(ZERO);
    }
    public State getState() {
        return state;
    }


    public void printMemoryDump(){
        System.out.println("=== DUMP ==========");
        for(BigInteger key : memory.keySet()){
            System.out.println(key + ": " + memory.get(key));
        }
/*        int index = 0;
        while(index < memory.size()){
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
        }*/
        System.out.println("=== END ===========");
    }

    public void setTwoCells(int i, int i1) {
        memory.put(ONE, BigInteger.valueOf(i));
        memory.put(TWO, BigInteger.valueOf(i1));
    }

    public BigInteger getMemCell(BigInteger i) {
        return memory.get(i);
    }

    public int getMemCell(int i) {
        return memory.get(BigInteger.valueOf(i)).intValue();
    }

}

class Instruction{
    Opcode opcode;
    BigInteger val1;
    BigInteger val2;
    BigInteger addr3;

    public Instruction(Opcode opcode, BigInteger val1, BigInteger val2, BigInteger addr3) {
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
    //ADD(1,4, (computer, instruction) -> computer.memory[instruction.addr3] = instruction.val1 + instruction.val2),
    ADD(1,4, (computer, instruction) -> computer.memory.put(instruction.addr3, instruction.val1.add(instruction.val2))),
    //MUL(2,4, (computer, instruction) -> computer.memory[instruction.addr3] = instruction.val1 * instruction.val2),
    MUL(1,4, (computer, instruction) -> computer.memory.put(instruction.addr3, instruction.val1.multiply(instruction.val2))),
    //INP(3,2, (computer, instruction) -> computer.memory[computer.memory[computer.pc + 1]] = computer.input.read()),
    INP(3,2, (computer, instruction) -> {
        //BigInteger addr = computer.pc.add(ONE);
        computer.memory.put(computer.memory.get(computer.pc.add(ONE)), computer.input.read());
    }),
    //OUT(4,2, (computer, instruction) -> computer.output.write(computer.memory[computer.memory[computer.pc+1]])),
    OUT(4,2, (computer, instruction) -> computer.output.write(computer.memory.get(computer.memory.get(computer.pc.add(ONE))))),
    //JTR(5,3, (computer, instruction) -> {if (instruction.val1!=0) computer.pc = instruction.val2 - 3;} ),
    JTR(5,3, (computer, instruction) -> {if (!instruction.val1.equals(ZERO)) computer.pc = instruction.val2.subtract(BigInteger.valueOf(3));} ),
    //JFL(6,3, (computer, instruction) -> {if (instruction.val1==0) computer.pc = instruction.val2 - 3;}),
    JFL(6,3, (computer, instruction) -> {if (instruction.val1.equals(ZERO)) computer.pc = instruction.val2.subtract(BigInteger.valueOf(3));}),
    LST(7,4,(computer, instruction) -> {
        if(instruction.val1.compareTo(instruction.val2) == -1){
            computer.memory.put(instruction.addr3,ONE);
        }else{
            computer.memory.put(instruction.addr3, ZERO);
        }
    }),
    EQS(8,4,(computer, instruction) -> {
        if(instruction.val1.equals(instruction.val2) ){
            computer.memory.put(instruction.addr3, ONE);
        }else{
            computer.memory.put(instruction.addr3, ZERO);
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
    LinkedList<BigInteger> list = new LinkedList<>();
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
