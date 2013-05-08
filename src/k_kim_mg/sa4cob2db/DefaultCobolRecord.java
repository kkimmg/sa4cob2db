package k_kim_mg.sa4cob2db;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import k_kim_mg.sa4cob2db.sql.SQLNetServer;
/**
 * Default Cobol Record
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class DefaultCobolRecord implements CobolRecord {
	private byte[] initialrecord;
	private CobolRecordMetaData metaData;
	private byte[] record;
	private Map<CobolColumn, NumberFormat> formats = new HashMap<CobolColumn, NumberFormat>();
	/**
	 * Constructor
	 * 
	 * @param meta metadata
	 */
	public DefaultCobolRecord(CobolRecordMetaData meta) {
		setMetaData(meta);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecord#findColumn(java.lang.String)
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
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecord#getBigDecimal(k_kim_mg.sa4cob2db.CobolColumn
	 * )
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
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecord#getBoolean(k_kim_mg.sa4cob2db.CobolColumn)
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
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecord#getByte(
	 * k_kim_mg.sa4cob2db.CobolColumn)
	 */
	public byte getByte(CobolColumn column) throws CobolRecordException {
		String work = getString(column);
		if (work == null)
			work = "0";
		return Byte.parseByte(work);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecord#getBytes(.mvh
	 * k_kim_mg.sa4cob2db.CobolColumn)
	 */
	public byte[] getBytes(CobolColumn column) throws CobolRecordException {
		return getBytesDisplay(column);
	}
	/**
	 * get bytes
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecord#getBytes(.mvh
	 *      k_kim_mg.sa4cob2db.CobolColumn)
	 */
	protected byte[] getBytesAuto(CobolColumn column) throws CobolRecordException {
		byte[] ret = null;
		switch (column.getUsage()) {
		case CobolColumn.USAGE_COMP_3:
			ret = getBytesComp3(column);
			break;
		default:
			ret = getBytesDisplay(column);
			break;
		}
		return ret;
	}
	/**
	 * get comp-3 value
	 * 
	 * @param column column
	 * @return bytes array
	 * @throws CobolRecordException exception
	 */
	protected byte[] getBytesComp3(CobolColumn column) throws CobolRecordException {
		byte[] ret = new byte[column.getLength()];
		byte[] wrk = new byte[column.getPhysicalLength()];
		System.arraycopy(record, column.getStart(), wrk, 0, wrk.length);
		int i = ret.length - 1, j = wrk.length - 1;
		byte byt = wrk[j];
		byte hig = (byte) ((0x0F & byt) | 0x70);
		hig <<= 4;
		byte low = (byte) (0xF0 & byt);
		low >>>= 4;
		low &= 0x0F;
		ret[i] = (byte) (hig | low);
		i--;
		j--;
		while (i >= 0 && j >= 0) {
			byt = wrk[j];
			low = (byte) ((0x0F & byt) | 0x30);
			ret[i] = low;
			if (i > 0) {
				hig = (byte) (0xF0 & byt);
				hig >>>= 4;
				ret[i - 1] = (byte) (hig | 0x30);
			}
			i -= 2;
			j--;
		}
		return ret;
	}
	/**
	 * get display value
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecord#getBytes(.mvh
	 *      k_kim_mg.sa4cob2db.CobolColumn)
	 */
	protected byte[] getBytesDisplay(CobolColumn column) throws CobolRecordException {
		byte[] ret = new byte[column.getPhysicalLength()];
		System.arraycopy(record, column.getStart(), ret, 0, ret.length);
		return ret;
	}
	/**
	 * get column
	 * 
	 * @param columnIndex column index
	 * @return index'th column
	 */
	public CobolColumn getColumn(int columnIndex) {
		return metaData.getColumn(columnIndex);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecord#getDate(
	 * k_kim_mg.sa4cob2db.CobolColumn)
	 */
	public Date getDate(CobolColumn column) throws CobolRecordException {
		Date ret = null;
		String datestr = getString(column);
		if (datestr == null) {
			ret = null;
		} else {
			String fmt = column.getFormat();
			if (fmt == null)
				fmt = "yyyyMMdd";
			DateFormat dateformat = new SimpleDateFormat(fmt);
			try {
				ret = dateformat.parse(datestr);
				String nvl = column.getForNull();
				if (nvl != null) {
					if (datestr.equals(nvl)) {
						ret = null;
					}
				}
			} catch (ParseException e) {
				if (column.isUseOnParseError()) {
					Object val = column.getValueOfParseError();
					if (val instanceof Date) {
						ret = (Date) val;
					} else if (val != null) {
						String work = val.toString();
						try {
							ret = dateformat.parse(work);
						} catch (ParseException e1) {
							SQLNetServer.logger.log(Level.SEVERE, "Exception Of Error Value", e);
							throw new CobolRecordException();
						}
					} else {
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
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecord#getDouble(.mvh
	 * k_kim_mg.sa4cob2db.CobolColumn)
	 */
	public double getDouble(CobolColumn column) throws CobolRecordException {
		double ret = 0;
		if (isColumnFormatted(column)) {
			try {
				NumberFormat formater = getFormatter(column);
				String work = getString(column);
				if (work == null) {
					ret = 0;
				} else {
					Number number = formater.parse(work/* .trim() */);
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
			byte[] bytes = null;
			switch (column.getUsage()) {
			case CobolColumn.USAGE_COMP_3:
				bytes = getBytesComp3(column);
				break;
			default:
				bytes = getBytes(column);
				break;
			}
			for (int i = 0; i < bytes.length - 1; i++) {
				byt = bytes[i];
				ret += (byt & 0x0F);
				ret *= 10.0;
			}
			byt = bytes[bytes.length - 1];
			ret += (byt & 0x0F);
			if (column.isSigned()) {
				// singed value?
				if ((byt & 0x40) != 0) {
					ret *= (-1);
				}
			}
			for (int i = 0; i < column.getNumberOfDecimal(); i++) {
				ret /= 10.0;
			}
		}
		return ret;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecord#getFloat(.mvh
	 * k_kim_mg.sa4cob2db.CobolColumn)
	 */
	public float getFloat(CobolColumn column) throws CobolRecordException {
		float ret = 0;
		if (isColumnFormatted(column)) {
			try {
				NumberFormat formater = getFormatter(column);
				String work = getString(column);
				if (work == null) {
					ret = 0;
				} else {
					Number number = formater.parse(work/* .trim() */);
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
			byte[] bytes = null;
			switch (column.getUsage()) {
			case CobolColumn.USAGE_COMP_3:
				bytes = getBytesComp3(column);
				break;
			default:
				bytes = getBytes(column);
				break;
			}
			for (int i = 0; i < bytes.length - 1; i++) {
				byt = bytes[i];
				ret += (byt & 0x0F);
				ret *= 10.0F;
			}
			byt = bytes[bytes.length - 1];
			ret += (byt & 0x0F);
			if (column.isSigned()) {
				if ((byt & 0x40) != 0) {
					ret *= (-1);
				}
			}
			for (int i = 0; i < column.getNumberOfDecimal(); i++) {
				ret /= 10.0F;
			}
		}
		return ret;
	}
	/**
	 * get NumberFormat object
	 * 
	 * @param column column to format
	 * @return NumberFormat object if not exists create object
	 */
	protected NumberFormat getFormatter(CobolColumn column) {
		NumberFormat formater = (formats.containsKey(column) ? formats.get(column) : CobolFormat.createFormatter(column));
		return formater;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecord#getInt(k_kim_mg .acm.CobolColumn)
	 */
	public int getInt(CobolColumn column) throws CobolRecordException {
		int ret = 0;
		if (isColumnFormatted(column)) {
			try {
				NumberFormat formatter = getFormatter(column);
				String work = getString(column);
				if (work == null) {
					ret = 0;
				} else {
					Number number = formatter.parse(work/* .trim() */);
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
			byte[] bytes = null;
			switch (column.getUsage()) {
			case CobolColumn.USAGE_COMP_3:
				bytes = getBytesComp3(column);
				break;
			default:
				bytes = getBytes(column);
				break;
			}
			for (int i = 0; i < bytes.length - 1; i++) {
				byt = bytes[i];
				ret += (byt & 0x0F);
				ret *= 10;
			}
			byt = bytes[bytes.length - 1];
			ret += (byt & 0x0F);
			if (column.isSigned()) {
				if ((byt & 0x40) != 0) {
					ret *= (-1);
				}
			}
			for (int i = 0; i < column.getNumberOfDecimal(); i++) {
				ret /= 10;
			}
		}
		return ret;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecord#getLong(
	 * k_kim_mg.sa4cob2db.CobolColumn)
	 */
	public long getLong(CobolColumn column) throws CobolRecordException {
		long ret = 0;
		if (isColumnFormatted(column)) {
			try {
				NumberFormat formater = getFormatter(column);
				String work = getString(column);
				if (work == null) {
					ret = 0;
				} else {
					Number number = formater.parse(work/* .trim() */);
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
			byte[] bytes = null;
			switch (column.getUsage()) {
			case CobolColumn.USAGE_COMP_3:
				bytes = getBytesComp3(column);
				break;
			default:
				bytes = getBytes(column);
				break;
			}
			for (int i = 0; i < bytes.length - 1; i++) {
				byt = bytes[i];
				ret += (byt & 0x0F);
				ret *= 10;
			}
			byt = bytes[bytes.length - 1];
			ret += (byt & 0x0F);
			if (column.isSigned()) {
				if ((byt & 0x40) != 0) {
					ret *= (-1);
				}
			}
			for (int i = 0; i < column.getNumberOfDecimal(); i++) {
				ret /= 10;
			}
		}
		return ret;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecord#getMetaData()
	 */
	public CobolRecordMetaData getMetaData() throws CobolRecordException {
		return metaData;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecord#getRecord(byte[])
	 */
	public int getRecord(byte[] bs) throws CobolRecordException {
		int length = (record.length > bs.length ? bs.length : record.length);
		System.arraycopy(record, 0, bs, 0, length);
		return length;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecord#getShort(.mvh
	 * k_kim_mg.sa4cob2db.CobolColumn)
	 */
	public short getShort(CobolColumn column) throws CobolRecordException {
		String work = getString(column);
		if (work == null)
			return 0;
		return Short.parseShort(work);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecord#getString(.mvh
	 * k_kim_mg.sa4cob2db.CobolColumn)
	 */
	public String getString(CobolColumn column) throws CobolRecordException {
		int start = column.getStart();
		int length = column.getPhysicalLength();
		String retValue = "";
		try {
			String encode = metaData.getEncode();
			if (encode != null && encode.length() > 0) {
				retValue = new String(record, start, length, encode);
			} else {
				retValue = new String(record, start, length);
			}
		} catch (UnsupportedEncodingException uee) {
			retValue = new String(record, start, length);
			SQLNetServer.logger.log(Level.SEVERE, uee.getMessage(), uee);
		} catch (IndexOutOfBoundsException ioe) {
			retValue = "";
			SQLNetServer.logger.log(Level.SEVERE, ioe.getMessage(), ioe);
		}
		if (column.getForNull() != null) {
			if (retValue.equals(column.getForNull())) {
				retValue = null;
			}
		}
		return retValue;
	}
	/**
	 * initialize record
	 */
	public void initializeRecord() {
		System.arraycopy(initialrecord, 0, record, 0, record.length);
	}
	/**
	 * does this column have format text?
	 * 
	 * @param column column
	 * @return true yes<br>
	 *         false no
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
	 * set meta data
	 * 
	 * @param data meta data
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
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see jp.int v =
	 * bi.intValue();ne.biglobe.mvhk_kim_mg.sa4cob2db.CobolRecord
	 * #setRecord(byte[])
	 */
	public int setRecord(byte[] bs) {
		int length = (record.length > bs.length ? bs.length : record.length);
		System.arraycopy(bs, 0, record, 0, length);
		return length;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecord#updateBigDecimal(
	 * .mvhk_kim_mg.sa4cob2db.CobolColumn, java.math.BigDecimal)
	 */
	public void updateBigDecimal(CobolColumn column, BigDecimal x) throws CobolRecordException {
		updateDouble(column, x.doubleValue());
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecord#updateBoolean(
	 * .mvhk_kim_mg.sa4cob2db.CobolColumn, boolean)
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
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecord#updateByte(.mvh
	 * k_kim_mg.sa4cob2db.CobolColumn, byte)
	 */
	public void updateByte(CobolColumn column, byte x) throws CobolRecordException {
		updateLong(column, x);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecord#updateBytes(.
	 * mvhk_kim_mg.sa4cob2db.CobolColumn, byte[])
	 */
	public void updateBytes(CobolColumn column, byte[] x) throws CobolRecordException {
		updateBytesDisplay(column, x);
	}
	/**
	 * update bytes
	 * 
	 * @param column column
	 * @param x value
	 * @throws CobolRecordException exception
	 */
	protected void updateBytesAuto(CobolColumn column, byte[] x) throws CobolRecordException {
		switch (column.getUsage()) {
		case CobolColumn.USAGE_COMP_3:
			updateBytesComp3(column, x);
			break;
		default:
			updateBytesDisplay(column, x);
			break;
		}
	}
	/**
	 * update bytes (usage comp-3)
	 * 
	 * @param column column
	 * @param x value
	 * @throws CobolRecordException exception
	 */
	protected void updateBytesComp3(CobolColumn column, byte[] x) throws CobolRecordException {
		int cstart = column.getStart();
		int xlength = x.length;
		int clength = column.getPhysicalLength();
		int i = xlength - 1;
		int j = clength - 1;
		byte cmp = 0x0;
		byte low = (byte) (x[i] & 0xf0);
		low >>>= 4;
		cmp |= low;
		byte hig = (byte) (x[i] & 0x0f);
		hig <<= 4;
		cmp |= hig;
		record[cstart + j] = cmp;
		i--;
		j--;
		while (i >= 0 && j >= 0) {
			cmp = 0x0;
			low = (byte) (x[i] & 0x0f);
			cmp |= low;
			if (i > 0) {
				hig = (byte) (x[i - 1] & 0x0f);
				hig <<= 4;
				cmp |= hig;
			} else {
			}
			record[cstart + j] = cmp;
			i -= 2;
			j--;
		}
	}
	/**
	 * update byte (usage display)
	 * 
	 * @param column
	 * @param x
	 * @throws CobolRecordException
	 */
	protected void updateBytesDisplay(CobolColumn column, byte[] x) throws CobolRecordException {
		int i = 0;
		int cstart = column.getStart();
		int clength = column.getPhysicalLength();
		while (i < clength && i < x.length) {
			record[cstart + i] = x[i];
			i++;
		}
	}
	/**
	 * right justification
	 * 
	 * @param column column
	 * @param x value
	 * @throws CobolRecordException exception
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
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecord#updateDate(.mvh
	 * k_kim_mg.sa4cob2db.CobolColumn, java.util.Date)
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
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecord#updateDouble(
	 * .mvhk_kim_mg.sa4cob2db.CobolColumn, double)
	 */
	public void updateDouble(CobolColumn column, double x) throws CobolRecordException {
		if (isColumnFormatted(column)) {
			NumberFormat df = getFormatter(column);
			updateStringR(column, df.format(x));
		} else {
			boolean b = (x < 0);
			byte[] bytes = new byte[column.getLength()];
			BigDecimal bi = BigDecimal.valueOf(x).abs();
			BigDecimal bd = BigDecimal.valueOf(x).abs();
			int lob = bytes.length - column.getNumberOfDecimal();
			// bytes[0]~[lob - 1]:bytes[length - 1]
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
			//bytes[lob]~[length - 1]:bytes[length - 1]
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
			switch (column.getUsage()) {
			case CobolColumn.USAGE_COMP_3:
				updateBytesComp3(column, bytes);
				break;
			default:
				updateBytes(column, bytes);
				break;
			}
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecord#updateFloat(.
	 * mvhk_kim_mg.sa4cob2db.CobolColumn, float)
	 */
	public void updateFloat(CobolColumn column, float x) throws CobolRecordException {
		updateDouble(column, x);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecord#updateInt(.mvh
	 * k_kim_mg.sa4cob2db.CobolColumn, int)
	 */
	public void updateInt(CobolColumn column, int x) throws CobolRecordException {
		updateLong(column, x);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecord#updateLong(.mvh
	 * k_kim_mg.sa4cob2db.CobolColumn, long)
	 */
	public void updateLong(CobolColumn column, long x) throws CobolRecordException {
		int _type = column.getType();
		if (_type == CobolColumn.TYPE_DOUBLE | _type == CobolColumn.TYPE_DOUBLE) {
			updateDouble(column, x);
		} else {
			if (isColumnFormatted(column)) {
				NumberFormat df = getFormatter(column);
				String s = df.format(x);
				updateStringR(column, s);
			} else {
				boolean b = (x < 0);
				long v = (b ? -x : x);
				int len = column.getLength();
				byte[] bytes = new byte[len];
				int i = len - 1;
				bytes[i] = (byte) ((b ? 0x70 : 0x30) | (v % 10)); // [length - 1]
				v /= 10;
				i--;
				while (i >= 0) {
					// bytes[0]~bytes[length - 2]
					bytes[i] = (byte) (0x30 | (v % 10));
					v /= 10;
					i--;
				}
				switch (column.getUsage()) {
				case CobolColumn.USAGE_COMP_3:
					updateBytesComp3(column, bytes);
					break;
				default:
					updateBytes(column, bytes);
					break;
				}
			}
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecord#updateNull(.mvh
	 * k_kim_mg.sa4cob2db.CobolColumn)
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
			// unknown type
		}
		updateBytesAuto(column, x);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecord#updateObject(
	 * .mvhk_kim_mg.sa4cob2db.CobolColumn, java.lang.Object)
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
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecord#updateObject(
	 * .mvhk_kim_mg.sa4cob2db.CobolColumn, java.lang.Object, int)
	 */
	public void updateObject(CobolColumn column, Object x, int scale) throws CobolRecordException {
		updateObject(column, x);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecord#updateShort(.
	 * mvhk_kim_mg.sa4cob2db.CobolColumn, short)
	 */
	public void updateShort(CobolColumn column, short x) throws CobolRecordException {
		updateLong(column, x);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecord#updateString(
	 * .mvhk_kim_mg.sa4cob2db.CobolColumn, java.lang.String)
	 */
	public void updateString(CobolColumn column, String x) throws CobolRecordException {
		try {
			if (x == null && column.getIfNull() != null) {
				x = column.getIfNull();
			} else if (x == null) {
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
	 * right justification
	 * 
	 * @param column column
	 * @param x value
	 * @throws CobolRecordException exception
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
