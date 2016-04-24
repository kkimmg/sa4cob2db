package k_kim_mg.sa4cob2db.sql;

import k_kim_mg.sa4cob2db.CobolColumn;
import k_kim_mg.sa4cob2db.CobolRecordException;

public class DefaultSQLCobolRecordMetaData2 extends DefaultSQLCobolRecordMetaData implements SQLCobolRecordMetaData2 {

	/** Entity Name */
	private String entityName;

	/** SELECT Statement */
	private String keyReadStatement;
	/** SELECT Statement */
	private String startGEStatement;
	/** SELECT Statement */
	private String startGTStatement;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.DefaultCobolRecordMetaData#addKey(k_kim_mg.sa4cob2db
	 * .CobolColumn)
	 */
	@Override
	public void addKey(CobolColumn column) {
		super.addKey(column);
		if (getEntityName() != null) {
			this.makeKeyReadStatement();
		}
	}

	@Override
	public void setSelectStatement(String string) {
		super.setSelectStatement(string);
		setEntityName(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.sql.SQLCobolRecordMetaData2#getEntityName()
	 */
	@Override
	public String getEntityName() {
		return entityName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.sql.SQLCobolRecordMetaData2#getKeyReadStatement()
	 */
	@Override
	public String getKeyReadStatement() {
		return keyReadStatement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.sql.SQLCobolRecordMetaData2#getStartGEStatement()
	 */
	@Override
	public String getStartGEStatement() {
		return startGEStatement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.sql.SQLCobolRecordMetaData2#getStartGTStatement()
	 */
	@Override
	public String getStartGTStatement() {
		return startGTStatement;
	}

	/**
	 * make select statement
	 */
	protected void makeSelectStatement() {
		int count = getKeyCount();
		if (getEntityName() != null && count > 0) {
			StringBuffer buff = new StringBuffer("SELECT * FROM " + getEntityName() + " ORDER BY ");
			int j = 0;
			for (int i = 0; i < count; i++) {
				CobolColumn column = getKey(i);
				if (column instanceof SQLCobolColumn) {
					SQLCobolColumn sqlcolumn = (SQLCobolColumn) column;
					if (j > 0) {
						buff.append(", ");
					}
					try {
						buff.append(sqlcolumn.getOriginalColumnName());
						j++;
					} catch (CobolRecordException e) {
						// Do Nothing
					}
				}
			}

			super.setSelectStatement(buff.toString());
		}
	}

	/**
	 * make select statement
	 */
	protected void makeKeyReadStatement() {
		int count = getKeyCount();
		if (getEntityName() != null && count > 0) {
			StringBuffer buff = new StringBuffer("SELECT * FROM " + getEntityName());
			StringBuffer where = new StringBuffer(" WHERE ");
			StringBuffer order = new StringBuffer(" ORDER BY ");
			int j = 0;
			for (int i = 0; i < count; i++) {
				CobolColumn column = getKey(i);
				if (column instanceof SQLCobolColumn) {
					SQLCobolColumn sqlcolumn = (SQLCobolColumn) column;
					if (j > 0) {
						where.append(" AND ");
						order.append(", ");
					}
					try {
						where.append(sqlcolumn.getOriginalColumnName());
						where.append(" = ?");
						order.append(sqlcolumn.getOriginalColumnName());
						j++;
					} catch (CobolRecordException e) {
						// Do Nothing
					}
				}
			}
			buff.append(where);
			buff.append(order);

			setKeyReadStatement(buff.toString());
		}
	}

	/**
	 * make select statement
	 */
	protected void makeStartGEStatement() {
		int count = getKeyCount();
		if (getEntityName() != null && count > 0) {
			StringBuffer buff = new StringBuffer("SELECT * FROM " + getEntityName());
			StringBuffer where = new StringBuffer(" WHERE ");
			StringBuffer order = new StringBuffer(" ORDER BY ");
			int j = 0;
			for (int i = 0; i < count; i++) {
				CobolColumn column = getKey(i);
				if (column instanceof SQLCobolColumn) {
					SQLCobolColumn sqlcolumn = (SQLCobolColumn) column;
					if (j > 0) {
						where.append(" AND ");
						order.append(", ");
					}
					try {
						where.append(sqlcolumn.getOriginalColumnName());
						where.append(" >= ?");
						order.append(sqlcolumn.getOriginalColumnName());
						j++;
					} catch (CobolRecordException e) {
						// Do Nothing
					}
				}
			}
			buff.append(where);
			buff.append(order);

			setKeyReadStatement(buff.toString());
		}
	}

	/**
	 * make select statement
	 */
	protected void makeStartGTStatement() {
		int count = getKeyCount();
		if (getEntityName() != null && count > 0) {
			StringBuffer buff = new StringBuffer("SELECT * FROM " + getEntityName());
			StringBuffer where = new StringBuffer(" WHERE ");
			StringBuffer order = new StringBuffer(" ORDER BY ");
			int j = 0;
			for (int i = 0; i < count; i++) {
				CobolColumn column = getKey(i);
				if (column instanceof SQLCobolColumn) {
					StringBuffer sub = new StringBuffer("(");
					try {
						SQLCobolColumn sqlcolumn = (SQLCobolColumn) column;
						int l = 0;
						for (int k = 0; k < i; k++) {
							CobolColumn subcolumn = getKey(k);
							if (subcolumn instanceof SQLCobolColumn) {
								SQLCobolColumn sqlsubcolumn = (SQLCobolColumn) subcolumn;
								if (l > 0) {
									sub.append(" AND ");
									l++;
								}
								sub.append(sqlsubcolumn.getOriginalColumnName());
								sub.append(" >= ?");
							}
						}
						if (l > 0) {
							sub.append(" AND ");
						}
						sub.append(sqlcolumn.getOriginalColumnName());
						sub.append(" > ?)");
						if (j > 0) {
							where.append(" OR ");
							order.append(", ");
						}
						where.append(sub);
						order.append(sqlcolumn.getOriginalColumnName());
						j++;
					} catch (CobolRecordException e) {
						// Do Nothing
					}
				}
			}
			buff.append(where);
			buff.append(order);

			setKeyReadStatement(buff.toString());
		}
	} /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.sql.SQLCobolRecordMetaData2#setEntityName(java.lang
	 * .String)
	 */

	@Override
	public void setEntityName(String entityName) {
		this.entityName = entityName;
		this.makeKeyReadStatement();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.sql.SQLCobolRecordMetaData2#setKeyReadStatement(java
	 * .lang.String)
	 */
	@Override
	public void setKeyReadStatement(String keyReadStatement) {
		this.keyReadStatement = keyReadStatement;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.sql.SQLCobolRecordMetaData2#setStartGEStatement(java
	 * .lang.String)
	 */
	@Override
	public void setStartGEStatement(String startGEStatement) {
		this.startGEStatement = startGEStatement;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.sql.SQLCobolRecordMetaData2#setStartGTStatement(java
	 * .lang.String)
	 */
	@Override
	public void setStartGTStatement(String startGTStatement) {
		this.startGTStatement = startGTStatement;

	}
}
