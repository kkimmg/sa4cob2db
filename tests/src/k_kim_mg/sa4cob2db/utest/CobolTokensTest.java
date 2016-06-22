package k_kim_mg.sa4cob2db.utest;

import static org.junit.Assert.*;
import k_kim_mg.sa4cob2db.codegen.CobolTokens;

import org.junit.Test;

public class CobolTokensTest {

  @Test
  public void normal() {
    CobolTokens tokens = new CobolTokens("    AAAA BBBB CCCC.");
    if (tokens.hasNext()) {
      assertEquals("AAAA", tokens.next(), "AAAA");
    } else {
      fail("AAAA is not found");
    }
    if (tokens.hasNext()) {
      assertEquals("BBBB", tokens.next(), "BBBB");
    } else {
      fail("BBBB is not found");
    }
    if (tokens.hasNext()) {
      assertEquals("CCCC", tokens.next(), "CCCC");
    } else {
      fail("CCCC is not found");
    }
    if (tokens.hasNext()) {
      assertEquals(".", tokens.next(), ".");
    } else {
      fail(". is not found");
    }
    if (tokens.hasNext()) {
      fail("too much tokens");
    }

  }

  @Test
  public void dq() {
    CobolTokens tokens = new CobolTokens("    AAAA \"BBBB\" \"CC CC\".");
    if (tokens.hasNext()) {
      assertEquals("AAAA", tokens.next(), "AAAA");
    } else {
      fail("AAAA is not found");
    }
    if (tokens.hasNext()) {
      assertEquals("\"BBBB\"", tokens.next(), "\"BBBB\"");
    } else {
      fail("BBBB is not found");
    }
    if (tokens.hasNext()) {
      assertEquals("\"CC CC\"", tokens.next(), "\"CC CC\"");
    } else {
      fail("CCCC is not found");
    }
    if (tokens.hasNext()) {
      assertEquals(".", tokens.next(), ".");
    } else {
      fail(". is not found");
    }
    if (tokens.hasNext()) {
      fail("too much tokens");
    }

  }
  
  @Test
  public void sq() {
    CobolTokens tokens = new CobolTokens("    AAAA 'BBBB' 'CC CC'.");
    if (tokens.hasNext()) {
      assertEquals("AAAA", tokens.next(), "AAAA");
    } else {
      fail("AAAA is not found");
    }
    if (tokens.hasNext()) {
      assertEquals("'BBBB'", tokens.next(), "'BBBB'");
    } else {
      fail("BBBB is not found");
    }
    if (tokens.hasNext()) {
      assertEquals("'CC CC'", tokens.next(), "'CC CC'");
    } else {
      fail("CCCC is not found");
    }
    if (tokens.hasNext()) {
      assertEquals(".", tokens.next(), ".");
    } else {
      fail(". is not found");
    }
    if (tokens.hasNext()) {
      fail("too much tokens");
    }

  }
  
  @Test
  public void compute() {
    CobolTokens tokens = new CobolTokens("    COMPUTE A = B + C / (D * E)");
    if (tokens.hasNext()) {
      assertEquals("COMPUTE", tokens.next(), "COMPUTE");
    } else {
      fail("COMPUTE is not found");
    }
    if (tokens.hasNext()) {
      assertEquals("A", tokens.next(), "A");
    } else {
      fail("A is not found");
    }
    if (tokens.hasNext()) {
      assertEquals("=", tokens.next(), "=");
    } else {
      fail("= is not found");
    }
    if (tokens.hasNext()) {
      assertEquals("B", tokens.next(), "B");
    } else {
      fail("B is not found");
    }
    if (tokens.hasNext()) {
      assertEquals("+", tokens.next(), "+");
    } else {
      fail("+ is not found");
    }
    if (tokens.hasNext()) {
      assertEquals("C", tokens.next(), "C");
    } else {
      fail("C is not found");
    }
    if (tokens.hasNext()) {
      assertEquals("/", tokens.next(), "/");
    } else {
      fail("/ is not found");
    }
    if (tokens.hasNext()) {
      assertEquals("(", tokens.next(), "(");
    } else {
      fail("( is not found");
    }
    if (tokens.hasNext()) {
      assertEquals("D", tokens.next(), "D");
    } else {
      fail("D is not found");
    }
    if (tokens.hasNext()) {
      assertEquals("*", tokens.next(), "*");
    } else {
      fail("* is not found");
    }
    if (tokens.hasNext()) {
      assertEquals("E", tokens.next(), "E");
    } else {
      fail("E is not found");
    }
    if (tokens.hasNext()) {
      assertEquals(")", tokens.next(), ")");
    } else {
      fail(") is not found");
    }
    if (tokens.hasNext()) {
      fail("What?");
    }

  }
  
  @Test
  public void length() {
    String row = "000100*AAAABBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBCCCCCCCC";
    assertEquals("*AB", row.substring(6,  72), "*AAAABBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");
    assertEquals("length", row.substring(6,  72).length(), 66);
  }
}
