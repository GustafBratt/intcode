package gustafbratt.intcode;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;


class Day2Test {
    @Test
    public void example1() {
        Computer c = new Computer("1,9,10,3,2,3,11,0,99,30,40,50");
        c.printMemoryDump();
        c.run();
        BigInteger cellZero = c.getCellZero();
        System.out.println("Cell zero: " + cellZero);
        c.printMemoryDump();
        assertThat(cellZero).isEqualTo(BigInteger.valueOf(3500));
    }

    @Test
    public void problem1() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("day2.txt").getFile());
        String text = Files.readString(file.toPath());
        Computer c = new Computer(text);
        c.setTwoCells(12, 2);
        c.printMemoryDump();
        c.run();
        c.printMemoryDump();
        BigInteger actual = c.getCellZero();
        System.out.println(actual);
    }

    @Test
    public void problem2() throws IOException {
        int solution = 0;
        for (int noun = 0; noun < 100; noun++) {
            for (int verb = 0; verb < 100; verb++) {
                int ans = runWithNounAndVerb(noun, verb);
                if (ans == 19690720)
                    solution =  (noun * 100) + verb;
            }
        }
        System.out.println("sol " + solution);
        assertThat(solution).isEqualTo(5296);
    }
    private int runWithNounAndVerb(int noun, int verb) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("day2.txt").getFile());
        String text = Files.readString(file.toPath());
        Computer c = new Computer(text);
        c.setTwoCells(noun, verb);
        c.run();
        BigInteger actual = c.getCellZero();
        return actual.intValue();
    }
}
