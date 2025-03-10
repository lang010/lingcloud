/*
 * $Id: GenericTypeValidator.java,v 1.1 2008/04/22 14:27:16 zouyongqiang Exp $
 * $Rev: 293556 $
 * $Date: 2008/04/22 14:27:16 $
 *
 * ====================================================================
 * Copyright 2001-2005 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lingcloud.molva.ocl.util;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class contains basic methods for performing validations that return the
 * correctly typed class based on the validation performed.
 */
public class GenericTypeValidator implements Serializable {
	private static final long serialVersionUID = 7801638455506781709L;
	private static Log log = LogFactory.getLog(GenericTypeValidator.class);

	/**
	 * Checks if the value can safely be converted to a byte primitive.
	 * 
	 * @param value
	 *            The value validation is being performed on.
	 * @return the converted Byte value.
	 */
	public static Byte formatByte(String value) {
		if (value == null) {
			return null;
		}

		try {
			return new Byte(value);
		} catch (NumberFormatException e) {
			return null;
		}

	}

	/**
	 * Checks if the value can safely be converted to a byte primitive.
	 * 
	 * @param value
	 *            The value validation is being performed on.
	 * @param locale
	 *            The locale to use to parse the number (system default if null)
	 * @return the converted Byte value.
	 */
	public static Byte formatByte(String value, Locale locale) {
		Byte result = null;

		if (value != null) {
			NumberFormat formatter = null;
			if (locale != null) {
				formatter = NumberFormat.getNumberInstance(locale);
			} else {
				formatter = NumberFormat.getNumberInstance(Locale.getDefault());
			}
			formatter.setParseIntegerOnly(true);
			ParsePosition pos = new ParsePosition(0);
			Number num = formatter.parse(value, pos);

			// If there was no error and we used the whole string
			if (pos.getErrorIndex() == -1 && pos.getIndex() == value.length()) {
				if (num.doubleValue() >= Byte.MIN_VALUE
						&& num.doubleValue() <= Byte.MAX_VALUE) {
					result = Byte.valueOf(num.byteValue());
				}
			}
		}

		return result;
	}

	/**
	 * Checks if the value can safely be converted to a short primitive.
	 * 
	 * @param value
	 *            The value validation is being performed on.
	 * @return the converted Short value.
	 */
	public static Short formatShort(String value) {
		if (value == null) {
			return null;
		}

		try {
			return new Short(value);
		} catch (NumberFormatException e) {
			return null;
		}

	}

	/**
	 * Checks if the value can safely be converted to a short primitive.
	 * 
	 * @param value
	 *            The value validation is being performed on.
	 * @param locale
	 *            The locale to use to parse the number (system default if
	 *            null)vv
	 * @return the converted Short value.
	 */
	public static Short formatShort(String value, Locale locale) {
		Short result = null;

		if (value != null) {
			NumberFormat formatter = null;
			if (locale != null) {
				formatter = NumberFormat.getNumberInstance(locale);
			} else {
				formatter = NumberFormat.getNumberInstance(Locale.getDefault());
			}
			formatter.setParseIntegerOnly(true);
			ParsePosition pos = new ParsePosition(0);
			Number num = formatter.parse(value, pos);

			// If there was no error and we used the whole string
			if (pos.getErrorIndex() == -1 && pos.getIndex() == value.length()) {
				if (num.doubleValue() >= Short.MIN_VALUE
						&& num.doubleValue() <= Short.MAX_VALUE) {
					result = Short.valueOf(num.shortValue());
				}
			}
		}

		return result;
	}

	/**
	 * Checks if the value can safely be converted to a int primitive.
	 * 
	 * @param value
	 *            The value validation is being performed on.
	 * @return the converted Integer value.
	 */
	public static Integer formatInt(String value) {
		if (value == null) {
			return null;
		}

		try {
			return new Integer(value);
		} catch (NumberFormatException e) {
			return null;
		}

	}

