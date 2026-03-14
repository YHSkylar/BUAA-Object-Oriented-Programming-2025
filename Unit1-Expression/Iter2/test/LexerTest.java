import org.junit.Test;

import static org.junit.Assert.*;

public class LexerTest {

    @Test
    public void peek() {
        Lexer lexer = new Lexer("\n");
        String expected = "\n";
        assertEquals(lexer.peek(),expected);
    }
}