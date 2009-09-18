package k_kim_mg.sa4cob2db;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import k_kim_mg.sa4cob2db.sql.SQLNetServer;
/**
 * �ǥե���ȤΥ��ܥ�쥳����
 * @author ���줪��
 */
public class DefaultCobolRecord implements CobolRecord {
	/** �쥳���ɤΥХ������� */
	private byte[] initialrecord;
	/** �᥿�ǡ��� */
	private CobolRecordMetaData metaData;
	/** �쥳���ɤΥХ������� */
	private byte[] record;
	/**
	 * ���ܥ�쥳����
	 * @param meta �᥿�ǡ���
	 */
	public DefaultCobolRecord(CobolRecordMetaData meta) {
		setMetaData(meta);
	}
	/*
	 * �󥤥�ǥå����μ��� (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecord#findColumn(java.lang.String)
	 */
	public int findColumn(String columnName) throws CobolRecordException {
		int retValue = -1;
		int columnCount = metaData.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			if (columnName.equals(getColumn(i).getName())) {
				retValue = i;
				break;
			}
		}
		if (retValue == -1) {
			throw new CobolRecordException();
		}
		return retValue;
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecord#getBigDecimal(jp.ne.biglobe
	 * .mvh.k_kim_mg.acm.CobolColumn)
	 */
	public BigDecimal getBigDecimal(CobolColumn column) throws CobolRecordException {
		String work = getString(column);
		String nvl = column.getForNull();
		if (nvl != null && work != null) {
			if (work.equals(nvl)) {
				return null;
			}
		}
		BigDecimal bd = (work != null ? new BigDecimal(work) : null);
		return bd;
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecord#getBoolean(jp.ne.biglobe.mvh
	 * .k_kim_mg.acm.CobolColumn)
	 */
	public boolean getBoolean(CobolColumn column) throws CobolRecordException {
		boolean ret = false;
		int csize = column.getPhysicalLength();
		int cstart = column.getStart();
		for (int i = 0; i < csize; i++) {
			if (record[cstart + i] != '0') {
				ret = true;
				break;
			}
		}
		return ret;
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecord#getByte(jp.ne.biglobe.mvh.
	 * k_kim_mg.acm.CobolColumn)
	 */
	public byte getByte(CobolColumn column) throws CobolRecordException {
		String work = getString(column);
		if (work == null)
			work = "0";
		return Byte.parseByte(work);
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecord#getBytes(jp.ne.biglobe.mvh
	 * .k_kim_mg.acm.CobolColumn)
	 */
	public byte[] getBytes(CobolColumn column) throws CobolRecordException {
		byte[] ret = new byte[column.getPhysicalLength()];
		System.arraycopy(record, column.getStart(), ret, 0, ret.length);
		return ret;
	}
	/**
	 * �쥳���ɤ˴ޤޤ����
	 * @param columnIndex �󥤥�ǥå���
	 * @return ����ǥå����ǻ��ꤷ����
	 */
	public CobolColumn getColumn(int columnIndex) {
		return metaData.getColumn(columnIndex);
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecord#getDate(jp.ne.biglobe.mvh.
	 * k_kim_mg.acm.CobolColumn)
	 */
	public Date getDate(CobolColumn column) throws CobolRecordException {
		Date ret = null;
		String datestr = getString(column);
		if (datestr == null) {
			// ���դ�Ϫ���٤�ʸ���󤬤��Ǥ�null
			ret = null;
		} else {
			String fmt = column.getFormat();
			if (fmt == null)
				fmt = "yyyyMMdd";
			DateFormat dateformat = new SimpleDateFormat(fmt);
			try {
				// ʸ���󤫤��Ѵ�����
				ret = dateformat.parse(datestr);
				String nvl = column.getForNull();
				if (nvl != null) {
					if (datestr.equals(nvl)) {
						// null���Ѵ����뤹��褦�����ꤵ��Ƥ���
						ret = null;
					}
				}
			} catch (ParseException e) {
				if (column.isUseOnParseError()) {
					Object val = column.getValueOfParseError();
					if (val instanceof Date) {
						// �����ͤ����ꤹ��
						ret = (Date) val;
					} else if (val != null) {
						// �����ͤ�ʸ���󤫤����ꤹ��
						String work = val.toString();
						try {
							ret = dateformat.parse(work);
						} catch (ParseException e1) {
							SQLNetServer.logger.log(Level.SEVERE, "Exception Of Error Value", e);
							throw new CobolRecordException();
						}
					} else {
						// �����ͤ�null
						ret = null;
					}
				} else {
					SQLNetServer.logger.log(Level.SEVERE, "Exception", e);
					throw new CobolRecordException();
				}
			}
		}
		return ret;
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecord#getDouble(jp.ne.biglobe.mvh
	 * .k_kim_mg.acm.CobolColumn)
	 */
	public double getDouble(CobolColumn column) throws CobolRecordException {
		double ret = 0;
		if (isColumnFormatted(column)) {
			try {
				NumberFormat formater = new DecimalFormat(column.getFormat());
				String work = getString(column);
				if (work == null) {
					ret = 0;
				} else {
					Number number = formater.parse(work);
					ret = number.doubleValue();
				}
			} catch (ParseException e) {
				if (column.isUseOnParseError()) {
					Object obj = column.getValueOfParseError();
					if (obj instanceof Number) {
						ret = ((Number) obj).doubleValue();
					} else if (obj != null) {
						ret = Double.parseDouble(obj.toString());
					} else {
						ret = 0;
					}
				} else {
					SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		} else {
			int byt;
			byte[] bytes = getBytes(column);
			for (int i = 0; i < bytes.length - 1; i++) {
				// �ƥХ��Ȥ��Ѥ߾夲
				byt = bytes[i];
				ret += (byt & 0x0F);
				ret *= 10.0;
			}
			byt = bytes[bytes.length - 1];
			ret += (byt & 0x0F);
			if (column.isSigned()) {
				// ����դ����ɤ���
				if ((byt & 0x40) != 0) {
					ret *= (-1);
				}
			}
			for (int i = 0; i < column.getNumberOfDecimal(); i++) {
				// ���������б�
				ret /= 10.0;
			}
		}
		return ret;
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecord#getFloat(jp.ne.biglobe.mvh
	 * .k_kim_mg.acm.CobolColumn)
	 */
	public float getFloat(CobolColumn column) throws CobolRecordException {
		float ret = 0;
		if (isColumnFormatted(column)) {
			try {
				NumberFormat formater = new DecimalFormat(column.getFormat());
				String work = getString(column);
				if (work == null) {
					ret = 0;
				} else {
					Number number = formater.parse(work);
					ret = number.floatValue();
				}
			} catch (ParseException e) {
				if (column.isUseOnParseError()) {
					Object obj = column.getValueOfParseError();
					if (obj instanceof Number) {
						ret = ((Number) obj).floatValue();
					} else if (obj != null) {
						ret = Float.parseFloat(obj.toString());
					} else {
						ret = 0;
					}
				} else {
					SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		} else {
			int byt;
			byte[] bytes = getBytes(column);
			for (int i = 0; i < bytes.length - 1; i++) {
				// �ƥХ��Ȥ��Ѥ߾夲
				byt = bytes[i];
				ret += (byt & 0x0F);
				ret *= 10.0F;
			}
			byt = bytes[bytes.length - 1];
			ret += (byt & 0x0F);
			if (column.isSigned()) {
				// ����դ����ɤ���
				if ((byt & 0x40) != 0) {
					ret *= (-1);
				}
			}
			for (int i = 0; i < column.getNumberOfDecimal(); i++) {
				// ���������б�
				ret /= 10.0F;
			}
		}
		return ret;
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecord#getInt(jp.ne.biglobe.mvh.k_kim_mg
	 * .acm.CobolColumn)
	 */
	public int getInt(CobolColumn column) throws CobolRecordException {
		int ret = 0;
		if (isColumnFormatted(column)) {
			try {
				NumberFormat formater = new DecimalFormat(column.getFormat());
				String work = getString(column);
				if (work == null) {
					ret = 0;
				} else {
					Number number = formater.parse(work);
					ret = number.intValue();
				}
			} catch (ParseException e) {
				if (column.isUseOnParseError()) {
					Object obj = column.getValueOfParseError();
					if (obj instanceof Number) {
						ret = ((Number) obj).intValue();
					} else if (obj != null) {
						ret = Integer.parseInt(obj.toString());
					} else {
						ret = 0;
					}
				} else {
					SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		} else {
			int byt;
			byte[] bytes = getBytes(column);
			for (int i = 0; i < bytes.length - 1; i++) {
				// �ƥХ��Ȥ��Ѥ߾夲
				byt = bytes[i];
				ret += (byt & 0x0F);
				ret *= 10;
			}
			byt = bytes[bytes.length - 1];
			ret += (byt & 0x0F);
			if (column.isSigned()) {
				// ����դ����ɤ���
				if ((byt & 0x40) != 0) {
					ret *= (-1);
				}
			}
			for (int i = 0; i < column.getNumberOfDecimal(); i++) {
				// ���������б�
				ret /= 10;
			}
		}
		return ret;
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecord#getLong(jp.ne.biglobe.mvh.
	 * k_kim_mg.acm.CobolColumn)
	 */
	public long getLong(CobolColumn column) throws CobolRecordException {
		long ret = 0;
		if (isColumnFormatted(column)) {
			try {
				NumberFormat formater = new DecimalFormat(column.getFormat());
				String work = getString(column);
				if (work == null) {
					ret = 0;
				} else {
					Number number = formater.parse(work);
					ret = number.longValue();
				}
			} catch (ParseException e) {
				if (column.isUseOnParseError()) {
					Object obj = column.getValueOfParseError();
					if (obj instanceof Number) {
						ret = ((Number) obj).longValue();
					} else if (obj != null) {
						ret = Long.parseLong(obj.toString());
					} else {
						ret = 0;
					}
				} else {
					SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		} else {
			int byt;
			byte[] bytes = getBytes(column);
			for (int i = 0; i < bytes.length - 1; i++) {
				// �ƥХ��Ȥ��Ѥ߾夲
				byt = bytes[i];
				ret += (byt & 0x0F);
				ret *= 10;
			}
			byt = bytes[bytes.length - 1];
			ret += (byt & 0x0F);
			if (column.isSigned()) {
				// ����դ����ɤ���
				if ((byt & 0x40) != 0) {
					ret *= (-1);
				}
			}
			for (int i = 0; i < column.getNumberOfDecimal(); i++) {
				// ���������б�
				ret /= 10;
			}
		}
		return ret;
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolRecord#getMetaData()
	 */
	public CobolRecordMetaData getMetaData() throws CobolRecordException {
		return metaData;
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolRecord#getRecord(byte[])
	 */
	public int getRecord(byte[] bs) throws CobolRecordException {
		int length = (record.length > bs.length ? bs.length : record.length);
		System.arraycopy(record, 0, bs, 0, length);
		return length;
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecord#getShort(jp.ne.biglobe.mvh
	 * .k_kim_mg.acm.CobolColumn)
	 */
	public short getShort(CobolColumn column) throws CobolRecordException {
		String work = getString(column);
		if (work == null)
			return 0;
		return Short.parseShort(work);
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecord#getString(jp.ne.biglobe.mvh
	 * .k_kim_mg.acm.CobolColumn)
	 */
	public String getString(CobolColumn column) throws CobolRecordException {
		int start = column.getStart();
		int length = column.getPhysicalLength();
		String retValue = "";
		try {
			String encode = metaData.getEncode();
			if (encode != null && encode.length() > 0) {
				// ���󥳡��ɻ��ꤢ��
				retValue = new String(record, start, length, encode);
			} else {
				// ���󥳡��ɻ���ʤ�
				retValue = new String(record, start, length);
			}
		} catch (UnsupportedEncodingException uee) {
			// ���󥳡��ǥ��󥰤��������ʤ�
			retValue = new String(record, start, length);
			SQLNetServer.logger.log(Level.SEVERE, uee.getMessage(), uee);
		} catch (IndexOutOfBoundsException ioe) {
			// ������ϰϳ���ȯ�������ΤǶ�ʸ������֤�
			retValue = "";
			SQLNetServer.logger.log(Level.SEVERE, ioe.getMessage(), ioe);
		}
		if (column.getForNull() != null) {
			if (retValue.equals(column.getForNull())) {
				// null���Ѵ������Ѥ˥롼�����ꤵ��Ƥ���
				retValue = null;
			}
		}
		return retValue;
	}
	/**
	 * JDBC��̥��åȤ���쥳���ɤ��Ѵ�����
	 */
	public void initializeRecord() {
		System.arraycopy(initialrecord, 0, record, 0, record.length);
	}
	/**
	 * ������Ͻ񼰤����ꤵ��Ƥ��뤫��
	 * @param column ��
	 * @return true:��ͭ��<br>
	 *         false:��̵��
	 */
	protected boolean isColumnFormatted(CobolColumn column) {
		boolean ret = false;
		if (column.getFormat() != null) {
			if (column.getFormat().trim().length() > 0) {
				ret = true;
			}
		}
		return ret;
	}
	/**
	 * ���Υ쥳���ɤΥ᥿�ǡ����򥻥åȤ���
	 * @param data �᥿�ǡ���
	 */
	public void setMetaData(CobolRecordMetaData data) {
		metaData = data;
		record = new byte[data.getRowSize()];
		initialrecord = new byte[data.getRowSize()];
		for (int i = 0; i < data.getColumnCount(); i++) {
			try {
				CobolColumn work = data.getColumn(i);
				updateNull(work);
			} catch (CobolRecordException e) {
				e.printStackTrace();
			}
		}
		System.arraycopy(record, 0, initialrecord, 0, initialrecord.length);
		// SQLNetServer.DebugPrint("�쥳����Ĺ��" + record.length);
	}
	/*
	 * (non-Javadoc)
	 * @see jp.int v =
	 * bi.intValue();ne.biglobe.mvh.k_kim_mg.acm.CobolRecord#setRecord(byte[])
	 */
	public int setRecord(byte[] bs) {
		int length = (record.length > bs.length ? bs.length : record.length);
		System.arraycopy(bs, 0, record, 0, length);
		return length;
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecord#updateBigDecimal(jp.ne.biglobe
	 * .mvh.k_kim_mg.acm.CobolColumn, java.math.BigDecimal)
	 */
	public void updateBigDecimal(CobolColumn column, BigDecimal x) throws CobolRecordException {
		updateDouble(column, x.doubleValue());
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecord#updateBoolean(jp.ne.biglobe
	 * .mvh.k_kim_mg.acm.CobolColumn, boolean)
	 */
	public void updateBoolean(CobolColumn column, boolean x) throws CobolRecordException {
		if (x) {
			updateLong(column, 1);
		} else {
			updateLong(column, 0);
		}
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecord#updateByte(jp.ne.biglobe.mvh
	 * .k_kim_mg.acm.CobolColumn, byte)
	 */
	public void updateByte(CobolColumn column, byte x) throws CobolRecordException {
		updateLong(column, x);
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecord#updateBytes(jp.ne.biglobe.
	 * mvh.k_kim_mg.acm.CobolColumn, byte[])
	 */
	public void updateBytes(CobolColumn column, byte[] x) throws CobolRecordException {
		int i = 0;
		int cstart = column.getStart();
		int clength = column.getPhysicalLength();
		while (i < clength && i < x.length) {
			record[cstart + i] = x[i];
			i++;
		}
	}
	/**
	 * �Х�����int v = bi.intValue();��򱦤���񤭹���
	 * @param column �񤭹�����
	 * @param x ��
	 * @throws CobolRecordException �󤬤ʤ��Ȥ�
	 */
	public void updateBytesR(CobolColumn column, byte[] x) throws CobolRecordException {
		int i = 0;
		int cstart = column.getStart();
		int clength = column.getPhysicalLength();
		int cend = cstart + clength;
		while (i < clength && i < x.length) {
			record[cend - i - 1] = x[x.length - i - 1];
			i++;
		}
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecord#updateDate(jp.ne.biglobe.mvh
	 * .k_kim_mg.acm.CobolColumn, java.util.Date)
	 */
	public void updateDate(CobolColumn column, Date x) throws CobolRecordException {
		if (x == null && column.getIfNull() != null) {
			updateString(column, column.getIfNull());
		} else {
			String format = column.getFormat();
			DateFormat dateformat = new SimpleDateFormat(format);
			updateString(column, dateformat.format(x));
		}
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecord#updateDouble(jp.ne.biglobe
	 * .mvh.k_kim_mg.acm.CobolColumn, double)
	 */
	public void updateDouble(CobolColumn column, double x) throws CobolRecordException {
		if (isColumnFormatted(column)) {
			DecimalFormat df = new DecimalFormat(column.getFormat());
			updateStringR(column, df.format(x));
		} else {
			boolean b = (x < 0);
			// �������������餬����Ǥ���
			byte[] bytes = new byte[column.getPhysicalLength()];
			BigDecimal bi = BigDecimal.valueOf(x).abs();
			BigDecimal bd = BigDecimal.valueOf(x).abs();
			int lob = bytes.length - column.getNumberOfDecimal();// ��������Ĺ��
			// ��������bytes[0]��[lob - 1]�����������Ǳ�ü��bytes[length - 1]
			int i = lob - 1;
			while (i >= 0) {
				BigDecimal r = bi.remainder(BigDecimal.TEN);
				int v = r.intValue();
				if ((i == (bytes.length - 1)) && column.isSigned()) {
					bytes[i] = (byte) ((b ? 0x70 : 0x30) | v);
				} else {
					bytes[i] = (byte) (0x30 | v);
				}
				bi = bi.movePointLeft(1);
				i--;
			}
			// ��������bytes[lob]��[length - 1]�����������Ǳ�ü��bytes[length - 1]
			bd = bd.movePointRight(1);
			for (i = lob; i < bytes.length; i++) {
				BigDecimal r = bd.remainder(BigDecimal.TEN);
				int v = r.intValue();
				if ((i == (bytes.length - 1)) && column.isSigned()) {
					bytes[i] = (byte) ((b ? 0x70 : 0x30) | v);
				} else {
					bytes[i] = (byte) (0x30 | v);
				}
				bd = bd.movePointRight(1);
			}
			updateBytes(column, bytes);
		}
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecord#updateFloat(jp.ne.biglobe.
	 * mvh.k_kim_mg.acm.CobolColumn, float)
	 */
	public void updateFloat(CobolColumn column, float x) throws CobolRecordException {
		updateDouble(column, x);
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecord#updateInt(jp.ne.biglobe.mvh
	 * .k_kim_mg.acm.CobolColumn, int)
	 */
	public void updateInt(CobolColumn column, int x) throws CobolRecordException {
		updateLong(column, x);
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecord#updateLong(jp.ne.biglobe.mvh
	 * .k_kim_mg.acm.CobolColumn, long)
	 */
	public void updateLong(CobolColumn column, long x) throws CobolRecordException {
		int _type = column.getType();
		if (_type == CobolColumn.TYPE_DOUBLE | _type == CobolColumn.TYPE_DOUBLE) {
			updateDouble(column, x);
		} else {
			if (isColumnFormatted(column)) {
				// �񼰲�����Ƥ���
				DecimalFormat df = new DecimalFormat(column.getFormat());
				updateStringR(column, df.format(x));
			} else {
				// �񼰤��ʤ�
				boolean b = (x < 0);
				long v = (b ? -x : x);
				int len = column.getPhysicalLength();
				byte[] bytes = new byte[len];
				int i = len - 1;
				bytes[i] = (byte) ((b ? 0x70 : 0x30) | (v % 10));// [length -
				// 1]���Ǳ�ü
				v /= 10;
				i--;
				while (i >= 0) {
					// bytes[0]��bytes[length - 2]->����������������
					bytes[i] = (byte) (0x30 | (v % 10));
					v /= 10;
					i--;
				}
				updateBytes(column, bytes);
			}
		}
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecord#updateNull(jp.ne.biglobe.mvh
	 * .k_kim_mg.acm.CobolColumn)
	 */
	public void updateNull(CobolColumn column) throws CobolRecordException {
		int _type = column.getType();
		int _size = column.getPhysicalLength();
		byte[] x = new byte[_size];
		if (_type == CobolColumn.TYPE_INTEGER || _type == CobolColumn.TYPE_FLOAT || _type == CobolColumn.TYPE_LONG || _type == CobolColumn.TYPE_DOUBLE) {
			// ---------------------------------------
			for (int i = 0; i < _size; i++) {
				x[i] = '0';
			}
		} else if (_type == CobolColumn.TYPE_DATE || _type == CobolColumn.TYPE_TIME) {
			for (int i = 0; i < _size; i++) {
				x[i] = ' ';
			}
		} else if (_type == CobolColumn.TYPE_XCHAR) {
			for (int i = 0; i < _size; i++) {
				x[i] = ' ';
			}
		} else {
			// ̤�ΤΥǡ�����
		}
		updateBytes(column, x);
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecord#updateObject(jp.ne.biglobe
	 * .mvh.k_kim_mg.acm.CobolColumn, java.lang.Object)
	 */
	public void updateObject(CobolColumn column, Object x) throws CobolRecordException {
		if (x == null && column.getIfNull() != null) {
			updateObject(column, column.getIfNull());
		} else {
			int _type = column.getType();
			if (_type == CobolColumn.TYPE_INTEGER | _type == CobolColumn.TYPE_LONG | _type == CobolColumn.TYPE_FLOAT | _type == CobolColumn.TYPE_DOUBLE) {
				// ---------------------------------------
				updateBigDecimal(column, new BigDecimal(x.toString()));
			} else if (_type == CobolColumn.TYPE_XCHAR | _type == CobolColumn.TYPE_NCHAR) {
				updateString(column, x.toString());
			} else {
			}
		}
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecord#updateObject(jp.ne.biglobe
	 * .mvh.k_kim_mg.acm.CobolColumn, java.lang.Object, int)
	 */
	public void updateObject(CobolColumn column, Object x, int scale) throws CobolRecordException {
		updateObject(column, x);
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecord#updateShort(jp.ne.biglobe.
	 * mvh.k_kim_mg.acm.CobolColumn, short)
	 */
	public void updateShort(CobolColumn column, short x) throws CobolRecordException {
		updateLong(column, x);
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecord#updateString(jp.ne.biglobe
	 * .mvh.k_kim_mg.acm.CobolColumn, java.lang.String)
	 */
	public void updateString(CobolColumn column, String x) throws CobolRecordException {
		try {
			if (x == null && column.getIfNull() != null) {
				x = column.getIfNull();
			} else {
				x = "";
			}
			String encode = metaData.getEncode();
			if (encode != null && encode.length() > 0) {
				updateBytes(column, x.getBytes(encode));
			} else {
				updateBytes(column, x.getBytes());
			}
		} catch (UnsupportedEncodingException ex) {
			updateBytes(column, x.getBytes());
		}
	}
	/**
	 * ��򱦵ͤ�ǹ�������
	 * @param column ����������
	 * @param x ����������
	 * @throws CobolRecordException �������ޤ������ʤ��ä��餷��
	 */
	public void updateStringR(CobolColumn column, String x) throws CobolRecordException {
		try {
			if (x == null && column.getIfNull() != null) {
				x = column.getIfNull();
			}
			String encode = metaData.getEncode();
			if (encode != null && encode.length() > 0) {
				updateBytesR(column, x.getBytes(encode));
			} else {
				updateBytesR(column, x.getBytes());
			}
		} catch (UnsupportedEncodingException ex) {
			updateBytesR(column, x.getBytes());
		} catch (Exception ex) {
		}
	}
}
