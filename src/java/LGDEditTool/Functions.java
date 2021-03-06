/*
 *    This file is part of LGDEditTool (LGDET).
 *
 *    LGDET is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    any later version.
 *
 *    LGDET is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with LGDET.  If not, see <http://www.gnu.org/licenses/>.
 */


package LGDEditTool;

import java.util.Calendar;

/**
 *
 * @author J. Nathanael Philipp
 * @version 1.0
 */
public class Functions {
	/**
	 * PRIVATE_reCAPTCHA_KEY
	 */
	public static final String PRIVATE_reCAPTCHA_KEY = "6Le1b88SAAAAALUjcJ26asXAk2wHDu-JwarKY8z1";
	/**
	 * PUBLIC_reCAPTCHA_KEY
	 */
	public static final String PUBLIC_reCAPTCHA_KEY = "6Le1b88SAAAAALjXm-PM6alI7EQlj-fi9eh-Wm2C ";

	/**
	 * Shorting the URL from property and object.
         * for ex.: 'http://www.w3.org/1999/02/22-rdf-syntax-ns#type' to 'w3#type'
         * or: 'http://linkedgeodata.org/ontology/Craft' to 'LGD:Craft'
	 * @param url URI to be shortened
	 * @return short version of the input URI
	 */
	public static String shortenURL(String url) {
		if ( url.contains("w3.org") && url.endsWith("#type") )
			return "w3#type";
		else if ( url.contains("linkedgeodata.org") ) {
			return "LGD:" + url.substring(url.lastIndexOf("/") + 1);
		}
		else
			return url;
	}

	/**
	 * Formating timestamp from database (format: YYYY-MM-ddTHH:mm:ss) into dd.MM.YYYY<br />HH:mm:ss
	 * @param timestamp
	 * @return String in dd.MM.YYYY<br />HH:mm:ss format.
	 */
	public static String showTimestamp(String timestamp) {
		String re = "";
		String[] a = timestamp.split("T")[0].split("-");
		re += a[2] + "." + a[1] + "." + a[0];
		re += "<br />" + timestamp.split("T")[1];
		return re;
	}

        /**
         * Template for Database. Transform Date-String to Database Date-Type-String
         * @return String in YYYY-MM-ddTHH:mm:ss format.
         */
	public static String getTimestamp() {
		return Calendar.getInstance().get(Calendar.YEAR) + "-" + ((Calendar.getInstance().get(Calendar.MONTH) + 1) < 10 ? "0" + (Calendar.getInstance().get(Calendar.MONTH) + 1) : (Calendar.getInstance().get(Calendar.MONTH) + 1)) + "-" + (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < 10 ? "0" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) : Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) + "T" + (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 10 ? "0" + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) : Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) + ":" + (Calendar.getInstance().get(Calendar.MINUTE) < 10 ? "0" + Calendar.getInstance().get(Calendar.MINUTE) : Calendar.getInstance().get(Calendar.MINUTE)) + ":" + (Calendar.getInstance().get(Calendar.SECOND) < 10 ? "0" + Calendar.getInstance().get(Calendar.SECOND) : Calendar.getInstance().get(Calendar.SECOND));
	}
}