	/**
	 * Checks if the value can safely be converted to an int primitive.
	 * 
	 * @param value
	 *            The value validation is being performed on.
	 * @param locale
	 *            The locale to use to parse the number (system default if null)
	 * @return the converted Integer value.
	 */
	public static Integer formatInt(String value, Locale locale) {
		Integer result = null;

		if (value != null) {
			NumberFormat formatter = null;
			if (locale != null) {
				formatter = NumberFormat.getNumberInstance(locale);
			} else {
				formatter = NumberFormat.getNumberInstance(Locale.getDefault());
			}
			formatter.setParseIntegerOnly(true);
			ParsePosition pos = new ParsePosition(0);
			Number num = formatter.parse(value, pos);

			// If there was no error and we used the whole string
			if (pos.getErrorIndex() == -1 && pos.getIndex() == value.length()) {
				if (num.doubleValue() >= Integer.MIN_VALUE
						&& num.doubleValue() <= Integer.MAX_VALUE) {
					result = Integer.valueOf(num.intValue());
				}
			}
		}

		return result;
	}

	/**
	 * Checks if the value can safely be converted to a long primitive.
	 * 
	 * @param value
	 *            The value validation is being performed on.
	 * @return the converted Long value.
	 */
	public static Long formatLong(String value) {
		if (value == null) {
			return null;
		}

		try {
			return new Long(value);
		} catch (NumberFormatException e) {
			return null;
		}

	}

	/**
	 * Checks if the value can safely be converted to a long primitive.
	 * 
	 * @param value
	 *            The value validation is being performed on.
	 * @param locale
	 *            The locale to use to parse the number (system default if null)
	 * @return the converted Long value.
	 */
	public static Long formatLong(String value, Locale locale) {
		Long result = null;

		if (value != null) {
			NumberFormat formatter = null;
			if (locale != null) {
				formatter = NumberFormat.getNumberInstance(locale);
			} else {
				formatter = NumberFormat.getNumberInstance(Locale.getDefault());
			}
			formatter.setParseIntegerOnly(true);
			ParsePosition pos = new ParsePosition(0);
			Number num = formatter.parse(value, pos);

			// If there was no error and we used the whole string
			if (pos.getErrorIndex() == -1 && pos.getIndex() == value.length()) {
				if (num.doubleValue() >= Long.MIN_VALUE
						&& num.doubleValue() <= Long.MAX_VALUE) {
					result = Long.valueOf(num.longValue());
				}
			}
		}

		return result;
	}

	/**
	 * Checks if the value can safely be converted to a float primitive.
	 * 
	 * @param value
	 *            The value validation is being performed on.
	 * @return the converted Float value.
	 */
	public static Float formatFloat(String value) {
		if (value == null) {
			return null;
		}

		try {
			return new Float(value);
		} catch (NumberFormatException e) {
			return null;
		}

	}

	/**
	 * Checks if the value can safely be converted to a float primitive.
	 * 
	 * @param value
	 *            The value validation is being performed on.
	 * @param locale
	 *            The locale to use to parse the number (system default if null)
	 * @return the converted Float value.
	 */
	public static Float formatFloat(String value, Locale locale) {
		Float result = null;

		if (value != null) {
			NumberFormat formatter = null;
			if (locale != null) {
				formatter = NumberFormat.getInstance(locale);
			} else {
				formatter = NumberFormat.getInstance(Locale.getDefault());
			}
			ParsePosition pos = new ParsePosition(0);
			Number num = formatter.parse(value, pos);

			// If there was no error and we used the whole string
			if (pos.getErrorIndex() == -1 && pos.getIndex() == value.length()) {
				if (num.doubleValue() >= (Float.MAX_VALUE * -1)
						&& num.doubleValue() <= Float.MAX_VALUE) {
					result = new Float(num.floatValue());
				}
			}
		}

		return result;
	}

