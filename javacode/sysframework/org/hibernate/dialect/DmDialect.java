package org.hibernate.dialect;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.MappingException;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.dialect.lock.LockingStrategy;
import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
import org.hibernate.dialect.lock.OptimisticLockingStrategy;
import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
import org.hibernate.dialect.lock.PessimisticReadSelectLockingStrategy;
import org.hibernate.dialect.lock.PessimisticWriteSelectLockingStrategy;
import org.hibernate.dialect.lock.SelectLockingStrategy;
import org.hibernate.exception.internal.SQLStateConverter;
import org.hibernate.exception.spi.SQLExceptionConverter;
import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
import org.hibernate.id.SequenceGenerator;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.persister.entity.Lockable;
import org.hibernate.type.StandardBasicTypes;


public class DmDialect extends Dialect {

	public DmDialect() {
		registerColumnType(1, "CHAR($l)");
		registerColumnType(12, "VARCHAR($l)");
		registerColumnType(-1, "TEXT");
		registerColumnType(2005, "TEXT");
		registerColumnType(-7, "BIT");
		registerColumnType(16, "BIT");
		registerColumnType(-6, "TINYINT");
		registerColumnType(5, "SMALLINT");
		registerColumnType(4, "INTEGER");
		registerColumnType(-5, "BIGINT");
		registerColumnType(7, "FLOAT");
		registerColumnType(6, "FLOAT");
		registerColumnType(8, "DOUBLE");
		registerColumnType(3, "DECIMAL");
		registerColumnType(2, "DECIMAL");
		registerColumnType(-2, "BINARY");
		registerColumnType(-3, "BLOB");
		registerColumnType(-4, "BLOB");
		registerColumnType(2004, "BLOB");
		registerColumnType(91, "DATE");
		registerColumnType(92, "TIME");
		registerColumnType(93, "DATETIME");

		registerKeyword("last");
		registerKeyword("size");
		registerHibernateType(5, StandardBasicTypes.SHORT.getName());

		registerFunction("abs", new StandardSQLFunction("abs"));
		registerFunction("asin", new StandardSQLFunction("asin", StandardBasicTypes.DOUBLE));

		registerFunction("acos", new StandardSQLFunction("acos", StandardBasicTypes.DOUBLE));

		registerFunction("atan", new StandardSQLFunction("atan", StandardBasicTypes.DOUBLE));

		registerFunction("atan2", new StandardSQLFunction("atan2", StandardBasicTypes.DOUBLE));

		registerFunction("ceil", new StandardSQLFunction("ceil", StandardBasicTypes.INTEGER));

		registerFunction("ceiling", new StandardSQLFunction("ceiling", StandardBasicTypes.INTEGER));

		registerFunction("cos", new StandardSQLFunction("cos", StandardBasicTypes.DOUBLE));

		registerFunction("cot", new StandardSQLFunction("cot", StandardBasicTypes.DOUBLE));

		registerFunction("cosh", new StandardSQLFunction("cosh", StandardBasicTypes.DOUBLE));

		registerFunction("degrees", new StandardSQLFunction("degrees"));
		registerFunction("exp", new StandardSQLFunction("exp", StandardBasicTypes.DOUBLE));

		registerFunction("GREATEST", new StandardSQLFunction("GREATEST", StandardBasicTypes.DOUBLE));

		registerFunction("floor", new StandardSQLFunction("floor", StandardBasicTypes.INTEGER));

		registerFunction("ln", new StandardSQLFunction("ln", StandardBasicTypes.DOUBLE));
		registerFunction("log", new StandardSQLFunction("log", StandardBasicTypes.DOUBLE));

		registerFunction("log10", new StandardSQLFunction("log10", StandardBasicTypes.DOUBLE));

		registerFunction("mod", new StandardSQLFunction("mod", StandardBasicTypes.INTEGER));

		registerFunction("pi", new NoArgSQLFunction("pi", StandardBasicTypes.DOUBLE));
		registerFunction("power", new StandardSQLFunction("power", StandardBasicTypes.DOUBLE));

		registerFunction("radians", new StandardSQLFunction("radians"));
		registerFunction("rand", new NoArgSQLFunction("rand", StandardBasicTypes.DOUBLE));
		registerFunction("round", new StandardSQLFunction("round"));
		registerFunction("sign", new StandardSQLFunction("sign", StandardBasicTypes.INTEGER));

		registerFunction("sin", new StandardSQLFunction("sin", StandardBasicTypes.DOUBLE));

		registerFunction("sinh", new StandardSQLFunction("sinh", StandardBasicTypes.DOUBLE));

		registerFunction("sqrt", new StandardSQLFunction("sqrt", StandardBasicTypes.DOUBLE));

		registerFunction("tan", new StandardSQLFunction("tan", StandardBasicTypes.DOUBLE));

		registerFunction("tanh", new StandardSQLFunction("tanh", StandardBasicTypes.DOUBLE));

		registerFunction("trunc", new StandardSQLFunction("trunc"));
		registerFunction("truncate", new StandardSQLFunction("truncate"));

		registerFunction("stddev", new StandardSQLFunction("stddev", StandardBasicTypes.DOUBLE));
		registerFunction("variance", new StandardSQLFunction("variance", StandardBasicTypes.DOUBLE));

		registerFunction("concat", new VarArgsSQLFunction(StandardBasicTypes.STRING, "", "||", ""));
		registerFunction("ascii", new StandardSQLFunction("ascii", StandardBasicTypes.INTEGER));

		registerFunction("bit_length", new StandardSQLFunction("bit_length", StandardBasicTypes.LONG));

		registerFunction("char", new StandardSQLFunction("char", StandardBasicTypes.CHARACTER));

		registerFunction("difference", new StandardSQLFunction("difference", StandardBasicTypes.INTEGER));

		registerFunction("LENGTH", new StandardSQLFunction("LENGTH", StandardBasicTypes.INTEGER));

		registerFunction("char_length", new StandardSQLFunction("char_length", StandardBasicTypes.LONG));

		registerFunction("character_length", new StandardSQLFunction("character_length", StandardBasicTypes.LONG));

		registerFunction("chr", new StandardSQLFunction("chr", StandardBasicTypes.CHARACTER));

		registerFunction("initcap", new StandardSQLFunction("initcap", StandardBasicTypes.STRING));

		registerFunction("insert", new StandardSQLFunction("insert", StandardBasicTypes.STRING));

		registerFunction("insstr", new StandardSQLFunction("insstr", StandardBasicTypes.STRING));

		registerFunction("instr", new StandardSQLFunction("instr", StandardBasicTypes.LONG));

		registerFunction("SUBSTRING", new StandardSQLFunction("SUBSTRING", StandardBasicTypes.STRING));

		registerFunction("instrb", new StandardSQLFunction("instrb", StandardBasicTypes.LONG));

		registerFunction("lcase", new StandardSQLFunction("lcase", StandardBasicTypes.STRING));

		registerFunction("left", new StandardSQLFunction("left", StandardBasicTypes.STRING));

		registerFunction("leftstr", new StandardSQLFunction("leftstr", StandardBasicTypes.STRING));

		registerFunction("len", new StandardSQLFunction("len", StandardBasicTypes.INTEGER));

		registerFunction("LENGTHB", new StandardSQLFunction("LENGTHB", StandardBasicTypes.INTEGER));

		registerFunction("octet_length", new StandardSQLFunction("octet_length", StandardBasicTypes.LONG));

		registerFunction("locate", new StandardSQLFunction("locate", StandardBasicTypes.LONG));

		registerFunction("lower", new StandardSQLFunction("lower", StandardBasicTypes.STRING));

		registerFunction("lpad", new StandardSQLFunction("lpad", StandardBasicTypes.STRING));

		registerFunction("ltrim", new StandardSQLFunction("ltrim", StandardBasicTypes.STRING));

		registerFunction("position", new StandardSQLFunction("position", StandardBasicTypes.INTEGER));

		registerFunction("INS", new StandardSQLFunction("INS", StandardBasicTypes.STRING));

		registerFunction("repeat", new StandardSQLFunction("repeat", StandardBasicTypes.STRING));

		registerFunction("REPLICATE", new StandardSQLFunction("REPLICATE", StandardBasicTypes.STRING));

		registerFunction("STUFF", new StandardSQLFunction("STUFF", StandardBasicTypes.STRING));

		registerFunction("repeatstr", new StandardSQLFunction("repeatstr", StandardBasicTypes.STRING));

		registerFunction("replace", new StandardSQLFunction("replace", StandardBasicTypes.STRING));

		registerFunction("reverse", new StandardSQLFunction("reverse", StandardBasicTypes.STRING));

		registerFunction("right", new StandardSQLFunction("right", StandardBasicTypes.STRING));

		registerFunction("rightstr", new StandardSQLFunction("rightstr", StandardBasicTypes.STRING));

		registerFunction("rpad", new StandardSQLFunction("rpad", StandardBasicTypes.STRING));

		registerFunction("TO_NUMBER", new StandardSQLFunction("TO_NUMBER"));

		registerFunction("rtrim", new StandardSQLFunction("rtrim", StandardBasicTypes.STRING));

		registerFunction("soundex", new StandardSQLFunction("soundex", StandardBasicTypes.STRING));

		registerFunction("space", new StandardSQLFunction("space", StandardBasicTypes.STRING));

		registerFunction("substr", new StandardSQLFunction("substr", StandardBasicTypes.STRING));

		registerFunction("substrb", new StandardSQLFunction("substrb", StandardBasicTypes.STRING));

		registerFunction("to_char", new StandardSQLFunction("to_char", StandardBasicTypes.STRING));

		registerFunction("STRPOSDEC", new StandardSQLFunction("STRPOSDEC", StandardBasicTypes.STRING));

		registerFunction("STRPOSINC", new StandardSQLFunction("STRPOSINC", StandardBasicTypes.STRING));

		registerFunction("VSIZE", new StandardSQLFunction("VSIZE", StandardBasicTypes.INTEGER));

		registerFunction("translate", new StandardSQLFunction("translate", StandardBasicTypes.STRING));

		registerFunction("trim", new StandardSQLFunction("trim", StandardBasicTypes.STRING));

		registerFunction("ucase", new StandardSQLFunction("ucase", StandardBasicTypes.STRING));

		registerFunction("upper", new StandardSQLFunction("upper", StandardBasicTypes.STRING));

		registerFunction("OVERLAPS", new StandardSQLFunction("OVERLAPS"));
		registerFunction("DATEPART", new StandardSQLFunction("DATEPART"));
		registerFunction("DATE_PART", new StandardSQLFunction("DATE_PART"));
		registerFunction("add_days", new StandardSQLFunction("add_days"));
		registerFunction("add_months", new StandardSQLFunction("add_months"));
		registerFunction("add_weeks", new StandardSQLFunction("add_weeks"));
		registerFunction("curdate", new NoArgSQLFunction("curdate", StandardBasicTypes.DATE));

		registerFunction("curtime", new NoArgSQLFunction("curtime", StandardBasicTypes.TIME));

		registerFunction("current_date", new NoArgSQLFunction("current_date", StandardBasicTypes.DATE));

		registerFunction("current_time", new NoArgSQLFunction("current_time", StandardBasicTypes.TIME));

		registerFunction("current_timestamp", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIMESTAMP));

