package com.yinhai.sysframework.dbf;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JDBField implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7244165538216894617L;
	private String name;
	private char type;
	private int length;
	private int decimalCount;

	public JDBField(String s, char c, int i, int j) throws JDBFException {
		if (s.length() > 10)
			throw new JDBFException("The field name is more than 10 characters long: " + s);
		if ((c != 'C') && (c != 'N') && (c != 'L') && (c != 'D') && (c != 'F'))
			throw new JDBFException("The field type is not a valid. Got: " + c);
		if (i < 1)
			throw new JDBFException("The field length should be a positive integer. Got: " + i);
		if ((c == 'C') && (i >= 254))
			throw new JDBFException("The field length should be less than 254 characters for character fields. Got: "
					+ i);
		if ((c == 'N') && (i >= 21))
			throw new JDBFException("The field length should be less than 21 digits for numeric fields. Got: " + i);
		if ((c == 'L') && (i != 1))
			throw new JDBFException("The field length should be 1 characater for logical fields. Got: " + i);
		if ((c == 'D') && (i != 8))
			throw new JDBFException("The field length should be 8 characaters for date fields. Got: " + i);
		if ((c == 'F') && (i >= 21))
			throw new JDBFException("The field length should be less than 21 digits for floating point fields. Got: "
					+ i);
		if (j < 0)
			throw new JDBFException("The field decimal count should not be a negative integer. Got: " + j);
		if (((c == 'C') || (c == 'L') || (c == 'D')) && (j != 0))
			throw new JDBFException(
					"The field decimal count should be 0 for character, logical, and date fields. Got: " + j);
		if (j > i - 1) {
			throw new JDBFException("The field decimal count should be less than the length - 1. Got: " + j);
		}

		name = s;
		type = c;
		length = i;
		decimalCount = j;
	}

	public String getName() {
		return name;
	}

	public char getType() {
		return type;
	}

	public int getLength() {
		return length;
	}

	public int getDecimalCount() {
		return decimalCount;
	}

	public String format(Object obj) throws JDBFException {
		if ((type == 'N') || (type == 'F')) {
			if (obj == null)
				obj = new Double(0.0D);
			if ((obj instanceof Number)) {
				Number number = (Number) obj;
				StringBuffer stringbuffer = new StringBuffer(getLength());
				for (int i = 0; i < getLength(); i++) {
					stringbuffer.append("#");
				}
				if (getDecimalCount() > 0)
					stringbuffer.setCharAt(getLength() - getDecimalCount() - 1, '.');
				DecimalFormat decimalformat = new DecimalFormat(stringbuffer.toString());
				String s1 = decimalformat.format(number);
				int k = getLength() - s1.length();
				if (k < 0)
					throw new JDBFException("Value " + number + " cannot fit in pattern: '" + stringbuffer + "'.");
				StringBuffer stringbuffer2 = new StringBuffer(k);
				for (int l = 0; l < k; l++) {
					stringbuffer2.append(" ");
				}
				return stringbuffer2 + s1;
			}

			throw new JDBFException("Expected a Number, got " + obj.getClass() + ".");
		}

		if (type == 'C') {
			if (obj == null)
				obj = "";
			if ((obj instanceof String)) {
				String s = (String) obj;
				if (s.length() > getLength())
					throw new JDBFException("'" + obj + "' is longer than " + getLength() + " characters.");
				StringBuffer stringbuffer1 = new StringBuffer(getLength() - s.length());
				for (int j = 0; j < getLength() - s.length(); j++) {
					stringbuffer1.append(' ');
				}
				return s + stringbuffer1;
			}

			throw new JDBFException("Expected a String, got " + obj.getClass() + ".");
		}

		if (type == 'L') {
			if (obj == null)
				obj = Boolean.FALSE;
			if ((obj instanceof Boolean)) {
				Boolean boolean1 = (Boolean) obj;
				return boolean1.booleanValue() ? "Y" : "N";
			}

			throw new JDBFException("Expected a Boolean, got " + obj.getClass() + ".");
		}

		if (type == 'D') {
			if (obj == null)
				obj = new Date();
			if ((obj instanceof Date)) {
				Date date = (Date) obj;
				SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyyMMdd");
				return simpledateformat.format(date);
			}

			throw new JDBFException("Expected a Date, got " + obj.getClass() + ".");
		}

		throw new JDBFException("Unrecognized JDBFField type: " + type);
	}

	public Object parse(String s) throws JDBFException {
		s = s.trim();
		if ((type == 'N') || (type == 'F')) {
			if (s.equals("")) {
				s = "0";
			}
			try {
				if (s.indexOf(".") < 0) {
					return new Long(s);
				}
				return new Double(s);
			} catch (NumberFormatException numberformatexception) {
				throw new JDBFException(numberformatexception);
			}
		}
		if (type == 'C')
			return s;
		if (type == 'L') {
			if ((s.equals("Y")) || (s.equals("y")) || (s.equals("T")) || (s.equals("t")))
				return Boolean.TRUE;
			if ((s.equals("N")) || (s.equals("n")) || (s.equals("F")) || (s.equals("f"))) {
				return Boolean.FALSE;
			}
			throw new JDBFException("Unrecognized value for logical field: " + s);
		}
		if (type == 'D') {
			SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyyMMdd");
			try {
				if ("".equals(s)) {
					return null;
				}
				return simpledateformat.parse(s);
			} catch (ParseException parseexception) {
				throw new JDBFException(parseexception);
			}
		}

		throw new JDBFException("Unrecognized JDBFField type: " + type);
	}

	public String toString() {
		return name;
	}
}
