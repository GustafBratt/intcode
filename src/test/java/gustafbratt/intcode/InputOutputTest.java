package gustafbratt.intcode;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class InputOutputTest {
    @Test
    public void testIO(){
        InputOutput io = new InputOutput();
        assertThat(io.isEmpty()).isTrue();
        io.write(7);
        io.write(8);
        io.write(9);
        assertThat(io.isEmpty()).isFalse();
        assertThat(io.toString()).isEqualTo("7,8,9");
        assertThat(io.read()).isEqualTo(7);
        assertThat(io.read()).isEqualTo(8);
        assertThat(io.read()).isEqualTo(9);
        assertThat(io.isEmpty()).isTrue();

    }

}