		registerFunction("dateadd", new StandardSQLFunction("dateadd", StandardBasicTypes.TIMESTAMP));

		registerFunction("CUR_TICK_TIME", new StandardSQLFunction("CUR_TICK_TIME"));
		registerFunction("datediff", new StandardSQLFunction("datediff", StandardBasicTypes.INTEGER));

		registerFunction("datepart", new StandardSQLFunction("datepart", StandardBasicTypes.INTEGER));

		registerFunction("dayname", new StandardSQLFunction("dayname", StandardBasicTypes.STRING));

		registerFunction("dayofmonth", new StandardSQLFunction("dayofmonth", StandardBasicTypes.INTEGER));

		registerFunction("dayofweek", new StandardSQLFunction("dayofweek", StandardBasicTypes.INTEGER));

		registerFunction("dayofyear", new StandardSQLFunction("dayofyear", StandardBasicTypes.INTEGER));

		registerFunction("days_between", new StandardSQLFunction("days_between", StandardBasicTypes.INTEGER));

		registerFunction("extract", new StandardSQLFunction("extract"));
		registerFunction("getdate", new StandardSQLFunction("getdate", StandardBasicTypes.TIMESTAMP));

		registerFunction("hour", new StandardSQLFunction("hour", StandardBasicTypes.INTEGER));

