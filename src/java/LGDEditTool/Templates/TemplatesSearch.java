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

package LGDEditTool.Templates;

import LGDEditTool.Functions;
import LGDEditTool.SiteHandling.User;
import LGDEditTool.db.DatabaseBremen;
import javax.servlet.http.HttpServletRequest;
import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaFactory;

/**
 *
 * @author J. Nathanael Philipp
 * @version 1.0
 */
public class TemplatesSearch {
	/**
	 * Template for Searchfield. This Template is used by the 'search'-tab.
	 * @return Returns a String with HTML-code.
	 */
	public static String search() {
		String re = "\t\t\t\t<fieldset class=\"search\">\n";
		re += "\t\t\t\t\t<legend>Search</legend>\n";
		re += "\t\t\t\t\t<form method=\"get\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		re += "\t\t\t\t\t\t<ul>\n";
		re += "\t\t\t\t\t\t\t<li>\n";
		re += "\t\t\t\t\t\t\t\t<label>Search:</label>\n";
		re += "\t\t\t\t\t\t\t\t<input type=\"text\" id=\"search\" name=\"search\" required />\n";
		re += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"tab\" value=\"search\">\n";
		re += "\t\t\t\t\t\t\t\t<input type=\"submit\" value=\"Search\" />\n";
		re += "\t\t\t\t\t\t\t</li>\n";
		re += "\t\t\t\t\t\t</ul>\n";
		re += "\t\t\t\t\t</form>\n";
		re += "\t\t\t\t</fieldset>\n";
		return re;
	}

	/**
	 * Template for search results. Builds String with HTML-Code that represent the search Result.
	 * @param search Search-string, which the user typed in.
	 * @return String with HTML-Code
	 * @throws Exception 
	 */
	public static String searchResult(String search) throws Exception {
		DatabaseBremen.getInstance().connect();
		String re = "", tmp = "";
                boolean result = false;

		tmp = kMapping((search.contains("#") ? search.split("#")[0] : search ));
		if ( !tmp.equals("") ) {
			re += tmp;
                        result = true;
                }

		tmp = "\n\t\t\t\t<br /><br />\n\n" + kvMapping(search);
		if ( !tmp.equals("\n\t\t\t\t<br /><br />\n\n") ) {
			re += tmp;
                        result = true;
                }

		tmp = "\n\t\t\t\t<br /><br />\n\n" + datatypeMapping((tmp.equals("\n\t\t\t\t<br /><br />\n\n") ? search : (search.contains("#") ? search.split("#")[0] : search )));
		if ( !tmp.equals("\n\t\t\t\t<br /><br />\n\n") ) {
			re += tmp;
                        result = true;
                }

                if ( !result )
                    re += "\t\t\t\t<p>Your search returned no results.</p>";

		return re;
	}