	/**
	 * Checks if the value can safely be converted to a double primitive.
	 * 
	 * @param value
	 *            The value validation is being performed on.
	 * @return the converted Double value.
	 */
	public static Double formatDouble(String value) {
		if (value == null) {
			return null;
		}

		try {
			return new Double(value);
		} catch (NumberFormatException e) {
			return null;
		}

	}

	/**
	 * Checks if the value can safely be converted to a double primitive.
	 * 
	 * @param value
	 *            The value validation is being performed on.
	 * @param locale
	 *            The locale to use to parse the number (system default if null)
	 * @return the converted Double value.
	 */
	public static Double formatDouble(String value, Locale locale) {
		Double result = null;

		if (value != null) {
			NumberFormat formatter = null;
			if (locale != null) {
				formatter = NumberFormat.getInstance(locale);
			} else {
				formatter = NumberFormat.getInstance(Locale.getDefault());
			}
			ParsePosition pos = new ParsePosition(0);
			Number num = formatter.parse(value, pos);

			// If there was no error and we used the whole string
			if (pos.getErrorIndex() == -1 && pos.getIndex() == value.length()) {
				if (num.doubleValue() >= (Double.MAX_VALUE * -1)
						&& num.doubleValue() <= Double.MAX_VALUE) {
					result = new Double(num.doubleValue());
				}
			}
		}

		return result;
	}

	/**
	 * <p>
	 * 
	 * Checks if the field is a valid date. The <code>Locale</code> is used with
	 * <code>java.text.DateFormat</code>. The setLenient method is set to
	 * <code>false</code> for all.
	 * </p>
	 * 
	 * @param value
	 *            The value validation is being performed on.
	 * @param locale
	 *            The Locale to use to parse the date (system default if null)
	 * @return the converted Date value.
	 */
	public static Date formatDate(String value, Locale locale) {
		Date date = null;

		if (value == null) {
			return null;
		}

		try {
			DateFormat formatter = null;
			if (locale != null) {
				formatter = DateFormat
						.getDateInstance(DateFormat.SHORT, locale);
			} else {
				formatter = DateFormat.getDateInstance(DateFormat.SHORT,
						Locale.getDefault());
			}

			formatter.setLenient(false);

			date = formatter.parse(value);
		} catch (ParseException e) {
			// Bad date so return null
			if (log.isDebugEnabled()) {
				log.debug("Date parse failed value=[" + value + "], "
						+ "locale=[" + locale + "] " + e);
			}
		}

		return date;
	}

	/**
	 * <p>
	 * Checks if the field is a valid date. The pattern is used with
	 * <code>java.text.SimpleDateFormat</code> . If strict is true, then the
	 * length will be checked so '2/12/1999' will not pass validation with the
	 * format 'MM/dd/yyyy' because the month isn't two digits. The setLenient
	 * method is set to <code>false</code> for all.
	 * </p>
	 * 
	 * @param value
	 *            The value validation is being performed on.
	 * @param datePattern
	 *            The pattern passed to <code>SimpleDateFormat</code>.
	 * @param strict
	 *            Whether or not to have an exact match of the datePattern.
	 * @return the converted Date value.
	 */
	public static Date formatDate(String value, String datePattern,
			boolean strict) {
		Date date = null;

		if (value == null || datePattern == null || datePattern.length() == 0) {
			return null;
		}

		try {
			SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
			formatter.setLenient(false);

			date = formatter.parse(value);

			if (strict) {
				if (datePattern.length() != value.length()) {
					date = null;
				}
			}
		} catch (ParseException e) {
			// Bad date so return null
			if (log.isDebugEnabled()) {
				log.debug("Date parse failed value=[" + value + "], "
						+ "pattern=[" + datePattern + "], " + "strict=["
						+ strict + "] " + e);
			}
		}

		return date;
	}

	private GenericTypeValidator() {

	}
}
