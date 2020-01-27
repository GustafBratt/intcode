package gustafbratt.intcode;

import com.google.common.collect.Maps;
import static java.math.BigInteger.ONE;
import static java.math.BigInteger.TWO;
import static java.math.BigInteger.ZERO;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Computer {
    private static final Logger LOGGER = Logger.getLogger(Computer.class.getName());

    private static final BigInteger THREE = BigInteger.valueOf(3);
    final Map<BigInteger, BigInteger> memory;

    final InputOutput input;
    final InputOutput output;
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


    public State runUntilBlockedOrEnd(){
        state = State.RUNNING;
        while(true) {
            Instruction instruction = new Instruction(this, pc);
            if(instruction.opcode==Opcode.BYE) {
                state = State.ENDED;
                return state;
            }
            try {
                instruction.opcode.executor.accept(this, instruction);
            } catch (BlockingInputException e){
                state = State.BLOCKED;
                return state;
            } catch (NullPointerException e){
                LOGGER.log(Level.SEVERE, "PC: " + pc);
                LOGGER.log(Level.SEVERE, "Instr: " + instruction);
                LOGGER.log(Level.SEVERE, getRawMemoryDump());
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


    public String getRawMemoryDump() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== DUMP ==========\n");
        for (BigInteger key : memory.keySet()) {
            sb.append(key).append("\t").append(memory.get(key)).append("\n");
        }
        sb.append("=== END ===========\n");
        return sb.toString();
    }

    public String getDisassembledDump(){
        StringBuilder sb = new StringBuilder();
        int index = 0;
        while(memory.get(BigInteger.valueOf(index))!=null) {
            try {
                Instruction instr = new Instruction(this, BigInteger.valueOf(index));
                sb.append(String.format("%-4s %05d ", index, memory.get(BigInteger.valueOf(index))));
                sb.append(instr).append("\n");
                index += instr.opcode.length;
            }catch(IllegalArgumentException e){
                sb.append(String.format("%-4s %5s ", index, memory.get(BigInteger.valueOf(index))));
                sb.append("\n");
                index++;
            }

        }
        return sb.toString();
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

    void doAdd(Instruction instruction){
        memory.put(instruction.writes[2], instruction.reads[0].add(instruction.reads[1]));
    }
    void doMul(Instruction instruction){
        memory.put(instruction.writes[2], instruction.reads[0].multiply(instruction.reads[1]));
    }
    void doInp(Instruction instruction){
        memory.put(instruction.writes[0], input.read());
    }
    void doOut(Instruction instruction){
        output.write(instruction.reads[0]);
    }
    void doJTR(Instruction instruction){
        if (!instruction.reads[0].equals(ZERO))
            pc = instruction.reads[1].subtract(THREE);
    }
    void doJFL(Instruction instruction){
        if (instruction.reads[0].equals(ZERO))
            pc = instruction.reads[1].subtract(BigInteger.valueOf(3));
    }
    void doLST(Instruction instruction){
        if(instruction.reads[0].compareTo(instruction.reads[1]) < 0){
            memory.put(instruction.writes[2],ONE);
        }else{
            memory.put(instruction.writes[2], ZERO);
        }
    }
    void doEQS(Instruction instruction){
        if(instruction.reads[0].equals(instruction.reads[1]) ){
            memory.put(instruction.writes[2], ONE);
        }else{
            memory.put(instruction.writes[2], ZERO);
        }
    }
    void doRLB(Instruction instruction){
        relativeBase = relativeBase.add(instruction.reads[0]);
    }
}

class Instruction{
    final Opcode opcode;
    final BigInteger[] writes;
    final BigInteger[] reads;
    final BigInteger[] raws; //For debugging purposes
    final int[] modes;

    Instruction(Computer computer, BigInteger base){
        Map<BigInteger, BigInteger> memory = computer.memory;
        BigInteger relativeBase = computer.relativeBase;
        int num = memory.get(base).intValue();
        opcode = Opcode.getByInt(num % 100);
        if(opcode==null)
            throw new IllegalArgumentException("Cant build opcode of " + num);
        modes = new int[3];
        reads = new BigInteger[3];
        writes = new BigInteger[3];
        raws = new BigInteger[3];
        modes[0] = (int) (Math.floor((float)num / 100) % 10);
        modes[1] = (int) (Math.floor((float)num / 1000) % 10);
        modes[2] = (int) (Math.floor((float)num / 10000) % 10);
        for(int offset = 0; offset < 3; offset++){
            BigInteger rawValue = base.add(BigInteger.valueOf(offset + 1));
            raws[offset] = memory.get(rawValue);
            switch (modes[offset]){
                case 0: //Indirect
                    writes[offset] = memory.get(rawValue);
                    reads[offset] = memory.get(memory.get(rawValue));
                    if(reads[offset] == null){ //Lazy initialize all memory to 0
                        reads[offset] = ZERO;
                    }
                    break;
                case 1: //Direct
                    reads[offset] = memory.get(rawValue);
                    break;
                case 2: //Relative
                    writes[offset] = relativeBase.add(memory.get(rawValue));
                    reads[offset] = memory.get(relativeBase.add(memory.get(rawValue)));
                default:
                    //Ignore
            }

        }
    }


    @Override
    public String toString() {
        String[] mods = {"*","","+"};

        switch (opcode.length){
            case 2:
                return String.format("%s %9s", opcode, mods[modes[0]] + raws[0]);
            case 3:
                return String.format("%s %9s %9s", opcode, mods[modes[0]] + raws[0], mods[modes[1]] + raws[1]);
            case 4:
                return String.format("%s %9s %9s %9s", opcode, mods[modes[0]] + raws[0], mods[modes[1]] + raws[1], mods[modes[2]] + raws[2]);
            default:
                return opcode.toString();
        }

    }
}

enum Opcode {
    ADD(1,4, Computer::doAdd),
    MUL(2,4, Computer::doMul),
    INP(3,2, Computer::doInp),
    OUT(4,2, Computer::doOut),
    JTR(5,3, Computer::doJTR),
    JFL(6,3, Computer::doJFL),
    LST(7,4, Computer::doLST),
    EQS(8,4, Computer::doEQS),
    RLB(9, 2, Computer::doRLB),
    BYE( 99,1,null),
    ;

    final int opcodeNumber;
    final int length;
    final BiConsumer<Computer, Instruction> executor;

    Opcode(int opcode, int length, BiConsumer<Computer, Instruction> executor) {
        this.opcodeNumber = opcode;
        this.length = length;
        this.executor = executor;
    }

    private static final Map<Integer, Opcode> opcodes = Maps.uniqueIndex(
        Arrays.asList(Opcode.values()),
        opcode -> opcode.opcodeNumber
    );

    public static Opcode getByInt(int i){
        return opcodes.get(i);
    }

}

enum State {
    INIT,
    RUNNING,
    BLOCKED,
    ENDED
}
