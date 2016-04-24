package k_kim_mg.sa4cob2db.utest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import k_kim_mg.sa4cob2db.CobolColumn;
import k_kim_mg.sa4cob2db.CobolRecord;
import k_kim_mg.sa4cob2db.CobolRecordException;
import k_kim_mg.sa4cob2db.CobolRecordMetaData;
import k_kim_mg.sa4cob2db.DefaultCobolColumn;
import k_kim_mg.sa4cob2db.DefaultCobolRecord;
import k_kim_mg.sa4cob2db.DefaultCobolRecordMetaData;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit Test
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class TypesTest {
  CobolRecordMetaData meta;
  CobolRecord record;

  /**
   * set up
   * 
   * @throws Exception
   *           exception
   */
  @Before
  public void setUp() throws Exception {
    // meta
    meta = new DefaultCobolRecordMetaData();
    meta.setName("TEST");
    meta.addAlias("ALIAS1");
    meta.addAlias("ALIAS2");
    meta.setEncode(null);
    // prepare column
    CobolColumn column;
    // columns
    // TYPE-A
    column = new DefaultCobolColumn(meta);
    column.setName("TYPE-A");
    column.setLength(4);
    column.setStart(0);
    column.setType(CobolColumn.TYPE_XCHAR);
    meta.addColumn(column);
    // TYPE-X
    column = new DefaultCobolColumn(meta);
    column.setName("TYPE-X");
    column.setLength(10);
    column.setStart(4);
    column.setType(CobolColumn.TYPE_XCHAR);
    meta.addColumn(column);
    // TYPE-9
    column = new DefaultCobolColumn(meta);
    column.setName("TYPE-9");
    column.setLength(18);
    column.setStart(14);
    column.setType(CobolColumn.TYPE_INTEGER);
    meta.addColumn(column);
    // TYPE-SP
    column = new DefaultCobolColumn(meta);
    column.setName("TYPE-SP");
    column.setLength(5);
    column.setSigned(true);
    column.setStart(32);
    column.setType(CobolColumn.TYPE_INTEGER);
    meta.addColumn(column);
    // TYPE-SM
    column = new DefaultCobolColumn(meta);
    column.setName("TYPE-SM");
    column.setLength(5);
    column.setSigned(true);
    column.setStart(37);
    column.setType(CobolColumn.TYPE_INTEGER);
    meta.addColumn(column);
    // TYPE-V
    column = new DefaultCobolColumn(meta);
    column.setName("TYPE-V");
    column.setNumberOfDecimal(2);
    column.setLength(3);
    column.setStart(42);
    column.setType(CobolColumn.TYPE_FLOAT);
    meta.addColumn(column);
    // TYPE-P
    column = new DefaultCobolColumn(meta);
    column.setName("TYPE-P");
    column.setLength(2);
    column.setStart(45);
    column.setType(CobolColumn.TYPE_INTEGER);
    meta.addColumn(column);
    // TYPE-0
    column = new DefaultCobolColumn(meta);
    column.setName("TYPE-0");
    column.setLength(4);
    column.setStart(47);
    column.setType(CobolColumn.TYPE_XCHAR);
    meta.addColumn(column);
    // TYPE-XB
    column = new DefaultCobolColumn(meta);
    column.setName("TYPE-XB");
    column.setLength(3);
    column.setStart(51);
    column.setType(CobolColumn.TYPE_XCHAR);
    meta.addColumn(column);
    // TYPE-CONMA
    column = new DefaultCobolColumn(meta);
    column.setName("TYPE-CONMA");
    column.setFormat("0,000");
    column.setLength(5);
    column.setStart(54);
    column.setType(CobolColumn.TYPE_INTEGER);
    meta.addColumn(column);
    // TYPE-SLASH
    column = new DefaultCobolColumn(meta);
    column.setName("TYPE-SLASH");
    column.setFormat("00/00");
    column.setLength(5);
    column.setStart(59);
    column.setType(CobolColumn.TYPE_INTEGER);
    meta.addColumn(column);
    // TYPE-PERIOD
    column = new DefaultCobolColumn(meta);
    column.setName("TYPE-PERIOD");
    column.setNumberOfDecimal(1);
    column.setFormat("0.00");
    column.setLength(4);
    column.setStart(64);
    column.setType(CobolColumn.TYPE_FLOAT);
    meta.addColumn(column);
    // TYPE-PLUS
    column = new DefaultCobolColumn(meta);
    column.setName("TYPE-PLUS");
    column.setFormat("+000");
    column.setLength(4);
    column.setStart(68);
    column.setType(CobolColumn.TYPE_INTEGER);
    meta.addColumn(column);
    // TYPE-MINUS
    column = new DefaultCobolColumn(meta);
    column.setName("TYPE-MINUS");
    column.setFormat("-000");
    column.setLength(4);
    column.setStart(72);
    column.setType(CobolColumn.TYPE_INTEGER);
    meta.addColumn(column);
    // TYPE-CR1
    column = new DefaultCobolColumn(meta);
    column.setName("TYPE-CR1");
    column.setFormat("0CR");
    column.setLength(3);
    column.setStart(76);
    column.setType(CobolColumn.TYPE_INTEGER);
    meta.addColumn(column);
    // TYPE-DB1
    column = new DefaultCobolColumn(meta);
    column.setName("TYPE-DB1");
    column.setFormat("0DB");
    column.setLength(3);
    column.setStart(79);
    column.setType(CobolColumn.TYPE_INTEGER);
    meta.addColumn(column);
    // TYPE-CR2
    column = new DefaultCobolColumn(meta);
    column.setName("TYPE-CR2");
    column.setFormat("0CR");
    column.setLength(3);
    column.setStart(82);
    column.setType(CobolColumn.TYPE_INTEGER);
    meta.addColumn(column);
    // TYPE-DB2
    column = new DefaultCobolColumn(meta);
    column.setName("TYPE-DB2");
    column.setFormat("0DB");
    column.setLength(3);
    column.setStart(85);
    column.setType(CobolColumn.TYPE_INTEGER);
    meta.addColumn(column);
    // TYPE-Z
    column = new DefaultCobolColumn(meta);
    column.setName("TYPE-Z");
    column.setFormat("###0");
    column.setLength(4);
    column.setStart(88);
    column.setType(CobolColumn.TYPE_INTEGER);
    meta.addColumn(column);
    // TYPE-AST
    column = new DefaultCobolColumn(meta);
    column.setName("TYPE-AST");
    column.setFormat("***0");
    column.setLength(4);
    column.setStart(92);
    column.setType(CobolColumn.TYPE_INTEGER);
    meta.addColumn(column);
    // TYPE-FOM1
    column = new DefaultCobolColumn(meta);
    column.setName("TYPE-FOM1");
    column.setFormat("0,000");
    column.setLength(5);
    column.setStart(96);
    column.setType(CobolColumn.TYPE_INTEGER);
    meta.addColumn(column);
    // TYPE-FOM2
    column = new DefaultCobolColumn(meta);
    column.setName("TYPE-FOM2");
    column.setFormat("0,000");
    column.setLength(5);
    column.setStart(101);
    column.setType(CobolColumn.TYPE_INTEGER);
    meta.addColumn(column);
    // TYPE-FOM3
    column = new DefaultCobolColumn(meta);
    column.setName("TYPE-FOM3");
    column.setFormat("+000");
    column.setLength(4);
    column.setStart(106);
    column.setType(CobolColumn.TYPE_INTEGER);
    meta.addColumn(column);
    // TYPE-FOM4
    column = new DefaultCobolColumn(meta);
    column.setName("TYPE-FOM4");
    column.setFormat("-000");
    column.setLength(4);
    column.setStart(110);
    column.setType(CobolColumn.TYPE_INTEGER);
    meta.addColumn(column);
    // TYPE-FOM8
    column = new DefaultCobolColumn(meta);
    column.setName("TYPE-FOM8");
    column.setFormat("##0");
    column.setLength(3);
    column.setStart(114);
    column.setType(CobolColumn.TYPE_INTEGER);
    meta.addColumn(column);
    // TYPE-FOM9
    column = new DefaultCobolColumn(meta);
    column.setName("TYPE-FOM9");
    column.setFormat("#,##0");
    column.setLength(5);
    column.setStart(117);
    column.setType(CobolColumn.TYPE_INTEGER);
    meta.addColumn(column);
    // TYPE-DISP
    column = new DefaultCobolColumn(meta);
    column.setName("TYPE-DISP");
    column.setLength(3);
    column.setSigned(true);
    column.setStart(122);
    column.setType(CobolColumn.TYPE_INTEGER);
    meta.addColumn(column);
    // TYPE-PACKED-DECIMAL
    column = new DefaultCobolColumn(meta);
    column.setName("TYPE-PACKED-DECIMAL");
    column.setLength(3);
    column.setSigned(true);
    column.setStart(125);
    column.setType(CobolColumn.TYPE_INTEGER);
    column.setUsage(CobolColumn.USAGE_COMP_3);
    meta.addColumn(column);
    // TYPE-COMP-3
    column = new DefaultCobolColumn(meta);
    column.setName("TYPE-COMP-3");
    column.setLength(4);
    column.setSigned(true);
    column.setStart(128);
    column.setType(CobolColumn.TYPE_INTEGER);
    column.setUsage(CobolColumn.USAGE_COMP_3);
    meta.addColumn(column);
    // TYPE-FOM5
    column = new DefaultCobolColumn(meta);
    column.setName("TYPE-FOM5");
    column.setFormat("\u00A4#,##9");
    column.setLength(7);
    column.setStart(132);
    column.setType(CobolColumn.TYPE_INTEGER);
    meta.addColumn(column);
    // TYPE-FOM7
    column = new DefaultCobolColumn(meta);
    column.setName("TYPE-FOM7");
    column.setFormat("\u00A4#,###,##9");
    column.setLength(12);
    column.setStart(137);
    column.setType(CobolColumn.TYPE_INTEGER);
    meta.addColumn(column);
    // TYPE-DECIMAL
    column = new DefaultCobolColumn(meta);
    column.setName("TYPE-DECIMAL");
    column.setNumberOfDecimal(2);
    column.setLength(6);
    column.setStart(143);
    column.setType(CobolColumn.TYPE_DECIMAL);
    meta.addColumn(column);
    // TYPE-DECIMAL
    column = new DefaultCobolColumn(meta);
    column.setName("TYPE-DECIMAL2");
    column.setNumberOfDecimal(2);
    column.setFormat("-00.00");
    column.setLength(6);
    column.setStart(149);
    column.setType(CobolColumn.TYPE_DECIMAL);
    meta.addColumn(column);
    // TYPE-BINARY
    column = new DefaultCobolColumn(meta);
    column.setName("TYPE-BINARY5");
    column.setLength(4);
    column.setStart(155);
    column.setType(CobolColumn.TYPE_INTEGER);
    column.setUsage(CobolColumn.USAGE_BINARY);
    meta.addColumn(column);
    // TYPE-BINARY2
    column = new DefaultCobolColumn(meta);
    column.setName("TYPE-BINARY18");
    column.setLength(18);
    column.setStart(159);
    column.setType(CobolColumn.TYPE_LONG);
    column.setUsage(CobolColumn.USAGE_BINARY);
    meta.addColumn(column);
    // Record
    record = new DefaultCobolRecord(meta);
  }

  /**
   * 0
   */
  @Test
  public void testTYPE_0() {
    CobolColumn column;
    String x = "AB00";
    try {
      x = "AB00";
      column = meta.getColumn("TYPE-0");
      record.updateString(column, x);
      assertEquals(column.getName() + " failed(" + record.getString(column) + ")", x,
          record.getString(column));
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * 9
   */
  @Test
  public void testTYPE_9() {
    CobolColumn column;
    int i = 5678;
    try {
      column = meta.getColumn("TYPE-9");
      record.updateInt(column, i);
      assertEquals(column.getName() + " failed(" + record.getString(column) + ")", i,
          record.getInt(column));
      assertEquals(column.getName() + " failed(" + column.getFormat() + ")", "000000000000005678",
          record.getString(column));
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * A
   */
  @Test
  public void testTYPE_A() {
    CobolColumn column;
    String x = "ABCD";
    try {
      column = meta.getColumn("TYPE-A");
      record.updateString(column, x);
      assertEquals(column.getName() + " failed(" + record.getString(column) + ")", x,
          record.getString(column));
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * ,
   */
  @Test
  public void testTYPE_CONMA() {
    CobolColumn column;
    int i = 5678;
    try {
      column = meta.getColumn("TYPE-CONMA");
      record.updateInt(column, i);
      assertEquals(column.getName() + " failed(" + record.getString(column) + ")", i,
          record.getInt(column));
      assertEquals(column.getName() + " failed(" + column.getFormat() + ")", "5,678",
          record.getString(column));
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * P
   */
  @Test
  public void testTYPE_P() {
    CobolColumn column;
    int i = 99;
    try {
      column = meta.getColumn("TYPE-P");
      record.updateInt(column, i);
      assertEquals(column.getName() + " failed(" + record.getString(column) + ")", i,
          record.getInt(column));
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * S9
   */
  @Test
  public void testTYPE_SM() {
    CobolColumn column;
    int i = -5678;
    try {
      column = meta.getColumn("TYPE-SM");
      record.updateInt(column, i);
      assertEquals(column.getName() + " failed(" + record.getString(column) + ")", i,
          record.getInt(column));
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * S9
   */
  @Test
  public void testTYPE_SP() {
    CobolColumn column;
    int i = 5678;
    try {
      column = meta.getColumn("TYPE-SP");
      record.updateInt(column, i);
      assertEquals(column.getName() + " failed(" + record.getString(column) + ")", i,
          record.getInt(column));
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * V
   */
  @Test
  public void testTYPE_V() {
    CobolColumn column;
    float f = 5.67f;
    try {
      column = meta.getColumn("TYPE-V");
      record.updateFloat(column, f);
      // System.out.println(column.getName() + " failed(" +
      // record.getString(column) + ")" + ":" + 0.0f + ":" + f + ":" +
      // record.getFloat(column));
      assertEquals(column.getName() + " failed(" + record.getString(column) + ")", 0.0f, f,
          record.getFloat(column));
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * X
   */
  @Test
  public void testTYPE_X() {
    CobolColumn column;
    String x = "ABCD";
    try {
      x = "ABCD      ";
      column = meta.getColumn("TYPE-X");
      record.updateString(column, x);
      assertEquals(column.getName() + " failed(" + record.getString(column) + ")", x,
          record.getString(column));
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * B
   */
  @Test
  public void testTYPE_XB() {
    CobolColumn column;
    String x = "X X";
    try {
      x = "X X";
      column = meta.getColumn("TYPE-XB");
      record.updateString(column, x);
      assertEquals(column.getName() + " failed(" + record.getString(column) + ")", x,
          record.getString(column));
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * /
   */
  @Test
  public void testTYPE_SLASH() {
    CobolColumn column;
    int i = 5678;
    try {
      column = meta.getColumn("TYPE-SLASH");
      record.updateInt(column, i);
      assertEquals(column.getName() + " failed(" + record.getString(column) + ")", i,
          record.getInt(column));
      assertEquals(column.getName() + " failed(" + column.getFormat() + ")", "56/78",
          record.getString(column));
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * .
   */
  @Test
  public void testTYPE_PERIOD() {
    CobolColumn column;
    float f = 5.67f;
    try {
      column = meta.getColumn("TYPE-PERIOD");
      record.updateFloat(column, f);
      assertEquals(column.getName() + " failed(" + record.getString(column) + ")", 0.0f, f,
          record.getFloat(column));
      assertEquals(column.getName() + " failed(" + column.getFormat() + ")", "5.67",
          record.getString(column));
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * +
   */
  @Test
  public void testTYPE_PLUS() {
    CobolColumn column;
    int i = 567;
    try {
      column = meta.getColumn("TYPE-PLUS");
      record.updateInt(column, i);
      assertEquals(column.getName() + " failed(" + record.getString(column) + ")", i,
          record.getInt(column));
      assertEquals(column.getName() + " failed(" + column.getFormat() + ")", "+567",
          record.getString(column));
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * -
   */
  @Test
  public void testTYPE_MINUS() {
    CobolColumn column;
    int i = -567;
    try {
      column = meta.getColumn("TYPE-MINUS");
      record.updateInt(column, i);
      assertEquals(column.getName() + " failed(" + record.getString(column) + ")", i,
          record.getInt(column));
      assertEquals(column.getName() + " failed(" + column.getFormat() + ")", "-567",
          record.getString(column));
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * CR
   */
  @Test
  public void testTYPE_CR1() {
    CobolColumn column;
    int i = 5;
    try {
      column = meta.getColumn("TYPE-CR1");
      record.updateInt(column, i);
      assertEquals(column.getName() + " failed(" + record.getString(column) + ")", i,
          record.getInt(column));
      assertEquals(column.getName() + " failed(" + column.getFormat() + ")", "5  ",
          record.getString(column));
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * DB
   */
  @Test
  public void testTYPE_DB1() {
    CobolColumn column;
    int i = 5;
    try {
      column = meta.getColumn("TYPE-DB1");
      record.updateInt(column, i);
      assertEquals(column.getName() + " failed(" + record.getString(column) + ")", i,
          record.getInt(column));
      assertEquals(column.getName() + " failed(" + column.getFormat() + ")", "5  ",
          record.getString(column));
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * CR
   */
  @Test
  public void testTYPE_CR2() {
    CobolColumn column;
    int i = -5;
    try {
      column = meta.getColumn("TYPE-CR2");
      record.updateInt(column, i);
      assertEquals(column.getName() + " failed(" + record.getString(column) + ")", i,
          record.getInt(column));
      assertEquals(column.getName() + " failed(" + column.getFormat() + ")", "5CR",
          record.getString(column));
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * DB
   */
  @Test
  public void testTYPE_DB2() {
    CobolColumn column;
    int i = -5;
    try {
      column = meta.getColumn("TYPE-DB2");
      record.updateInt(column, i);
      assertEquals(column.getName() + " failed(" + record.getString(column) + ")", i,
          record.getInt(column));
      assertEquals(column.getName() + " failed(" + column.getFormat() + ")", "5DB",
          record.getString(column));
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * Z
   */
  @Test
  public void testTYPE_Z() {
    CobolColumn column;
    int i = 567;
    try {
      column = meta.getColumn("TYPE-Z");
      record.updateInt(column, i);
      assertEquals(column.getName() + " failed(" + record.getString(column) + ")", i,
          record.getInt(column));
      assertEquals(column.getName() + " failed(" + column.getFormat() + ")", " 567",
          record.getString(column));
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * *
   */
  @Test
  public void testTYPE_AST() {
    CobolColumn column;
    int i = 567;
    try {
      column = meta.getColumn("TYPE-AST");
      record.updateInt(column, i);
      assertEquals(column.getName() + " failed(" + record.getString(column) + ")", i,
          record.getInt(column));
      assertEquals(column.getName() + " failed(" + column.getFormat() + ")", "*567",
          record.getString(column));
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * 9,999
   */
  @Test
  public void testTYPE_FOM1() {
    CobolColumn column;
    int i = 5678;
    try {
      column = meta.getColumn("TYPE-FOM1");
      record.updateInt(column, i);
      assertEquals(column.getName() + " failed(" + record.getString(column) + ")", i,
          record.getInt(column));
      assertEquals(column.getName() + " failed(" + column.getFormat() + ")", "5,678",
          record.getString(column));
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * 9,000
   */
  @Test
  public void testTYPE_FOM2() {
    CobolColumn column;
    int i = 8000;
    try {
      column = meta.getColumn("TYPE-FOM2");
      record.updateInt(column, i);
      assertEquals(column.getName() + " failed(" + record.getString(column) + ")", i,
          record.getInt(column));
      assertEquals(column.getName() + " failed(" + column.getFormat() + ")", "8,000",
          record.getString(column));
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * +999
   */
  @Test
  public void testTYPE_FOM3() {
    CobolColumn column;
    int i = 56;
    try {
      column = meta.getColumn("TYPE-FOM3");
      record.updateInt(column, i);
      assertEquals(column.getName() + " failed(" + record.getString(column) + ")", i,
          record.getInt(column));
      assertEquals(column.getName() + " failed(" + column.getFormat() + ")", "+056",
          record.getString(column));
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
    i = -56;
    try {
      column = meta.getColumn("TYPE-FOM3");
      record.updateInt(column, i);
      assertEquals(column.getName() + " failed(" + record.getString(column) + ")", i,
          record.getInt(column));
      assertEquals(column.getName() + " failed(" + column.getFormat() + ")", "-056",
          record.getString(column));
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * -999
   */
  @Test
  public void testTYPE_FOM4() {
    CobolColumn column;
    int i = -56;
    try {
      column = meta.getColumn("TYPE-FOM4");
      record.updateInt(column, i);
      assertEquals(column.getName() + " failed(" + record.getString(column) + ")", i,
          record.getInt(column));
      assertEquals(column.getName() + " failed(" + column.getFormat() + ")", "-056",
          record.getString(column));
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * \u00A4999
   */
  @Test
  public void testTYPE_FOM5() {
    String cMark = Currency.getInstance(Locale.getDefault()).getSymbol();
    CobolColumn column;
    int i = 567;
    try {
      column = meta.getColumn("TYPE-FOM5");
      record.updateInt(column, i);
      assertEquals(column.getName() + " failed(" + record.getString(column) + ")", i,
          record.getInt(column));
      assertEquals(column.getName() + " failed(" + column.getFormat() + ")",
          (cMark + "567").trim(), record.getString(column).trim());
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
    i = 5678;
    try {
      column = meta.getColumn("TYPE-FOM5");
      record.updateInt(column, i);
      assertEquals(column.getName() + " failed(" + record.getString(column) + ")", i,
          record.getInt(column));
      assertEquals(column.getName() + " failed(" + column.getFormat() + ")",
          (cMark + "5,678").trim(), (record.getString(column)).trim());
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * \u00A49,999
   */
  @Test
  public void testTYPE_FOM7() {
    String cMark = Currency.getInstance(Locale.getDefault()).getSymbol();
    CobolColumn column;
    int i = 12345678;
    try {
      column = meta.getColumn("TYPE-FOM7");
      record.updateInt(column, i);
      assertEquals(column.getName() + " failed(" + record.getString(column) + ")", i,
          record.getInt(column));
      assertEquals(column.getName() + " failed(" + column.getFormat() + ")",
          (cMark + "12,345,678").trim(), (record.getString(column)).trim());
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }

  }

  /**
   * ZZZ
   */
  @Test
  public void testTYPE_FOM8() {
    CobolColumn column;
    int i = 56;
    try {
      column = meta.getColumn("TYPE-FOM8");
      record.updateInt(column, i);
      assertEquals(column.getName() + " failed(" + record.getString(column) + ")", i,
          record.getInt(column));
      assertEquals(column.getName() + " failed(" + column.getFormat() + ")", " 56",
          record.getString(column));
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * 9,999
   */
  @Test
  public void testTYPE_FOM9() {
    CobolColumn column;
    int i = 5678;
    try {
      column = meta.getColumn("TYPE-FOM9");
      record.updateInt(column, i);
      assertEquals(column.getName() + " failed(" + record.getString(column) + ")", i,
          record.getInt(column));
      assertEquals(column.getName() + " failed(" + column.getFormat() + ")", "5,678",
          record.getString(column));
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * USAGE DISPLAY
   */
  @Test
  public void testTYPE_DISP() {
    CobolColumn column;
    int i = 567;
    try {
      column = meta.getColumn("TYPE-DISP");
      record.updateInt(column, i);
      assertEquals(column.getName() + " failed(" + record.getString(column) + ")", i,
          record.getInt(column));
      assertEquals(column.getName() + " failed(" + column.getFormat() + ")", "567",
          record.getString(column));
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * USAGE COMP-3
   */
  @Test
  public void testTYPE_PACKED_DECIMAL() {
    CobolColumn column;
    int i = 567;
    try {
      column = meta.getColumn("TYPE-PACKED-DECIMAL");
      record.updateInt(column, i);
      assertEquals(column.getName() + " failed", i, record.getInt(column));
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
    i = -567;
    try {
      column = meta.getColumn("TYPE-PACKED-DECIMAL");
      record.updateInt(column, i);
      assertEquals(column.getName() + " failed", i, record.getInt(column));
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * USAGE COMP-3
   */
  @Test
  public void testTYPE_COMP_3() {
    CobolColumn column;
    int i = 5678;
    try {
      column = meta.getColumn("TYPE-COMP-3");
      record.updateInt(column, i);
      assertEquals(column.getName() + " failed ", i, record.getInt(column));
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
    i = -5678;
    try {
      column = meta.getColumn("TYPE-COMP-3");
      record.updateInt(column, i);
      assertEquals(column.getName() + " failed ", i, record.getInt(column));
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * DECIMAL
   */
  @Test
  public void testTYPE_DECIMAL() {
    CobolColumn column;
    BigDecimal f = new BigDecimal(345.67f);
    try {
      column = meta.getColumn("TYPE-DECIMAL");
      record.updateBigDecimal(column, f);
      assertEquals(column.getName() + " failed(" + record.getString(column) + ")", 0.0d,
          f.doubleValue(), record.getBigDecimal(column).doubleValue());
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
    f = new BigDecimal(-345.67f);
    try {
      column = meta.getColumn("TYPE-DECIMAL");
      record.updateBigDecimal(column, f);
      assertEquals(column.getName() + " failed(" + record.getString(column) + ")", 0.0d,
          f.doubleValue(), record.getBigDecimal(column).doubleValue());
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * DECIMAL
   */
  @Test
  public void testTYPE_DECIMAL2() {
    CobolColumn column;
    BigDecimal f = BigDecimal.valueOf(345.67d);
    try {
      column = meta.getColumn("TYPE-DECIMAL2");
      record.updateBigDecimal(column, f);
      NumberFormat nf = new DecimalFormat(column.getFormat());
      assertEquals(column.getName() + " failed(" + record.getString(column) + ")", nf.format(f),
          nf.format(record.getBigDecimal(column)));
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
    f = BigDecimal.valueOf(-45.67d);
    try {
      column = meta.getColumn("TYPE-DECIMAL2");
      record.updateBigDecimal(column, f);
      NumberFormat nf = new DecimalFormat(column.getFormat());
      assertEquals(column.getName() + " failed(" + record.getString(column) + ")", nf.format(f),
          nf.format(record.getBigDecimal(column)));
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * BINARY(5)
   */
  @Test
  public void testTYPE_BINARY5() {
    CobolColumn column;
    int i = 5678;
    try {
      column = meta.getColumn("TYPE-BINARY5");
      record.updateInt(column, i);
      assertEquals(column.getName(), i, record.getInt(column));
      byte[] bytes = record.getBytes(column);
      assertEquals(column.getName(), bytes[0], 0x01);
      assertEquals(column.getName(), bytes[1], 0x06);
      assertEquals(column.getName(), bytes[2], 0x02);
      assertEquals(column.getName(), bytes[3], 0x0e);
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
    i = -5678;
    try {
      column = meta.getColumn("TYPE-BINARY5");
      record.updateInt(column, i);
      assertEquals(column.getName(), i, record.getInt(column));
      byte[] bytes = record.getBytes(column);
      assertEquals(column.getName(), ~bytes[0] & 0x0F, 0x01);
      assertEquals(column.getName(), ~bytes[1] & 0x0F, 0x06);
      assertEquals(column.getName(), ~bytes[2] & 0x0F, 0x02);
      assertEquals(column.getName(), (~bytes[3] & 0x0F) + 1, 0x0e);
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * BINARY(18)
   */
  @Test
  public void testTYPE_BINARY18() {
    CobolColumn column;
    long i = 123456789012345678L;
    try {
      column = meta.getColumn("TYPE-BINARY18");
      record.updateLong(column, i);
      assertEquals(column.getName(), i, record.getLong(column));
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
    i = -12345678901234567L;
    try {
      column = meta.getColumn("TYPE-BINARY18");
      record.updateLong(column, i);
      assertEquals(column.getName(), i, record.getLong(column));
    } catch (CobolRecordException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }
}
