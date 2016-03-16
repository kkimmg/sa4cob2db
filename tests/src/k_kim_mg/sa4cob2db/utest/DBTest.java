package k_kim_mg.sa4cob2db.utest;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Properties;
import java.util.logging.Level;

import k_kim_mg.sa4cob2db.CobolFile;
import k_kim_mg.sa4cob2db.CobolRecord;
import k_kim_mg.sa4cob2db.CobolRecordMetaData;
import k_kim_mg.sa4cob2db.CobolRecordMetaDataSet;
import k_kim_mg.sa4cob2db.DefaultCobolRecord;
import k_kim_mg.sa4cob2db.FileStatus;
import k_kim_mg.sa4cob2db.sql.ACMSQLSession;
import k_kim_mg.sa4cob2db.sql.SQLCobolRecordMetaDataSet;
import k_kim_mg.sa4cob2db.sql.SQLFileServer;
import k_kim_mg.sa4cob2db.sql.SQLNetServer;
import k_kim_mg.sa4cob2db.sql.xml.NodeReadLoader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DBTest {
  SQLFileServer fileServer;
  CobolRecordMetaDataSet metaDataSet;
  ACMSQLSession session;

  /**
   * set up
   * 
   * @throws Exception
   *           exception
   */
  @Before
  public void setUp() throws Exception {
    fileServer = new SQLFileServer();
    // meta data filename
    String metaString = getEnvValue("ACM_CONFFILE", SQLNetServer.DEFAULT_CONFIG);
    File metaFile = new File(metaString);
    SQLNetServer.logger.log(Level.INFO, metaFile.getAbsolutePath());
    // meta data
    NodeReadLoader nodeLoader = new NodeReadLoader();
    metaDataSet = fileServer.getMetaDataSet();
    Properties properties = new Properties();
    nodeLoader.createMetaDataSet(metaFile, metaDataSet, properties);
    if (metaDataSet instanceof SQLCobolRecordMetaDataSet) {
      SQLCobolRecordMetaDataSet sqlset = (SQLCobolRecordMetaDataSet) metaDataSet;
      SQLNetServer.updateProperty(properties, "jdbcdriverurl", "ACM_JDBCDRIVERURL");
      SQLNetServer.updateProperty(properties, "jdbcdatabaseurl", "ACM_JDBCDATABASEURL");
      SQLNetServer.updateProperty(properties, "jdbcusername", "ACM_JDBCUSERNAME");
      SQLNetServer.updateProperty(properties, "jdbcpassword", "ACM_JDBCPASSWORD");
      sqlset.setDriverURL(properties.getProperty("jdbcdriverurl"));
      sqlset.setDatabaseURL(properties.getProperty("jdbcdatabaseurl"));
      sqlset.setUsername(properties.getProperty("jdbcusername"));
      sqlset.setPassword(properties.getProperty("jdbcpassword"));
    }
    session = new ACMSQLSession(fileServer);
  }

  /**
   * termintate
   */
  @After
  public void terminate() {
  }

  @Test
  public void testReadSeq1() {

    CobolFile file = session.createFile("dbtests");
    CobolRecordMetaData meta = metaDataSet.getMetaData("dbtests");
    // CobolRecord record = new DefaultCobolRecord(meta);
    byte[] bytes = new byte[meta.getRowSize()];
    FileStatus status = null;
    status = file.open(CobolFile.MODE_INPUT, CobolFile.ACCESS_SEQUENTIAL);
    assertEquals("Open Status", status.OK.getStatusCode(), status.getStatusCode());
    status = file.read(bytes);
    assertEquals("Read Status", status.OK.getStatusCode(), status.getStatusCode());
    status = file.close();
    assertEquals("Close Status", status.OK.getStatusCode(), status.getStatusCode());
  }

  @Test
  public void testReadKey1() throws Exception {

    CobolFile file = session.createFile("dbtests");
    CobolRecordMetaData meta = metaDataSet.getMetaData("dbtests");
    CobolRecord record = new DefaultCobolRecord(meta);
    byte[] bytes = new byte[meta.getRowSize()];
    FileStatus status = null;
    status = file.open(CobolFile.MODE_INPUT, CobolFile.ACCESS_DYNAMIC);
    assertEquals("Open Status", status.OK.getStatusCode(), status.getStatusCode());
    record.updateInt(meta.getColumn("id"), 100);
    record.getRecord(bytes);
    status = file.move(bytes);
    assertEquals("Move Status", status.OK.getStatusCode(), status.getStatusCode());
    status = file.read(bytes);
    assertEquals("Read Status", status.OK.getStatusCode(), status.getStatusCode());
    status = file.close();
    assertEquals("Close Status", status.OK.getStatusCode(), status.getStatusCode());
    record.setRecord(bytes);
    assertEquals("CD is invalid.", "00100", record.getString(meta.getColumn("cd")).trim());
  }

  @Test
  public void testReadKey2() throws Exception {

    CobolFile file = session.createFile("dbtests3");
    CobolRecordMetaData meta = metaDataSet.getMetaData("dbtests3");
    CobolRecord record = new DefaultCobolRecord(meta);
    byte[] bytes = new byte[meta.getRowSize()];
    FileStatus status = null;
    status = file.open(CobolFile.MODE_INPUT, CobolFile.ACCESS_DYNAMIC);
    assertEquals("Open Status", status.OK.getStatusCode(), status.getStatusCode());
    record.updateInt(meta.getColumn("id"), 100);
    record.getRecord(bytes);
    status = file.move(bytes);
    assertEquals("Move Status", status.OK.getStatusCode(), status.getStatusCode());
    status = file.read(bytes);
    assertEquals("Read Status", status.OK.getStatusCode(), status.getStatusCode());
    status = file.close();
    assertEquals("Close Status", status.OK.getStatusCode(), status.getStatusCode());
    record.setRecord(bytes);
    assertEquals("CD is invalid.", "00100", record.getString(meta.getColumn("cd")).trim());
  }

  @Test
  public void testStartGE1() throws Exception {

    CobolFile file = session.createFile("dbtests");
    CobolRecordMetaData meta = metaDataSet.getMetaData("dbtests");
    CobolRecord record = new DefaultCobolRecord(meta);
    byte[] bytes = new byte[meta.getRowSize()];
    FileStatus status = null;
    status = file.open(CobolFile.MODE_INPUT, CobolFile.ACCESS_DYNAMIC);
    assertEquals("Open Status", status.OK.getStatusCode(), status.getStatusCode());
    record.updateInt(meta.getColumn("id"), 100);
    record.getRecord(bytes);
    status = file.start(CobolFile.IS_GREATER_THAN_OR_EQUAL_TO, bytes);
    assertEquals("Move Status", status.OK.getStatusCode(), status.getStatusCode());
    status = file.read(bytes);
    assertEquals("Read Status", status.OK.getStatusCode(), status.getStatusCode());
    status = file.close();
    assertEquals("Close Status", status.OK.getStatusCode(), status.getStatusCode());
    record.setRecord(bytes);
    assertEquals("CD is invalid.", "00100", record.getString(meta.getColumn("cd")).trim());
  }

  @Test
  public void testStartGE2() throws Exception {

    CobolFile file = session.createFile("dbtests3");
    CobolRecordMetaData meta = metaDataSet.getMetaData("dbtests3");
    CobolRecord record = new DefaultCobolRecord(meta);
    byte[] bytes = new byte[meta.getRowSize()];
    FileStatus status = null;
    status = file.open(CobolFile.MODE_INPUT, CobolFile.ACCESS_DYNAMIC);
    assertEquals("Open Status", status.OK.getStatusCode(), status.getStatusCode());
    record.updateInt(meta.getColumn("id"), 100);
    record.getRecord(bytes);
    status = file.start(CobolFile.IS_GREATER_THAN_OR_EQUAL_TO, bytes);
    assertEquals("Move Status", status.OK.getStatusCode(), status.getStatusCode());
    status = file.read(bytes);
    assertEquals("Read Status", status.OK.getStatusCode(), status.getStatusCode());
    status = file.close();
    assertEquals("Close Status", status.OK.getStatusCode(), status.getStatusCode());
    record.setRecord(bytes);
    assertEquals("CD is invalid.", "00100", record.getString(meta.getColumn("cd")).trim());
  }

  @Test
  public void testStartGT1() throws Exception {

    CobolFile file = session.createFile("dbtests");
    CobolRecordMetaData meta = metaDataSet.getMetaData("dbtests");
    CobolRecord record = new DefaultCobolRecord(meta);
    byte[] bytes = new byte[meta.getRowSize()];
    FileStatus status = null;
    status = file.open(CobolFile.MODE_INPUT, CobolFile.ACCESS_DYNAMIC);
    assertEquals("Open Status", status.OK.getStatusCode(), status.getStatusCode());
    record.updateInt(meta.getColumn("id"), 100);
    record.getRecord(bytes);
    status = file.start(CobolFile.IS_GREATER_THAN, bytes);
    assertEquals("Move Status", status.OK.getStatusCode(), status.getStatusCode());
    status = file.read(bytes);
    assertEquals("Read Status", status.OK.getStatusCode(), status.getStatusCode());
    status = file.close();
    assertEquals("Close Status", status.OK.getStatusCode(), status.getStatusCode());
    record.setRecord(bytes);
    assertEquals("CD is invalid.", "00101", record.getString(meta.getColumn("cd")).trim());
  }

  @Test
  public void testStartGT2() throws Exception {

    CobolFile file = session.createFile("dbtests3");
    CobolRecordMetaData meta = metaDataSet.getMetaData("dbtests3");
    CobolRecord record = new DefaultCobolRecord(meta);
    byte[] bytes = new byte[meta.getRowSize()];
    FileStatus status = null;
    status = file.open(CobolFile.MODE_INPUT, CobolFile.ACCESS_DYNAMIC);
    assertEquals("Open Status", status.OK.getStatusCode(), status.getStatusCode());
    record.updateInt(meta.getColumn("id"), 100);
    record.getRecord(bytes);
    status = file.start(CobolFile.IS_GREATER_THAN, bytes);
    assertEquals("Move Status", status.OK.getStatusCode(), status.getStatusCode());
    status = file.read(bytes);
    assertEquals("Read Status", status.OK.getStatusCode(), status.getStatusCode());
    status = file.close();
    assertEquals("Close Status", status.OK.getStatusCode(), status.getStatusCode());
    record.setRecord(bytes);
    assertEquals("CD is invalid.", "00101", record.getString(meta.getColumn("cd")).trim());
  }

  /**
   * get environment value
   * 
   * @param key
   *          key
   * @param defaultValue
   *          default
   * @return value
   */
  static String getEnvValue(String key, String defaultValue) {
    String ret = System.getProperty(key, System.getenv(key));
    if (ret == null)
      ret = defaultValue;
    if (ret.length() == 0)
      ret = defaultValue;
    return ret;
  }
}
