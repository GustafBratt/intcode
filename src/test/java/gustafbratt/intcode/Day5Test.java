package gustafbratt.intcode;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Day5Test {
    @Test
    public void problem1() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("day5.txt").getFile());
        String text = Files.readString(file.toPath());
        InputOutput inputs = new InputOutput();
        InputOutput outputs = new InputOutput();
        inputs.write(1);
        Computer c = new Computer(text, inputs, outputs);
        c.run();
        System.out.println("Final output:" );
        System.out.println(outputs.getFinalOutput());
        assertThat(outputs.getFinalOutput()).isEqualTo(16209841);
    }
    @Test
    public void example1(){
        Computer c = new Computer("1002,4,3,4,33");
        c.run();
        assertThat(c.getMemCell(4)).isEqualTo(99);
    }

    @Test
    void example2(){
        Computer c = new Computer("1101,100,-1,4,0");
        c.printMemoryDump();
        c.run();
        c.printMemoryDump();
        assertThat(c.getMemCell(4)).isEqualTo(99);
    }

    @Test
    void problem2() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("day5.txt").getFile());
        String text = Files.readString(file.toPath());
        InputOutput inputs = new InputOutput();
        InputOutput outputs = new InputOutput();
        inputs.write(5);
        Computer c = new Computer(text, inputs, outputs);
        c.run();
        System.out.println("Final output:" );
        System.out.println(outputs.getFinalOutput());
        assertThat(outputs.getFinalOutput()).isEqualTo(8834787);
    }
}