	/**
	 * Template for K-Mapping results. SQL-Query for K-Mappings and fills K-Mapping table.
	 * @param search Search-string, which the user typed in.
	 * @return String with HTML-Code
	 * @throws Exception 
	 */
	private static String kMapping(String search) throws Exception {
		DatabaseBremen database = DatabaseBremen.getInstance();
		String re = "";
		Object[][] a = database.execute("SELECT k, property, object, count(k) FROM lgd_map_resource_k WHERE " + (search.contains("*") ? "k LIKE '" + search.replaceAll("\\*", "%") + "%'" : "k='" + search + "'") + " GROUP BY k, property, object ORDER BY k");

		if ( a.length == 0 )
			return "";

		//K-Mappings
		re = "\t\t\t\t<h2>K-Mappings</h2>\n";
		re += "\t\t\t\t<table class=\"table\">\n";
		re += "\t\t\t\t\t<tr>\n";
		re += "\t\t\t\t\t\t<th>k</th>\n";
		re += "\t\t\t\t\t\t<th>property</th>\n";
		re += "\t\t\t\t\t\t<th>object</th>\n";
		re += "\t\t\t\t\t\t<th>affected Entities</th>\n";
		re += "\t\t\t\t\t\t<th>Edit</th>\n";
		re += "\t\t\t\t\t\t<th>Delete</th>\n";
		re += "\t\t\t\t\t</tr>\n";

		for ( int i = 0; i < a.length; i++ ) {
			re += "\t\t\t\t\t<tr id=\"k" + i + "a\">\n";
			re += "\t\t\t\t\t\t<td>" + a[i][0] + "</td>\n";
			re += "\t\t\t\t\t\t<td>" + Functions.shortenURL(a[i][1].toString()) + "</td>\n";
			re += "\t\t\t\t\t\t<td>" + Functions.shortenURL(a[i][2].toString()) + "</td>\n";
			re += "\t\t\t\t\t\t<td>" + a[i][3] + "</td>\n";
			re += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('k" + i + "')\">Edit</a></td>\n";
			re += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kd" + i + "')\">Delete</a></td>\n";
			re += "\t\t\t\t\t</tr>\n";

			//edit
			re += kMappingEdit(search, i, a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), a[i][3].toString());
			//delete
			re += kMappingDelete(search, i, a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), a[i][3].toString());
		}

		re += "\t\t\t\t</table>\n";
		return re;
	}

	/**
	 * Template for K-Mapping edit fields. Elements for editing K-Mappings.
	 * @param search Search-string, which the user typed in.
	 * @param i table-column id
	 * @param k K-Mapping key
	 * @param property K-Mapping property
	 * @param object K-Mapping object
	 * @param affectedEntities K-Mapping affected entities
	 * @return String with HTML-Code
	 */
	private static String kMappingEdit(String search, int i, String k, String property, String object, String affectedEntities) {
		String re = "";

		re += "\t\t\t\t\t<form action=\"?tab=search&search=" + search + (!User.getInstance().isLoggedIn() ? "&captcha=yes" : "") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		re += "\t\t\t\t\t\t<tr id=\"k" + i + "\" class=\"mapping\" style=\"display: none;\">\n";
		re += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		re += "\t\t\t\t\t\t\t<td><input type=\"text\" name=\"property\" value=\"" + property + "\" style=\"width: 27em;\" required /></td>\n";
		re += "\t\t\t\t\t\t\t<td><input type=\"text\" name=\"object\" value=\"" + object + "\" style=\"width: 27em;\" required /></td>\n";
		re += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		re += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('k" + i + "')\">Hide</a></td>\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"aproperty\" value=\"" + property + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"aobject\" value=\"" + object + "\" />\n";
		re += "\t\t\t\t\t\t\t<td>Delete</td>\n";
		re += "\t\t\t\t\t\t</tr>\n";
		re += getUserField("k" + i + "u", "kmapping", "Save", 6);
		re += "\t\t\t\t\t</form>\n";
		return re;
	}

	/**
	 * Template for K-Mapping delete fields. Elements for deleting K-Mappings
	 * @param search Search-string, which the user typed in.
	 * @param i table-column id
	 * @param k K-Mapping key
	 * @param property K-Mapping property
	 * @param object K-Mapping object
	 * @param affectedEntities K-Mapping affected entities
	 * @return String with HTML-Code
	 */
	private static String kMappingDelete(String search, int i, String k, String property, String object, String affectedEntities) {
		String re = "";

		re += "\t\t\t\t\t<form action=\"?tab=search&search=" + search + (!User.getInstance().isLoggedIn() ? "&captcha=yes" : "") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		re += "\t\t\t\t\t\t<tr id=\"kd" + i + "\" class=\"mapping\" style=\"display: none;\">\n";
		re += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + property + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + object + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>Edit</td>\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + property + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"object\" value=\"" + object + "\" />\n";
		re += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kd" + i + "')\">Hide</a></td>\n";
		re += "\t\t\t\t\t\t</tr>\n";
		re += getUserField("kd" + i + "u", "kmapping", "Delete", 6);
		re += "\t\t\t\t\t</form>\n";

		return re;
	}

	/**
	 * Template for KV-Mapping results. SQL-Query for KV-Mappings and fills KV-Mapping table.
	 * @param search Search-string, which the user typed in.
	 * @return String with HTML-Code
	 * @throws Exception 
	 */
	private static String kvMapping(String search) throws Exception {
		DatabaseBremen database = DatabaseBremen.getInstance();
		String re = "";
		Object[][] a;
		if ( search.contains("#") )
			a = database.execute("SELECT k, v, property, object, count(k) FROM lgd_map_resource_kv WHERE k='" + search.split("#")[0] + "' AND v='" + search.split("#")[1] + "' GROUP BY k, v, property, object ORDER BY k, v");
		else
			a = database.execute("SELECT k, v, property, object, count(k) FROM lgd_map_resource_kv WHERE " + (search.contains("*") ? "k LIKE '" + search.replaceAll("\\*", "%") + "%'" : "k='" + search + "'") + " OR " + (search.contains("*") ? "v LIKE '" + search.replaceAll("\\*", "%") + "%'" : "v='" + search + "'") + " GROUP BY k, v, property, object ORDER BY k, v");

		if ( a.length == 0 )
			return "";

		//KV-Mappings
		re += "\t\t\t\t<h2>KV-Mappings</h2>\n";
		re += "\t\t\t\t<table class=\"table\">\n";
		re += "\t\t\t\t\t<tr>\n";
		re += "\t\t\t\t\t\t<th>k</th>\n";
		re += "\t\t\t\t\t\t<th>v</th>\n";
		re += "\t\t\t\t\t\t<th>property</th>\n";
		re += "\t\t\t\t\t\t<th>object</th>\n";
		re += "\t\t\t\t\t\t<th>affected Entities</th>\n";
		re += "\t\t\t\t\t\t<th>Edit</th>\n";
		re += "\t\t\t\t\t\t<th>Delete</th>\n";
		re += "\t\t\t\t\t</tr>\n";

		for ( int i = 0; i < a.length; i++ ) {
			re += "\t\t\t\t\t<tr id=\"kv" + i + "a\">\n";
			re += "\t\t\t\t\t\t<td>" + a[i][0] + "</td>\n";
			re += "\t\t\t\t\t\t<td>" + a[i][1] + "</td>\n";
			re += "\t\t\t\t\t\t<td>" + Functions.shortenURL(a[i][2].toString()) + "</td>\n";
			re += "\t\t\t\t\t\t<td>" + Functions.shortenURL(a[i][3].toString()) + "</td>\n";
			re += "\t\t\t\t\t\t<td>" + a[i][4] + "</td>\n";
			re += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kv" + i + "')\">Edit</a></td>\n";
			re += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kvd" + i + "')\">Delete</a></td>\n";
			re += "\t\t\t\t\t</tr>\n";

			//edit
			re += kvMappingEdit(search,i, a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), a[i][3].toString(), a[i][4].toString());
			//delete
			re += kvMappingDelete(search, i, a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), a[i][3].toString(), a[i][4].toString());
		}

		re += "\t\t\t\t</table>";

		return re;
	}

	/**
	 * Template for KV-Mapping edit fields. Elements for editing KV-Mappings.
	 * @param search Search-string, which the user typed in.
	 * @param i table-column id
	 * @param k KV-Mapping key
	 * @param v KV-Mapping value
	 * @param property KV-Mapping property
	 * @param object KV-Mapping object
	 * @param affectedEntities KV-Mapping affected entities
	 * @return String with HTML-Code
	 */
	private static String kvMappingEdit(String search, int i, String k, String v, String property, String object, String affectedEntities) {
		String re = "";

		re += "\t\t\t\t\t<form action=\"?tab=search&search=" + search + (!User.getInstance().isLoggedIn() ? "&captcha=yes" : "") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		re += "\t\t\t\t\t\t<tr id=\"kv" + i + "\" class=\"mapping\" style=\"display: none;\">\n";
		re += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + v + "</td>\n";
		re += "\t\t\t\t\t\t\t<td><input type=\"text\" name=\"property\" value=\"" + property + "\" style=\"width: 23em;\" /></td>\n";
		re += "\t\t\t\t\t\t\t<td><input type=\"text\" name=\"object\" value=\"" + object + "\" style=\"width: 23em;\" /></td>\n";
		re += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		re += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kv" + i + "')\">Hide</a></td>\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"v\" value=\"" + v + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"aproperty\" value=\"" + property + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"aobject\" value=\"" + object + "\" />\n";
		re += "\t\t\t\t\t\t\t<td>Delete</td>\n";
		re += "\t\t\t\t\t\t</tr>\n";
		re += getUserField("kv" + i + "u", "kvmapping", "Save", 7);
		re += "\t\t\t\t\t</form>\n";

		return re;
	}

	/**
	 * Template for KV-Mapping delete fields. Elements for deleting KV-Mappings
	 * @param search Search-string, which the user typed in.
	 * @param i table-column id
	 * @param k KV-Mapping key
	 * @param v KV-Mapping value
	 * @param property KV-Mapping property
	 * @param object KV-Mapping object
	 * @param affectedEntities KV-Mapping affected entities
	 * @return String with HTML-Code
	 */
	private static String kvMappingDelete(String search,int i, String k, String v, String property, String object, String affectedEntities) {
		String re = "";

		re += "\t\t\t\t\t<form action=\"?tab=search&search=" + search + (!User.getInstance().isLoggedIn() ? "&captcha=yes" : "") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		re += "\t\t\t\t\t\t<tr id=\"kvd" + i + "\" class=\"mapping\" style=\"display: none;\">\n";
		re += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + v + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + property + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + object + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>Edit</td>\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"v\" value=\"" + v + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + property + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"object\" value=\"" + object + "\" />\n";
		re += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kvd" + i + "')\">Hide</a></td>\n";
		re += "\t\t\t\t\t\t</tr>\n";
		re += getUserField("kvd" + i +"u", "kvmapping", "Delete", 7);
		re += "\t\t\t\t\t</form>\n";

		return re;
	}

	/**
	 * Template for Datatype-Mapping results. SQL-Query for Datatype-Mappings and fills Datatype-Mapping table.
	 * @param search Search-string, which the user typed in.
	 * @return String with HTML-Code
	 * @throws Exception 
	 */
	private static String datatypeMapping(String search) throws Exception {
		DatabaseBremen database = DatabaseBremen.getInstance();
		String re = "";
		Object[][] a;

		if ( search.contains("#") )
			a = database.execute("SELECT k, datatype, count(k) FROM lgd_map_datatype WHERE k='" + search.split("#")[0] + "' " + (search.split("#")[1].equals("int") || search.split("#")[1].equals("float") || search.split("#")[1].equals("boolean") ? "AND datatype='" + search.split("#")[1] + "'" : "" ) + " GROUP BY k, datatype ORDER BY k, datatype");
		else
			a = database.execute("SELECT k, datatype, count(k) FROM lgd_map_datatype WHERE " + (search.contains("*") ? "k LIKE '" + search.replaceAll("\\*", "%") + "%'" : "k='" + search + "'") + " GROUP BY k, datatype ORDER BY k, datatype");

		if ( a.length == 0 )
			return "";

		//Datatype-Mappings
		re = "\t\t\t\t<h2>Datatype-Mappings</h2>\n";
		re += "\t\t\t\t<table class=\"table\">\n";
		re += "\t\t\t\t\t<tr>\n";
		re += "\t\t\t\t\t\t<th>k</th>\n";
		re += "\t\t\t\t\t\t<th>datatype</th>\n";
		re += "\t\t\t\t\t\t<th>affected Entities</th>\n";
		re += "\t\t\t\t\t\t<th>Edit</th>\n";
		re += "\t\t\t\t\t\t<th>Delete</th>\n";
		re += "\t\t\t\t\t</tr>\n";
		
		for ( int i = 0; i < a.length; i++ ) {
			re += "\t\t\t\t\t<tr id=\"tk" + i + "a\">\n";
			re += "\t\t\t\t\t\t<td>" + a[i][0] + "</td>\n";
			re += "\t\t\t\t\t\t<td>" + a[i][1] + "</td>\n";
			re += "\t\t\t\t\t\t<td>" + a[i][2] + "</td>\n";
			re += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('tk" + i + "')\">Edit</a></td>\n";
			re += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('tkd" + i + "')\">Delete</a></td>\n";
			re += "\t\t\t\t\t</tr>\n";

			//edit
			re += datatypeMappingEdit(search, i, a[i][0].toString(), a[i][1].toString(), a[i][2].toString());
			//delete
			re += datatypeMappingDelete(search, i, a[i][0].toString(), a[i][1].toString(), a[i][2].toString());
		}

		re += "\t\t\t\t</table>";

		return re;
	}

	/**
	 * Template for Datatype-Mapping edit fields. Elements for editing Datatype-Mappings.
	 * @param search Search-string, which the user typed in.
	 * @param i table-column id
	 * @param k Datatype-Mapping key
	 * @param datatype Datatype-Mapping datatype
	 * @param affectedEntities Datatype-Mapping affected entities
	 * @return String with HTML-Code
	 */
	private static String datatypeMappingEdit(String search, int i, String k, String datatype, String affectedEntities) {
		String re = "";

		re += "\t\t\t\t\t<form action=\"?tab=search&search=" + search + (!User.getInstance().isLoggedIn() ? "&captcha=yes" : "") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		re += "\t\t\t\t\t\t<tr id=\"tk" + i + "\" class=\"mapping\" style=\"display: none;\">\n";
		re += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		re += "\t\t\t\t\t\t\t<td><input type=\"text\" name=\"datatype\" value=\"" + datatype + "\" style=\"width: 23em;\" /></td>\n";
		re += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		re += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('tk" + i + "')\">Hide</a></td>\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"adatatype\" value=\"" + datatype + "\" />\n";
		re += "\t\t\t\t\t\t\t<td>Delete</td>\n";
		re += "\t\t\t\t\t\t</tr>\n";
		re += getUserField("tk" + i + "u", "dmapping", "Save", 5);
		re += "\t\t\t\t\t</form>\n";

		return re;
	}

	/**
	 * Template for Datatype-Mapping delete fields.  Elements for deleting Datatype-Mappings
	 * @param search Search-string, which the user typed in.
	 * @param i table-column id
	 * @param k Datatype-Mapping key
	 * @param datatype Datatype-Mapping datatype
	 * @param affectedEntities Datatype-Mapping affected entities
	 * @return String with HTML-Code
	 */
	private static String datatypeMappingDelete(String search, int i, String k, String datatype, String affectedEntities) {
		String re = "";

		re += "\t\t\t\t\t<form action=\"?tab=search&search=" + search + (!User.getInstance().isLoggedIn() ? "&captcha=yes" : "") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		re += "\t\t\t\t\t\t<tr id=\"tkd" + i + "\" class=\"mapping\" style=\"display: none;\">\n";
		re += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + datatype + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>Edit</td>\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"datatype\" value=\"" + datatype + "\" />\n";
		re += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('tkd" + i + "')\">Hide</a></td>\n";
		re += "\t\t\t\t\t\t</tr>\n";
		re += getUserField("tkd" + i + "u", "dmapping", "Delete", 5);
		re += "\t\t\t\t\t</form>\n";

		return re;
	}

	/**
	 * Template for user fields. Contains User and Comment input-box for Editing and Deleting.
	 * @param id id for toggle visiblity
	 * @param submitName submit name
	 * @param submitValue submit value
	 * @param columns column count
	 * @return String with HTML-Code
	 */
	private static String getUserField(String id, String submitName, String submitValue, int columns) {
		String re = "";

		if ( !User.getInstance().isLoggedIn() ) {
			re += "\t\t\t\t\t\t<tr id=\"" + id + "\" class=\"mapping\" style=\"display: none;\">\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"" + (columns == 7 ? "3" : "2") + "\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t<label>Login or Email:</label>\n";
			re += "\t\t\t\t\t\t\t\t<input type=\"text\" name=\"user\" style=\"width: 20em;\" value=\"" + User.getInstance().getUsername() + "\" required />\n";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"2\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t<label>Comment:</label>\n";
			re += "\t\t\t\t\t\t\t\t<textarea name=\"comment\" placeholder=\"No comment.\" style=\"width: 30em; height: 5em;\" required></textarea>\n";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"" + (columns == 5 ? "1" : "2") + "\">\n";
			re += "\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"" + submitName + "\" value=\"" + submitValue + "\" />";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t</tr>\n";
		}
		else {
			re += "\t\t\t\t\t\t<tr id=\"" + id + "\" class=\"mapping\" style=\"display: none;\">\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"" + (columns == 7 ? "5" : "4") + "\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t<label>Comment:</label>\n";
			re += "\t\t\t\t\t\t\t\t<textarea name=\"comment\" placeholder=\"No comment.\" style=\"width: 30em; height: 5em;\" required></textarea>\n";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"" + (columns == 5 ? "1" : "2") + "\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"" + submitName + "\" value=\"" + submitValue + "\" />";
			re += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"user\" value=\"" + User.getInstance().getUsername() + "\" />\n";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t</tr>\n";
		}

		return re;
	}

	/**
	 * reCaptcha form. Request ReCaptcha from a user who is not logged in.
	 * @param request request
	 * @param search Search-string, which the user typed in.
	 * @return String with HTML-Code
	 */
	public static String captcha(HttpServletRequest request, String search) {
		ReCaptcha c = ReCaptchaFactory.newReCaptcha(Functions.PUBLIC_reCAPTCHA_KEY, Functions.PRIVATE_reCAPTCHA_KEY, false);

		String re;
		re = "\t\t\t\t<article class=\"captcha\">\n";
		re += "\t\t\t\t\t<form action=\"?tab=search&search=" + search + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">";
		re += "\t\t\t\t\t\t<ul>\n";
		re += "\t\t\t\t\t\t\t<li>"+ c.createRecaptchaHtml(null, null) + "</li>\n";
		re += "\t\t\t\t\t\t\t<li><input type=\"submit\" name=\"fcaptcha\" value=\"Send\" /></li>\n";
		re += "\t\t\t\t\t\t</ul>\n";

		if ( request.getParameter("kmapping") != null ) {
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + request.getParameter("k") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"object\" value=\"" + request.getParameter("object") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + request.getParameter("property") + "\" />\n";

			if ( !request.getParameter("kmapping").equals("Delete") ) {
				re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"aproperty\" value=\"" + request.getParameter("aproperty") + "\" />\n";
				re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"aobject\" value=\"" + request.getParameter("aobject") + "\" />\n";
			}

			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"user\" value=\"" + request.getParameter("user") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"comment\" value=\"" + request.getParameter("comment") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"kmapping\" value=\"" + request.getParameter("kmapping") + "\" />\n";
		}
		else if ( request.getParameter("kvmapping") != null ) {
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + request.getParameter("k") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"v\" value=\"" + request.getParameter("v") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"object\" value=\"" + request.getParameter("object") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + request.getParameter("property") + "\" />\n";

			if ( !request.getParameter("kvmapping").equals("Delete") ) {
				re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"aproperty\" value=\"" + request.getParameter("aproperty") + "\" />\n";
				re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"aobject\" value=\"" + request.getParameter("aobject") + "\" />\n";
			}

			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"user\" value=\"" + request.getParameter("user") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"comment\" value=\"" + request.getParameter("comment") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"kvmapping\" value=\"" + request.getParameter("kvmapping") + "\" />\n";
		}
		else if ( request.getParameter("dmapping") != null ) {
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + request.getParameter("k") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"datatypr\" value=\"" + request.getParameter("datatype") + "\" />\n";

			if ( !request.getParameter("dmapping").equals("Delete") )
				re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"adatatype\" value=\"" + request.getParameter("adatatype") + "\" />\n";

			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"user\" value=\"" + request.getParameter("user") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"comment\" value=\"" + request.getParameter("comment") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"dmapping\" value=\"" + request.getParameter("dmapping") + "\" />\n";
		}

		re += "\t\t\t\t\t</form>\n";
		re += "\t\t\t\t</article>\n";
		return re;
	}
}