		registerFunction("LOCALTIMESTAMP", new StandardSQLFunction("LOCALTIMESTAMP"));
		registerFunction("NOW", new StandardSQLFunction("NOW"));
		registerFunction("last_day", new StandardSQLFunction("last_day"));
		registerFunction("minute", new StandardSQLFunction("minute", StandardBasicTypes.INTEGER));

		registerFunction("month", new StandardSQLFunction("month", StandardBasicTypes.INTEGER));

		registerFunction("monthname", new StandardSQLFunction("monthname", StandardBasicTypes.STRING));

		registerFunction("months_between", new StandardSQLFunction("months_between"));

		registerFunction("GREATEST", new StandardSQLFunction("GREATEST", StandardBasicTypes.DATE));

		registerFunction("TO_DATETIME", new StandardSQLFunction("TO_DATETIME"));
		registerFunction("next_day", new StandardSQLFunction("next_day"));
		registerFunction("quarter", new StandardSQLFunction("quarter", StandardBasicTypes.INTEGER));

		registerFunction("second", new StandardSQLFunction("second", StandardBasicTypes.INTEGER));

		registerFunction("round", new StandardSQLFunction("round"));
		registerFunction("timestampadd", new StandardSQLFunction("timestampadd", StandardBasicTypes.TIMESTAMP));

