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
                    val1 = memory.get(relativeBase.add(memory.get(pc.add(ONE))));
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
                    val2 = memory.get(relativeBase.add(memory.get(pc.add(TWO))));
                    break;
                default:
                    //ignore
            }
            switch (mode3){
                case 0: //Indirect
                case 1: //Direct
                    val3 = memory.get(pc.add(THREE));
                    break;
                case 2: //Relative
                    val3 = relativeBase.add(memory.get(pc.add(THREE)));
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

    public State runUntilBlockedOrEnd(){
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
            } catch (NullPointerException e){
                LOGGER.log(Level.SEVERE, "PC: " + pc);
                LOGGER.log(Level.SEVERE, "Instr: " + instruction);
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
        int index = 0;
        while(memory.get(BigInteger.valueOf(index))!=null) {
            Instruction instr = buildInstruction(BigInteger.valueOf(index));
            System.out.print(String.format("%-4s %4s ", index, memory.get(BigInteger.valueOf(index))));
            if(instr.opcode == null) {
                System.out.println("");
                index++;
            } else {
                System.out.println(instr);
                index += instr.opcode.length;
            }


        }
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
        memory.put(instruction.val3, instruction.val1.add(instruction.val2));
    }
    void doMul(Instruction instruction){
        memory.put(instruction.val3, instruction.val1.multiply(instruction.val2));
    }
    void doInp(Instruction instruction){
        switch (instruction.mode1) {
            case 0:
                memory.put(memory.get(pc.add(ONE)), input.read());
                break;
            case 1:
                throw new IllegalStateException("input with direct addressing " + pc);
            case 2:
                memory.put(relativeBase.add(memory.get(pc.add(ONE))), input.read());
                break;
        }
    }
    void doOut(Instruction instruction){
        output.write(instruction.val1);
    }
    void doJTR(Instruction instruction){
        if (!instruction.val1.equals(ZERO))
            pc = instruction.val2.subtract(THREE);
    }
    void doJFL(Instruction instruction){
        if (instruction.val1.equals(ZERO))
            pc = instruction.val2.subtract(BigInteger.valueOf(3));
    }
    void doLST(Instruction instruction){
        if(instruction.val1.compareTo(instruction.val2) < 0){
            memory.put(instruction.val3,ONE);
        }else{
            memory.put(instruction.val3, ZERO);
        }
    }
    void doEQS(Instruction instruction){
        if(instruction.val1.equals(instruction.val2) ){
            memory.put(instruction.val3, ONE);
        }else{
            memory.put(instruction.val3, ZERO);
        }
    }
    void doRLB(Instruction instruction){
        relativeBase = relativeBase.add(instruction.val1);
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
        switch (opcode.length){
            case 2:
                return String.format("%s %8s", opcode, val1);
            case 3:
                return String.format("%s %8s %8s", opcode, val1, val2);
            case 4:
                return String.format("%s %8s %8s %8s", opcode, val1, val2, val3);
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
