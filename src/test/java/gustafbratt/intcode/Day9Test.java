package gustafbratt.intcode;

import com.google.common.io.Resources;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class Day9Test {
    @Test
    public void example2(){
        InputOutput io = new InputOutput();
        Computer c = new Computer("1102,34915192,34915192,7,4,7,99,0", null, io);
        c.printMemoryDump();
        c.run();
        BigInteger result = io.read();
        System.out.println(result);
        assertThat(result.toString().length()).isGreaterThan(15);
    }

    @Test
    public void example1(){
        String program = "109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99";
        Computer c = new Computer(program);
        c.printMemoryDump();
        c.run();
        System.out.println(c.getOutput());
        assertThat(c.getOutput().toString()).isEqualTo(program);
    }

    @Test
    public void example0() {
        InputOutput out = new InputOutput();
        Computer c = new Computer("109,19,204,-34,99", null, out);
        c.relativeBase = BigInteger.valueOf(2000);
        c.memory.put(BigInteger.valueOf(1985), BigInteger.valueOf(242));
        c.printMemoryDump();
        c.run();
        BigInteger outVal = out.read();
        assertThat(outVal).isEqualTo(242);
    }

    @Test
    public void problem1() throws IOException {
        String program = Resources.toString(Resources.getResource("day9.txt"), StandardCharsets.UTF_8);
        Computer c = new Computer(program, new InputOutput(1));
        c.printMemoryDump();
        c.run();
        System.out.println(c.getOutput());
    }
    @Test
    public void problem2() throws IOException {
        String program = Resources.toString(Resources.getResource("day9.txt"), StandardCharsets.UTF_8);
        Computer c = new Computer(program, new InputOutput(2));
        c.printMemoryDump();
        c.run();
        System.out.println(c.getOutput());
    }
}
/*Executing 0 MUL
Executing 4 LST
Executing 8 JTR
Executing 11 ADD
Executing 15 RLB
15 Adding 988 to relative base. Value: 988
Executing 17 RLB
17 Adding 3 to relative base. Value: 991
Executing 19 RLB
19 Adding 3 to relative base. Value: 994
Executing 21 RLB
21 Adding 3 to relative base. Value: 997
Executing 23 RLB
23 Adding 3 to relative base. Value: 1000
Executing 25 INP
25 Input to address 1000 + 0
Executing 27 EQS
Executing 31 JTR
Executing 65 ADD
Executing 69 MUL
Executing 73 ADD
Executing 77 ADD
Executing 81 ADD
Executing 85 MUL
Executing 89 MUL
Executing 93 MUL
Executing 97 MUL
Executing 101 MUL
Executing 105 MUL
Executing 109 ADD
Executing 113 ADD
Executing 117 ADD
Executing 121 ADD
Executing 125 MUL
Executing 129 ADD
Executing 133 ADD
Executing 137 ADD
Executing 141 ADD
Executing 145 ADD
Executing 149 MUL
Executing 153 ADD
Executing 157 MUL
Executing 161 MUL
Executing 165 MUL
Executing 169 ADD
Executing 173 ADD
Executing 177 ADD
Executing 181 ADD
Executing 185 RLB
185 Adding 9 to relative base. Value: 1009
Executing 187 EQS
Executing 191 JTR
Executing 194 ADD
Executing 198 JTR
Executing 203 MUL
Executing 207 RLB
207 Adding 3 to relative base. Value: 1012
Executing 209 ADD
Executing 213 EQS
Executing 217 JTR
Executing 220 ADD
Executing 224 JFL
Executing 229 MUL
Executing 233 RLB
233 Adding -1 to relative base. Value: 1011
Executing 235 EQS
Executing 239 JTR
Executing 242 JFL
Executing 251 MUL
Executing 255 RLB
255 Adding -11 to relative base. Value: 1000
Executing 257 ADD
Executing 261 EQS
Executing 265 JTR
Executing 268 ADD
Executing 272 JTR
Executing 277 MUL
Executing 281 RLB
281 Adding 3 to relative base. Value: 1003
Executing 283 ADD
Executing 287 EQS
Executing 291 JTR
Executing 303 MUL
Executing 307 RLB
307 Adding 16 to relative base. Value: 1019
Executing 309 EQS
Executing 313 JTR
Executing 325 MUL
Executing 329 RLB
329 Adding -4 to relative base. Value: 1015
Executing 331 MUL
Executing 335 EQS
Executing 339 JTR
Executing 342 OUT
Executing 344 ADD
Executing 348 JTR
Executing 351 MUL
Executing 355 RLB
355 Adding -15 to relative base. Value: 1000
Executing 357 MUL
Executing 361 EQS
Executing 365 JTR
Executing 368 ADD
Executing 372 JFL
Executing 377 MUL
Executing 381 RLB
381 Adding 6 to relative base. Value: 1006
Executing 383 ADD
Executing 387 EQS
Executing 391 JTR
Executing 403 MUL
Executing 407 RLB
407 Adding 8 to relative base. Value: 1014
Executing 409 MUL
Executing 413 EQS
Executing 417 JTR
Executing 425 ADD
Executing 429 MUL
Executing 433 RLB
433 Adding -1 to relative base. Value: 1013
Executing 435 EQS
Executing 439 JTR
Executing 447 ADD
Executing 451 MUL
Executing 455 RLB
455 Adding 8 to relative base. Value: 1021
Executing 457 JTR
Executing 460 JTR
Executing 469 MUL
Executing 473 RLB
473 Adding 5 to relative base. Value: 1026
Executing 475 JTR
Executing 487 MUL
Executing 491 RLB
491 Adding -33 to relative base. Value: 993
Executing 493 MUL
Executing 497 EQS
Executing 501 JTR
Executing 504 JTR
Executing 513 MUL
Executing 517 RLB
517 Adding 2 to relative base. Value: 995
Executing 519 LST
Executing 523 JTR
Executing 535 MUL
Executing 539 RLB
539 Adding 30 to relative base. Value: 1025
Executing 541 LST
Executing 545 JTR
Executing 551 OUT
Executing 553 ADD
Executing 557 MUL
Executing 561 RLB
561 Adding 2 to relative base. Value: 1027
Executing 563 JFL
Executing 566 ADD
Executing 570 JTR
Executing 575 MUL
Executing 579 RLB
579 Adding -19 to relative base. Value: 1008
Executing 581 MUL
Executing 585 EQS
Executing 589 JTR
Executing 601 MUL
Executing 605 RLB
605 Adding -2 to relative base. Value: 1006
Executing 607 LST
Executing 611 JTR
Executing 619 ADD
Executing 623 MUL
Executing 627 RLB
627 Adding 2 to relative base. Value: 1008
Executing 629 ADD
Executing 633 EQS
Executing 637 JTR
Executing 640 ADD
Executing 644 JFL
Executing 649 MUL
Executing 653 RLB
653 Adding 17 to relative base. Value: 1025
Executing 655 JTR
Executing 663 ADD
Executing 667 MUL
Executing 671 RLB
671 Adding 4 to relative base. Value: 1029
Executing 673 JTR
Executing 676 ADD
Executing 680 JFL
Executing 685 MUL
Executing 689 RLB
689 Adding -17 to relative base. Value: 1012
Executing 691 ADD
Executing 695 EQS
Executing 699 JTR
Executing 702 OUT
Executing 704 ADD
Executing 708 JTR
Executing 711 MUL
Executing 715 RLB
715 Adding 1 to relative base. Value: 1013
Executing 717 MUL
Executing 721 EQS
Executing 725 JTR
Executing 728 ADD
Executing 732 JTR
Executing 737 MUL
Executing 741 RLB
741 Adding -9 to relative base. Value: 1004
Executing 743 LST
Executing 747 JTR
Executing 750 JTR
Executing 759 MUL
Executing 763 RLB
763 Adding 23 to relative base. Value: 1027
Executing 765 JFL
Executing 777 MUL
Executing 781 RLB
781 Adding -3 to relative base. Value: 1024
Executing 783 JFL
Executing 786 JTR
Executing 795 MUL
Executing 799 RLB
799 Adding -13 to relative base. Value: 1011
Executing 801 LST
Executing 805 JTR
Executing 808 ADD
Executing 812 JTR
Executing 817 MUL
Executing 821 RLB
821 Adding -9 to relative base. Value: 1002
Executing 823 EQS
Executing 827 JTR
Executing 833 OUT
Executing 835 ADD
Executing 839 MUL
Executing 843 RLB
843 Adding -4 to relative base. Value: 998
Executing 845 EQS
Executing 849 JTR
Executing 857 ADD
Executing 861 MUL
Executing 865 RLB
865 Adding 28 to relative base. Value: 1026
Executing 867 JFL
Executing 879 MUL
Executing 883 RLB
883 Adding -4 to relative base. Value: 1022
Executing 885 LST
Executing 889 JTR
Executing 897 ADD
Executing 901 OUT
21102,21107,21101,21108,3546398493
Deprecated Gradle features were used in this build, making it incompatible with Gradle 7.0.
Use '--warning-mode all' to show the individual deprecation warnings.
See https://docs.gradle.org/6.0.1/userguide/command_line_interface.html#sec:command_line_warnings
BUILD SUCCESSFUL in 1s
6 actionable tasks: 2 executed, 4 up-to-date
11:59:10: Tasks execution finished ':cleanTest :test --tests "gustafbratt.intcode.Day9Test.problem1"'.

 */