		registerFunction("timestampdiff", new StandardSQLFunction("timestampdiff", StandardBasicTypes.INTEGER));

		registerFunction("BIGDATEDIFF", new StandardSQLFunction("BIGDATEDIFF", StandardBasicTypes.BIG_INTEGER));

		registerFunction("sysdate", new StandardSQLFunction("sysdate", StandardBasicTypes.TIME));

		registerFunction("LEAST", new StandardSQLFunction("LEAST"));

		registerFunction("trunc", new StandardSQLFunction("trunc"));
		registerFunction("week", new StandardSQLFunction("week", StandardBasicTypes.INTEGER));

		registerFunction("weekday", new StandardSQLFunction("weekday", StandardBasicTypes.INTEGER));

		registerFunction("weeks_between", new StandardSQLFunction("weeks_between", StandardBasicTypes.INTEGER));

		registerFunction("year", new StandardSQLFunction("year", StandardBasicTypes.INTEGER));

		registerFunction("years_between", new StandardSQLFunction("years_between", StandardBasicTypes.INTEGER));

		registerFunction("to_date", new StandardSQLFunction("to_date", StandardBasicTypes.TIMESTAMP));
		registerFunction("systimestamp", new NoArgSQLFunction("systimestamp", StandardBasicTypes.TIMESTAMP));

		registerFunction("coalesce", new StandardSQLFunction("coalesce"));
		registerFunction("ifnull", new StandardSQLFunction("ifnull"));
		registerFunction("isnull", new StandardSQLFunction("isnull"));
		registerFunction("nullif", new StandardSQLFunction("nullif"));
		registerFunction("nvl", new StandardSQLFunction("nvl"));

		registerFunction("str", new StandardSQLFunction("to_char", StandardBasicTypes.STRING));
		registerFunction("decode", new StandardSQLFunction("decode"));

		registerFunction("cur_database", new StandardSQLFunction("cur_database", StandardBasicTypes.STRING));
		registerFunction("page", new StandardSQLFunction("page", StandardBasicTypes.INTEGER));

		registerFunction("sessid", new StandardSQLFunction("sessid", StandardBasicTypes.LONG));

		registerFunction("uid", new StandardSQLFunction("uid", StandardBasicTypes.LONG));
		registerFunction("user", new StandardSQLFunction("user", StandardBasicTypes.STRING));

		registerFunction("vsize", new StandardSQLFunction("vsize", StandardBasicTypes.INTEGER));

		registerFunction("tabledef", new StandardSQLFunction("tabledef", StandardBasicTypes.STRING));

