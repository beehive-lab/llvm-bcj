package uk.ac.man.cs.llvm.ir;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import uk.ac.man.cs.llvm.ir.disassembler.LLVMDisassembler;

public abstract class AbstractTest {

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    private final String filename;

    protected AbstractTest(String filename) {
        this.filename = filename;
    }

    @Before
    public void before() {
        System.setOut(new PrintStream(output));
    }

    @After
    public void after() {
        System.setOut(null);
    }

    @Test
    public void test() {
        Path ll = Paths.get("src", "test", "resources", filename + ".ll");
        Path bc = Paths.get("src", "test", "resources", filename + ".bc");

        assertTrue(Files.exists(ll));

        try {
            Files.delete(bc);
        } catch (IOException ignore) { // probably doesn't exist
        }

        assemble(ll);

        assertTrue(Files.exists(bc));

        try {
            List<String> input = Files.readAllLines(ll);

            LLVMDisassembler.main(new String[]{bc.toAbsolutePath().toString()});

            BufferedReader reader = new BufferedReader(new StringReader(output.toString()));

            for (String line : input) {
                String out = reader.readLine();
                assertEquals(line.trim(), out.trim());
            }

            reader.close();

            Files.delete(bc);
        } catch (IOException ex) {
            fail();
        }

        assertFalse(Files.exists(bc));
    }

    public static void assemble(Path ll) {
        Runtime rt = Runtime.getRuntime();
        try {
            Process pr = rt.exec("thirdparty/llvm-3.8/bin/llvm-as " + ll.toAbsolutePath().toString());
            pr.waitFor();
            assertEquals(0, pr.exitValue());
        } catch (InterruptedException | IOException ignore) {
        }
    }
}
