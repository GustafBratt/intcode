package gustafbratt.intcode;

import com.google.common.collect.Maps;
import static java.math.BigInteger.ONE;
import static java.math.BigInteger.TWO;
import static java.math.BigInteger.ZERO;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class Computer {
    private static final BigInteger THREE = BigInteger.valueOf(3);
    Map<BigInteger, BigInteger> memory;

    InputOutput input;
    InputOutput output;
    BigInteger pc = ZERO;

    State state = State.INIT;
    BigInteger relativeBase = ZERO;

    public Computer(String inputString, InputOutput inputs, InputOutput outputs) {
        String[] strings = inputString.replace("\n","").split(",");
        memory = new HashMap<>();
        BigInteger[] programArray = Arrays.stream(strings).map(BigInteger::new).toArray(BigInteger[]::new);
        BigInteger index = ZERO;
        for(BigInteger cell : programArray){
            memory.put(index, cell);
            index = index.add(ONE);
        }
        if(inputs==null) {
            inputs = new InputOutput();
        }
        if(outputs==null) {
            outputs = new InputOutput();
        }
        this.input = inputs;
        this.output = outputs;
    }

    public Computer(String inputString) {
        this(inputString, null, null);
    }

    public Computer(String program, InputOutput inp) {
        this(program, inp, new InputOutput());
    }

    public InputOutput getInput() {
        return input;
    }

    public InputOutput getOutput() {
        return output;
    }


    private Instruction buildInstruction(BigInteger pc){
        int num = memory.get(pc).intValue();
        int opcode = num % 100;
        int mode1 = (int) (Math.floor((float)num / 100) % 10);
        int mode2 = (int) (Math.floor((float)num / 1000) % 10);
        int mode3 = (int) (Math.floor((float)num / 10000) % 10);
        BigInteger val1 = null;
        BigInteger val2 = null;
        BigInteger val3 = null;
        try {
            switch (mode1){
                case 0: //Indirect
                    val1 = memory.get(memory.get(pc.add(ONE)));
                    if(val1 == null)
                        val1 = ZERO;
                    break;
                case 1: //Direct
                    val1 = memory.get(pc.add(ONE));
                    break;
                case 2: //Relative
                    BigInteger value = memory.get(pc.add(ONE));
                    if(value!=null) {
                        val1 = memory.get(relativeBase.add(value));
                    }
                    break;
                default:
                    //Ignore
            }
            switch (mode2){
                case 0: //Indirect
                    val2 = memory.get(memory.get(pc.add(TWO)));
                    break;
                case 1: //Direct
                    val2 = memory.get(pc.add(TWO));
                    break;
                case 2: //Relative
                    BigInteger value = memory.get(pc.add(TWO));
                    if(value!=null) {
                        val2 = memory.get(relativeBase.add(value));
                    }
                    break;
                default:
                    //ignore
            }
            switch (mode3){
                case 0: //Indirect
                //    val3 = memory.get(memory.get(pc.add(THREE)));
                //    break;
                case 1: //Direct
                    val3 = memory.get(pc.add(THREE));
                    break;
                case 2: //Relative
                    BigInteger value = memory.get(pc.add(THREE));
                    if(value!=null) {
                        val3 = relativeBase.add(value);
                    }
                    break;
                default:
                    //ignore
            }
        }catch(ArrayIndexOutOfBoundsException e){
            //Ignore
        }
        Opcode currentInst = Opcode.getByInt(opcode);
        return new Instruction(currentInst, val1, val2, val3, mode1, mode2, mode3);
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
                //System.out.println("Executing " + pc + " " + instruction.opcode);
                instruction.opcode.executor.accept(this, instruction);
            } catch (BlockingInputException e){
                state = State.BLOCKED;
                return state;
            } catch (NullPointerException e){
                System.out.println("PC: " + pc);
                System.out.println("Instr: " + instruction);
                printMemoryDump();
                throw e;
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
        System.out.println("adr\traw\t\topcode");
        int index = 0;
        while(memory.get(BigInteger.valueOf(index))!=null){
            Instruction instr = buildInstruction(BigInteger.valueOf(index));
            System.out.print(index + "\t");
            System.out.print(memory.get(BigInteger.valueOf(index)) + "\t");
            System.out.print(instr.opcode);
            if(instr.opcode == null){
                System.out.println("xxx");
                index++;
            }else {
                for (int i = 1; i < instr.opcode.length; i++) {
                    System.out.print("\t");
                    if(instr.mode1 == 0 && i==1){
                        System.out.print("*");
                    }
                    if(instr.mode1 == 2 && i==1){
                        System.out.print("+");
                    }

                    if(instr.mode2 == 0 && i==2){
                        System.out.print("*");
                    }
                    if(i==3 && instr.mode3==2){
                        System.out.print("+");
                    }
                    System.out.print(memory.get(BigInteger.valueOf(index + i)));
                }
                index += instr.opcode.length;
                System.out.println();
            }
        }
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
    BigInteger val3;
    int mode1;
    int mode2;
    int mode3;

    public Instruction(Opcode opcode, BigInteger val1, BigInteger val2, BigInteger val3, int mode1, int mode2, int mode3) {
        this.opcode = opcode;
        this.val1 = val1;
        this.val2 = val2;
        this.val3 = val3;
        this.mode1 = mode1;
        this.mode2 = mode2;
        this.mode3 = mode3;
    }

    @Override
    public String toString() {
        return "Instruction{" +
            "opcode=" + opcode +
            ", val1=" + val1 +
            ", val2=" + val2 +
            ", val3=" + val3 +
            '}';
    }
}

enum Opcode {
    ADD(1,4, (computer, instruction) -> computer.memory.put(instruction.val3, instruction.val1.add(instruction.val2))),
    MUL(2,4, (computer, instruction) -> computer.memory.put(instruction.val3, instruction.val1.multiply(instruction.val2))),
    INP(3,2, (computer, instruction) -> {
        switch (instruction.mode1) {
            case 0:
                computer.memory.put(computer.memory.get(computer.pc.add(ONE)), computer.input.read());
                break;
            case 1:
                throw new IllegalStateException("input with direct addressing " + computer.pc);
            case 2:
                System.out.println(computer.pc + " Input to address " + computer.relativeBase + " + " +computer.memory.get(computer.pc.add(ONE)) );
                computer.memory.put(computer.relativeBase.add(computer.memory.get(computer.pc.add(ONE))), computer.input.read());
                break;
        }
    }),
    OUT(4,2, (computer, instruction) -> computer.output.write(instruction.val1)),
    JTR(5,3, (computer, instruction) -> {if (!instruction.val1.equals(ZERO)) computer.pc = instruction.val2.subtract(BigInteger.valueOf(3));} ),
    JFL(6,3, (computer, instruction) -> {if (instruction.val1.equals(ZERO)) computer.pc = instruction.val2.subtract(BigInteger.valueOf(3));}),
    LST(7,4,(computer, instruction) -> {
        if(instruction.val1.compareTo(instruction.val2) < 0){
            computer.memory.put(instruction.val3,ONE);
        }else{
            computer.memory.put(instruction.val3, ZERO);
        }
    }),
    EQS(8,4,(computer, instruction) -> {
        if(instruction.val1.equals(instruction.val2) ){
            computer.memory.put(instruction.val3, ONE);
        }else{
            computer.memory.put(instruction.val3, ZERO);
        }
    }),
    RLB(9, 2, (computer, instruction) -> {
        computer.relativeBase = computer.relativeBase.add(instruction.val1);
        //System.out.println(computer.pc + " Adding " + instruction.val1 + " to relative base. Value: " + computer.relativeBase);
    }),
    BYE( 99,1,null),
    ;
    int opcodeNumber;
    int length;
    BiConsumer<Computer, Instruction> executor;
    Opcode(int opcode, int length, BiConsumer<Computer, Instruction> executor) {
        this.opcodeNumber = opcode;
        this.length = length;
        this.executor = executor;
    }

    private static final Map<Integer, Opcode> opcodes = Maps.uniqueIndex(
        Arrays.asList(Opcode.values()),
        opcode -> opcode != null ? opcode.getOpcodeNumber() : 0
    );

    public static Opcode getByInt(int i){
        return opcodes.get(i);
    }
    public int getOpcodeNumber(){
        return opcodeNumber;
    }
}

class InputOutput {
    LinkedList<BigInteger> list = new LinkedList<>();

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
enum State {
    INIT,
    RUNNING,
    BLOCKED,
    ENDED
}
