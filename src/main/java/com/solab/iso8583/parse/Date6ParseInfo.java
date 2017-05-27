package com.solab.iso8583.parse;

import com.solab.iso8583.CustomField;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.IsoValue;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class Date6ParseInfo extends DateTimeParseInfo {

    public Date6ParseInfo() {
   		super(IsoType.DATE6, 6);
   	}

   	@Override
   	public <T> IsoValue<Date> parse(final int field, final byte[] buf,
                                       final int pos, final CustomField<T> custom)
   			throws ParseException, UnsupportedEncodingException {
   		if (pos < 0) {
   			throw new ParseException(String.format("Invalid DATE6 field %d position %d",
                       field, pos), pos);
   		}
   		if (pos+6 > buf.length) {
   			throw new ParseException(String.format("Insufficient data for DATE6 field %d, pos %d",
                       field, pos), pos);
   		}
   		//A SimpleDateFormat in the case of dates won't help because of the missing data
   		//we have to use the current date for reference and change what comes in the buffer
        Calendar cal = Calendar.getInstance();
   		//Set the month in the date
        int year;
        if (forceStringDecoding) {
            year = Integer.parseInt(new String(buf, pos, 2, getCharacterEncoding()), 10);
            cal.set(Calendar.MONTH, Integer.parseInt(new String(buf, pos, 2, getCharacterEncoding()), 10)-1);
            cal.set(Calendar.DATE, Integer.parseInt(new String(buf, pos+2, 2, getCharacterEncoding()), 10));
        } else {
            year = ((buf[pos] - 48) * 10) + buf[pos + 1] - 48;
            cal.set(Calendar.MONTH, ((buf[pos+2] - 48) * 10) + buf[pos + 3] - 49);
            cal.set(Calendar.DATE, ((buf[pos + 4] - 48) * 10) + buf[pos + 5] - 48);
        }
        if (year > 50) {
            cal.set(Calendar.YEAR, 1900+year);
        } else {
            cal.set(Calendar.YEAR, 2000+year);
        }
        cal.set(Calendar.MILLISECOND,0);
        if (tz != null) {
            cal.setTimeZone(tz);
        }
   		adjustWithFutureTolerance(cal);
   		return new IsoValue<>(type, cal.getTime(), null);
   	}

   	@Override
   	public <T> IsoValue<Date> parseBinary(final int field, final byte[] buf,
                                          final int pos, final CustomField<T> custom)
               throws ParseException {
        if (pos < 0) {
            throw new ParseException(String.format("Invalid DATE6 field %d position %d",
                field, pos), pos);
        }
        if (pos+3 > buf.length) {
            throw new ParseException(String.format("Insufficient data for DATE6 field %d, pos %d",
                field, pos), pos);
        }
   		int[] tens = new int[3];
   		int start = 0;
   		for (int i = pos; i < pos + tens.length; i++) {
   			tens[start++] = (((buf[i] & 0xf0) >> 4) * 10) + (buf[i] & 0x0f);
   		}
   		Calendar cal = Calendar.getInstance();
   		//A SimpleDateFormat in the case of dates won't help because of the missing data
   		//we have to use the current date for reference and change what comes in the buffer
   		//Set the month in the date
        if (tens[0] > 50) {
            cal.set(Calendar.YEAR, 1900+tens[0]);
        } else {
            cal.set(Calendar.YEAR, 2000+tens[0]);
        }
   		cal.set(Calendar.MONTH, tens[1] - 1);
   		cal.set(Calendar.DATE, tens[2]);
           if (tz != null) {
               cal.setTimeZone(tz);
           }
   		adjustWithFutureTolerance(cal);
   		return new IsoValue<>(type, cal.getTime(), null);
   	}

}