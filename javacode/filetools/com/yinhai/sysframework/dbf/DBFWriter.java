package com.yinhai.sysframework.dbf;

import java.io.BufferedOutputStream;

public class DBFWriter {

	private BufferedOutputStream stream;
	private int recCount;

	public DBFWriter(String s, JDBField[] ajdbfield) throws JDBFException {
		stream = null;
		recCount = 0;
		fields = null;
		fileName = "fileName.dbf";
		dbfEncoding = null;
		fileName = s;
		try {
			init(new java.io.FileOutputStream(s), ajdbfield);
		} catch (java.io.FileNotFoundException filenotfoundexception) {
			throw new JDBFException(filenotfoundexception);
		}
	}

	private JDBField[] fields;
	private String fileName;
	private String dbfEncoding;

	public DBFWriter(java.io.OutputStream outputstream, JDBField[] ajdbfield) throws JDBFException {
		stream = null;
		recCount = 0;
		fields = null;
		fileName = "fileName.dbf";
		dbfEncoding = null;
		init(outputstream, ajdbfield);
	}

	public DBFWriter(String s, JDBField[] ajdbfield, String s1) throws JDBFException {
		stream = null;
		recCount = 0;
		fields = null;
		fileName = "fileName.dbf";
		dbfEncoding = null;
		fileName = s;
		try {
			dbfEncoding = s1;
			init(new java.io.FileOutputStream(s), ajdbfield);
		} catch (java.io.FileNotFoundException filenotfoundexception) {
			throw new JDBFException(filenotfoundexception);
		}
	}

	private void init(java.io.OutputStream outputstream, JDBField[] ajdbfield) throws JDBFException {
		fields = ajdbfield;
		try {
			stream = new BufferedOutputStream(outputstream);
			writeHeader();
			for (int i = 0; i < ajdbfield.length; i++) {
				writeFieldHeader(ajdbfield[i]);
			}
			stream.write(13);
			stream.flush();
		} catch (Exception exception) {
			throw new JDBFException(exception);
		}
	}

	private void writeHeader() throws java.io.IOException {
		byte[] abyte0 = new byte[16];
		abyte0[0] = 3;
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		abyte0[1] = ((byte) (calendar.get(1) - 1900));
		abyte0[2] = ((byte) calendar.get(2));
		abyte0[3] = ((byte) calendar.get(5));
		abyte0[4] = 0;
		abyte0[5] = 0;
		abyte0[6] = 0;
		abyte0[7] = 0;
		int i = (fields.length + 1) * 32 + 1;
		abyte0[8] = ((byte) (i % 256));
		abyte0[9] = ((byte) (i / 256));
		int j = 1;
		for (int k = 0; k < fields.length; k++) {
			j += fields[k].getLength();
		}
		abyte0[10] = ((byte) (j % 256));
		abyte0[11] = ((byte) (j / 256));
		abyte0[12] = 0;
		abyte0[13] = 0;
		abyte0[14] = 0;
		abyte0[15] = 0;
		stream.write(abyte0, 0, abyte0.length);
		for (int l = 0; l < 16; l++) {
			abyte0[l] = 0;
		}
		stream.write(abyte0, 0, abyte0.length);
	}

	private void writeFieldHeader(JDBField jdbfield) throws java.io.IOException {
		byte[] abyte0 = new byte[16];

		String s = jdbfield.getName();
		String news = "";

		int j = 0;

		for (int k = 0; k < s.length(); k++) {
			if (s.substring(k, k + 1).getBytes().length + j > 10) {
				break;
			}

			j += s.substring(k, k + 1).getBytes().length;
			news = news + s.charAt(k);
		}

		byte[] abytem = news.getBytes();

		for (int k = 0; k < abytem.length; k++) {
			abyte0[k] = abytem[k];
		}

		for (int k = j; k <= 10; k++) {
			abyte0[k] = 0;
		}
		abyte0[11] = ((byte) jdbfield.getType());
		abyte0[12] = 0;
		abyte0[13] = 0;
		abyte0[14] = 0;
		abyte0[15] = 0;

		stream.write(abyte0, 0, abyte0.length);

		for (int l = 0; l < 16; l++) {
			abyte0[l] = 0;
		}
		abyte0[0] = ((byte) jdbfield.getLength());
		abyte0[1] = ((byte) jdbfield.getDecimalCount());
		stream.write(abyte0, 0, abyte0.length);
	}

	public void addRecord(Object[] aobj) throws JDBFException {
		if (aobj.length != fields.length)
			throw new JDBFException("Error adding record: Wrong number of values. Expected " + fields.length + ", got "
					+ aobj.length + ".");
		int i = 0;
		for (int j = 0; j < fields.length; j++) {
			i += fields[j].getLength();
		}
		byte[] abyte0 = new byte[i];
		int k = 0;
		String s = "";
		for (int l = 0; l < fields.length; l++) {
			s = fields[l].format(aobj[l]);
			byte[] abyte1;
			try {
				if (dbfEncoding != null) {
					abyte1 = s.getBytes(dbfEncoding);
				} else
					abyte1 = s.getBytes();
			} catch (java.io.UnsupportedEncodingException unsupportedencodingexception) {
				throw new JDBFException(unsupportedencodingexception);
			}
			for (int i1 = 0; i1 < fields[l].getLength(); i1++) {
				abyte0[(k + i1)] = abyte1[i1];
			}
			k += fields[l].getLength();
		}
		try {
			stream.write(32);
			stream.write(abyte0, 0, abyte0.length);
			stream.flush();
		} catch (java.io.IOException ioexception) {
			throw new JDBFException(ioexception);
		}
		recCount += 1;
	}

	public void close() throws JDBFException {
		try {
			stream.write(26);
			stream.close();
			java.io.RandomAccessFile randomaccessfile = new java.io.RandomAccessFile(fileName, "rw");
			randomaccessfile.seek(4L);
			byte[] abyte0 = new byte[4];
			abyte0[0] = ((byte) (recCount % 256));
			abyte0[1] = ((byte) (recCount / 256 % 256));
			abyte0[2] = ((byte) (recCount / 65536 % 256));
			abyte0[3] = ((byte) (recCount / 16777216 % 256));
			randomaccessfile.write(abyte0, 0, abyte0.length);
			randomaccessfile.close();
		} catch (java.io.IOException ioexception) {
			throw new JDBFException(ioexception);
		}
	}

	@SuppressWarnings("resource")
	public byte[] getByteContent() throws NumberFormatException, java.io.IOException {
		java.io.RandomAccessFile randomaccessfile = new java.io.RandomAccessFile(fileName, "rw");
		int btlong = Integer.parseInt(String.valueOf(randomaccessfile.length()));
		byte[] retByte = new byte[btlong];
		for (int y = 0; y < btlong; y++) {
			long lt = Long.parseLong(String.valueOf(y));
			randomaccessfile.seek(lt);
			retByte[y] = randomaccessfile.readByte();
		}
		return retByte;
	}
}