		getDefaultProperties().setProperty("hibernate.use_outer_join", "true");
		getDefaultProperties().setProperty("hibernate.jdbc.batch_size", "0");
	}

	public boolean supportsIdentityColumns() {
		return true;
	}

	public boolean supportsInsertSelectIdentity() {
		return true;
	}

	public boolean hasDataTypeInIdentityColumn() {
		return true;
	}

	public String getIdentitySelectString() throws MappingException {
		return "select scope_identity()";
	}

	public String appendIdentitySelectToInsert(String insertString) {
		return insertString + " select scope_identity()";
	}

	protected String getIdentityColumnString() throws MappingException {
		return "identity not null";
	}

	public boolean supportsSequences() {
		return true;
	}

	public boolean supportsPooledSequences() {
		return true;
	}

	public String getSequenceNextValString(String sequenceName) throws MappingException {
		return "select " + getSelectSequenceNextValString(sequenceName);
	}

	public String getSelectSequenceNextValString(String sequenceName) throws MappingException {
		return sequenceName + ".nextval";
	}

	/**
	 * @deprecated
	 */
	public String[] getCreateSequenceStrings(String sequenceName) throws MappingException {
		return new String[] { getCreateSequenceString(sequenceName) };
	}

	public String[] getCreateSequenceStrings(String sequenceName, int initialValue, int incrementSize)
			throws MappingException {
		return new String[] { getCreateSequenceString(sequenceName, initialValue, incrementSize) };
	}

	protected String getCreateSequenceString(String sequenceName) throws MappingException {
		return "create sequence " + sequenceName;
	}

	protected String getCreateSequenceString(String sequenceName, int initialValue, int incrementSize)
			throws MappingException {
		if (supportsPooledSequences()) {
			return getCreateSequenceString(sequenceName) + " increment by " + incrementSize + " start with "
					+ initialValue;
		}
		throw new MappingException(getClass().getName() + " does not support pooled sequences");
	}

	public String[] getDropSequenceStrings(String sequenceName) throws MappingException {
		return new String[] { getDropSequenceString(sequenceName) };
	}

	protected String getDropSequenceString(String sequenceName) throws MappingException {
		return "drop sequence " + sequenceName;
	}

	public String getQuerySequencesString() {
		return "select name from sysobjects where type$ = 'SCHOBJ' and subtype$ = 'SEQ';";
	}

	public String getSelectGUIDString() {
		return "select GUID()";
	}

	public boolean supportsLimit() {
		return true;
	}

	public boolean supportsLimitOffset() {
		return supportsLimit();
	}

	public boolean supportsVariableLimit() {
		return supportsLimit();
	}

	public boolean bindLimitParametersInReverseOrder() {
		return true;
	}

	public boolean bindLimitParametersFirst() {
		return false;
	}

	public boolean useMaxForLimit() {
		return true;
	}

	public boolean forceLimitUsage() {
		return false;
	}

	static int getAfterSelectInsertPoint(String sql) {
		int selectIndex = sql.toLowerCase().indexOf("select");
		int selectDistinctIndex = sql.toLowerCase().indexOf("select distinct");
		return selectIndex + (selectDistinctIndex != selectIndex ? 6 : 15);
	}

	public String getLimitString(String query, int offset, int limit) {
		return getLimitString(query, offset > 0);
	}

	protected String getLimitString(String query, boolean hasOffset) {
		query = query.trim();
		boolean isForUpdate = false;
		if (query.toLowerCase().endsWith(" for update")) {
			query = query.substring(0, query.length() - 11);
			isForUpdate = true;
		}

		StringBuilder pagingSelect = new StringBuilder(query.length() + 100);
		if (hasOffset) {
			pagingSelect.append(query).append(" limit ? offset ? ");
		} else {
			pagingSelect.append(query).append(" limit ? ");
		}
		if (isForUpdate) {
			pagingSelect.append(" for update");
		}

		return pagingSelect.toString();
	}

	public int convertToFirstRowValue(int zeroBasedFirstResult) {
		return zeroBasedFirstResult;
	}

	public boolean supportsLockTimeouts() {
		return true;
	}

	public boolean isLockTimeoutParameterized() {
		return false;
	}

	public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
		if (lockMode == LockMode.PESSIMISTIC_FORCE_INCREMENT) {
			return new PessimisticForceIncrementLockingStrategy(lockable, lockMode);
		}
		if (lockMode == LockMode.PESSIMISTIC_WRITE) {
			return new PessimisticWriteSelectLockingStrategy(lockable, lockMode);
		}
		if (lockMode == LockMode.PESSIMISTIC_READ) {
			return new PessimisticReadSelectLockingStrategy(lockable, lockMode);
		}
		if (lockMode == LockMode.OPTIMISTIC) {
			return new OptimisticLockingStrategy(lockable, lockMode);
		}
		if (lockMode == LockMode.OPTIMISTIC_FORCE_INCREMENT) {
			return new OptimisticForceIncrementLockingStrategy(lockable, lockMode);
		}
		return new SelectLockingStrategy(lockable, lockMode);
	}

	public String getForUpdateString(LockOptions lockOptions) {
		LockMode lockMode = lockOptions.getLockMode();
		if (lockMode == LockMode.PESSIMISTIC_READ) {
			return getReadLockString(lockOptions.getTimeOut());
		}
		if (lockMode == LockMode.PESSIMISTIC_WRITE) {
			return getWriteLockString(lockOptions.getTimeOut());
		}
		if (lockMode == LockMode.UPGRADE_NOWAIT) {
			return getForUpdateNowaitString();
		}
		if (lockMode == LockMode.PESSIMISTIC_FORCE_INCREMENT) {
			return getForUpdateNowaitString();
		}

		return "";
	}

	public String getForUpdateString(LockMode lockMode) {
		if (lockMode == LockMode.PESSIMISTIC_READ) {
			return getReadLockString(-1);
		}
		if (lockMode == LockMode.PESSIMISTIC_WRITE) {
			return getWriteLockString(-1);
		}
		if (lockMode == LockMode.UPGRADE_NOWAIT) {
			return getForUpdateNowaitString();
		}
		if (lockMode == LockMode.PESSIMISTIC_FORCE_INCREMENT) {
			return getForUpdateNowaitString();
		}

		return "";
	}

	public String getForUpdateString() {
		return "";
	}

	public String getWriteLockString(int timeout) {
		return getForUpdateString();
	}

	public String getReadLockString(int timeout) {
		return getForUpdateString();
	}

	public boolean forUpdateOfColumns() {
		return false;
	}

	public boolean supportsOuterJoinForUpdate() {
		return false;
	}

	public String getForUpdateString(String aliases) {
		return getForUpdateString();
	}

	public String getForUpdateString(String aliases, LockOptions lockOptions) {
		return getForUpdateString(lockOptions);
	}

	public String getForUpdateNowaitString() {
		return getForUpdateString();
	}

	public String getForUpdateNowaitString(String aliases) {
		return getForUpdateString(aliases);
	}

	public String appendLockHint(LockMode mode, String tableName) {
		return tableName;
	}

	public String getCreateTableString() {
		return "create table ";
	}

	public String getCreateMultisetTableString() {
		return getCreateTableString();
	}

	public boolean supportsTemporaryTables() {
		return true;
	}

	public String generateTemporaryTableName(String baseTableName) {
		return "##" + baseTableName;
	}

	public String getCreateTemporaryTableString() {
		return "create global temporary table";
	}

	public String getCreateTemporaryTablePostfix() {
		return "on commit delete rows";
	}

	public String getDropTemporaryTableString() {
		return "drop table ";
	}

	public Boolean performTemporaryTableDDLInIsolation() {
		return Boolean.valueOf(true);
	}

	public boolean dropTemporaryTableAfterUse() {
		return false;
	}

	public int registerResultSetOutParameter(CallableStatement statement, int position) throws SQLException {
		throw new UnsupportedOperationException(getClass().getName()
				+ " does not support resultsets via stored procedures");
	}

	public ResultSet getResultSet(CallableStatement statement) throws SQLException {
		for (boolean flag = statement.execute(); (!flag) && (statement.getUpdateCount() != -1); flag = statement
				.getMoreResults()) {
		}
		return statement.getResultSet();
	}

	public boolean supportsCurrentTimestampSelection() {
		return true;
	}

	public boolean isCurrentTimestampSelectStringCallable() {
		return false;
	}

	public String getCurrentTimestampSelectString() {
		return "select current_timestamp()";
	}

	public String getCurrentTimestampSQLFunctionName() {
		return "current_timestamp";
	}

	@SuppressWarnings("deprecation")
	public SQLExceptionConverter buildSQLExceptionConverter() {
		return new SQLStateConverter(getViolatedConstraintNameExtracter());
	}

	private static final ViolatedConstraintNameExtracter EXTRACTER = new ViolatedConstraintNameExtracter() {
		public String extractConstraintName(SQLException sqle) {
			return null;
		}
	};

	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
		return EXTRACTER;
	}

	public String getSelectClauseNullString(int sqlType) {
		return "null";
	}

	public boolean supportsUnionAll() {
		return true;
	}

	public String getLowercaseFunction() {
		return "lower";
	}

	public String transformSelectString(String select) {
		return select;
	}

	public String toBooleanValueString(boolean bool) {
		return bool ? "1" : "0";
	}

	public char openQuote() {
		return '"';
	}

	public char closeQuote() {
		return '"';
	}

	public boolean hasAlterTable() {
		return true;
	}

	public boolean dropConstraints() {
		return false;
	}

	public boolean qualifyIndexName() {
		return true;
	}

	public boolean supportsUnique() {
		return true;
	}

	public boolean supportsUniqueConstraintInCreateAlterTable() {
		return true;
	}

	public String getAddColumnString() {
		return " add column ";
	}

	public String getDropForeignKeyString() {
		return " drop constraint ";
	}

	public String getTableTypeString() {
		return "";
	}

	public String getAddForeignKeyConstraintString(String constraintName, String[] foreignKey, String referencedTable,
			String[] primaryKey, boolean referencesPrimaryKey) {
		StringBuffer res = new StringBuffer(30);

		res.append(" add constraint ").append(constraintName).append(" foreign key (")
				.append(StringHelper.join(", ", foreignKey)).append(") references ").append(referencedTable);

		if (!referencesPrimaryKey) {
			res.append(" (").append(StringHelper.join(", ", primaryKey)).append(')');
		}

		return res.toString();
	}

	public String getAddPrimaryKeyConstraintString(String constraintName) {
		return " add constraint " + constraintName + " primary key ";
	}

	public boolean hasSelfReferentialForeignKeyBug() {
		return false;
	}

	public String getNullColumnString() {
		return "";
	}

	public boolean supportsCommentOn() {
		return false;
	}

	public String getTableComment(String comment) {
		return "";
	}

	public String getColumnComment(String comment) {
		return "";
	}

	public boolean supportsIfExistsBeforeTableName() {
		return false;
	}

	public boolean supportsIfExistsAfterTableName() {
		return false;
	}

	public boolean supportsColumnCheck() {
		return true;
	}

	public boolean supportsTableCheck() {
		return true;
	}

	public boolean supportsCascadeDelete() {
		return true;
	}

	public boolean supportsNotNullUnique() {
		return true;
	}

	public String getCascadeConstraintsString() {
		return " cascade ";
	}

	public String getCrossJoinSeparator() {
		return " cross join ";
	}

	public ColumnAliasExtractor getColumnAliasExtractor() {
		return ColumnAliasExtractor.COLUMN_LABEL_EXTRACTOR;
	}

	public boolean supportsEmptyInList() {
		return false;
	}

	public boolean areStringComparisonsCaseInsensitive() {
		return true;
	}

	public boolean supportsRowValueConstructorSyntax() {
		return false;
	}

	public boolean supportsRowValueConstructorSyntaxInInList() {
		return false;
	}

	public boolean useInputStreamToInsertBlob() {
		return true;
	}

	public boolean supportsParametersInInsertSelect() {
		return false;
	}

	public boolean replaceResultVariableInOrderByClauseWithPosition() {
		return false;
	}

	public boolean requiresCastingOfParametersInSelectClause() {
		return false;
	}

	public boolean supportsResultSetPositionQueryMethodsOnForwardOnlyCursor() {
		return true;
	}

	public boolean supportsCircularCascadeDeleteConstraints() {
		return false;
	}

	public boolean supportsSubselectAsInPredicateLHS() {
		return true;
	}

	public boolean supportsExpectedLobUsagePattern() {
		return true;
	}

	public boolean supportsLobValueChangePropogation() {
		return false;
	}

	public boolean supportsUnboundedLobLocatorMaterialization() {
		return false;
	}

	public boolean supportsSubqueryOnMutatingTable() {
		return true;
	}

	public boolean supportsExistsInSelect() {
		return false;
	}

	public boolean doesReadCommittedCauseWritersToBlockReaders() {
		return false;
	}

	public boolean doesRepeatableReadCauseReadersToBlockWriters() {
		return false;
	}

	public boolean supportsBindAsCallableArgument() {
		return true;
	}

	public boolean supportsTupleCounts() {
		return false;
	}

	public boolean supportsTupleDistinctCounts() {
		return false;
	}

	@SuppressWarnings("deprecation")
	public Class<SequenceGenerator> getNativeIdentifierGeneratorClass() {
		return SequenceGenerator.class;
	}
}
