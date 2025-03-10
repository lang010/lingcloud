/*
 * $Id: UrlValidator.java,v 1.1 2008/04/22 14:27:16 zouyongqiang Exp $
 * $Rev: 327148 $
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.oro.text.perl.Perl5Util;

/**
 * <p>
 * Validates URLs.
 * </p>
 * Behavour of validation is modified by passing in options: <li>ALLOW_2_SLASHES
 * - [FALSE] Allows double '/' characters in the path component.</li> <li>
 * NO_FRAGMENT- [FALSE] By default fragments are allowed, if this option is
 * included then fragments are flagged as illegal.</li> <li>ALLOW_ALL_SCHEMES -
 * [FALSE] By default only http, https, and ftp are considered valid schemes.
 * Enabling this option will let any scheme pass validation.</li>
 * 
 * <p>
 * Originally based in on php script by Debbie Dyer, validation.php v1.2b, Date:
 * 03/07/02, http://javascript.internet.com. However, this validation now bears
 * little resemblance to the php original.
 * </p>
 * 
 * <pre>
 *    Example of usage:
 *    Construct a UrlValidator with valid schemes of &quot;
 *    http&quot;, and &quot;https&quot;.
 * 
 *     String[] schemes = {&quot;http&quot;,&quot;https&quot;}.
 *     UrlValidator urlValidator = new UrlValidator(schemes);
 *     if (urlValidator.isValid(&quot;ftp://foo.bar.com/&quot;)) {
 *        System.out.println(&quot;url is valid&quot;);
 *     } else {
 *        System.out.println(&quot;url is invalid&quot;);
 *     }
 * 
 *     prints &quot;url is invalid&quot;
 *    If instead the default constructor is used.
 * 
 *     UrlValidator urlValidator = new UrlValidator();
 *     if (urlValidator.isValid(&quot;ftp://foo.bar.com/&quot;)) {
 *        System.out.println(&quot;url is valid&quot;);
 *     } else {
 *        System.out.println(&quot;url is invalid&quot;);
 *     }
 * 
 *    prints out &quot;url is valid&quot;
 * </pre>
 * 
 * @see <a href='http://www.ietf.org/rfc/rfc2396.txt' > Uniform Resource
 *      Identifiers (URI): Generic Syntax </a>
 * 
 * @since Validator 1.1
 */
public class UrlValidator implements Serializable {
	private static final long serialVersionUID = -2616840096135613949L;

	/**
	 * Allows all validly formatted schemes to pass validation instead of
	 * supplying a set of valid schemes.
	 */
	public static final int ALLOW_ALL_SCHEMES = 1 << 0;

	/**
	 * Allow two slashes in the path component of the URL.
	 */
	public static final int ALLOW_2_SLASHES = 1 << 1;

	/**
	 * Enabling this options disallows any URL fragments.
	 */
	public static final int NO_FRAGMENTS = 1 << 2;

	private static final String ALPHA_CHARS = "a-zA-Z";

	private static final String ALPHA_NUMERIC_CHARS = ALPHA_CHARS + "\\d";

	private static final String SPECIAL_CHARS = ";/@&=,.?:+$";

	private static final String VALID_CHARS = "[^\\s" + SPECIAL_CHARS + "]";

	private static final String SCHEME_CHARS = ALPHA_CHARS;

	// Drop numeric, and "+-." for now
	private static final String AUTHORITY_CHARS = ALPHA_NUMERIC_CHARS
			+ "\\-\\.";

	private static final String ATOM = VALID_CHARS + '+';

	/**
	 * This expression derived/taken from the BNF for URI (RFC2396).
	 */
	private static final String URL_PATTERN = "/^(([^:/?#]+):)?"
		+ "(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?/";

	// 12 3 4 5 6 7 8 9

	/**
	 * Schema/Protocol (ie. http:, ftp:, file:, etc).
	 */
	private static final int PARSE_URL_SCHEME = 2;

	/**
	 * Includes hostname/ip and port number.
	 */
	private static final int PARSE_URL_AUTHORITY = 4;

	private static final int PARSE_URL_PATH = 5;

	private static final int PARSE_URL_QUERY = 7;

	private static final int PARSE_URL_FRAGMENT = 9;

	/**
	 * Protocol (ie. http:, ftp:,https:).
	 */
	private static final String SCHEME_PATTERN = "/^[" + SCHEME_CHARS + "]/";

	private static final String AUTHORITY_PATTERN = "/^([" + AUTHORITY_CHARS
			+ "]*)(:\\d*)?(.*)?/";

	// 1 2 3 4

	private static final int PARSE_AUTHORITY_HOST_IP = 1;

	private static final int PARSE_AUTHORITY_PORT = 2;

	/**
	 * Should always be empty.
	 */
	private static final int PARSE_AUTHORITY_EXTRA = 3;

	private static final String PATH_PATTERN = "/^(/[-\\w:@&?=+,.!/~*'%$]*)?$/";

	private static final String QUERY_PATTERN = "/^(.*)$/";

	private static final String LEGAL_ASCII_PATTERN = "/^[\\000-\\177]+$/";

	private static final String IP_V4_DOMAIN_PATTERN = 
		"/^(\\d{1,3})[.](\\d{1,3})[.](\\d{1,3})[.](\\d{1,3})$/";

	private static final String DOMAIN_PATTERN = "/^" + ATOM + "(\\." + ATOM
			+ ")*$/";

	private static final String PORT_PATTERN = "/^:(\\d{1,5})$/";

	private static final String ATOM_PATTERN = "/(" + ATOM + ")/";

	private static final String ALPHA_PATTERN = "/^[" + ALPHA_CHARS + "]/";

	/**
	 * Holds the set of current validation options.
	 */
	private Flags options = null;

	/**
	 * The set of schemes that are allowed to be in a URL.
	 */
	private Set allowedSchemes = new HashSet();

	/**
	 * If no schemes are provided, default to this set.
	 */
	private String[] defaultSchemes = { "http", "https", "ftp" };

	/**
	 * Create a UrlValidator with default properties.
	 */
	public UrlValidator() {
		this(null);
	}

	/**
	 * Behavior of validation is modified by passing in several 
	 * strings options:.
	 * 
	 * @param schemes
	 *            Pass in one or more url schemes to consider valid, passing in
	 *            a null will default to "http,https,ftp" being valid. If a
	 *            non-null schemes is specified then all valid schemes must be
	 *            specified. Setting the ALLOW_ALL_SCHEMES option will ignore
	 *            the contents of schemes.
	 */
	public UrlValidator(String[] schemes) {
		this(schemes, 0);
	}

	/**
	 * Initialize a UrlValidator with the given validation options.
	 * 
	 * @param options
	 *            The options should be set using the public constants declared
	 *            in this class. To set multiple options you simply add them
	 *            together. For example, ALLOW_2_SLASHES + NO_FRAGMENTS enables
	 *            both of those options.
	 */
	public UrlValidator(int options) {
		this(null, options);
	}

	/**
	 * Behavour of validation is modified by passing in options.
	 * 
	 * @param schemes
	 *            The set of valid schemes.
	 * @param options
	 *            The options should be set using the public constants declared
	 *            in this class. To set multiple options you simply add them
	 *            together. For example, ALLOW_2_SLASHES + NO_FRAGMENTS enables
	 *            both of those options.
	 */
	public UrlValidator(String[] schemes, int options) {
		this.options = new Flags(options);

		if (this.options.isOn(ALLOW_ALL_SCHEMES)) {
			return;
		}

		if (schemes == null) {
			schemes = this.defaultSchemes;
		}

		this.allowedSchemes.addAll(Arrays.asList(schemes));
	}

	/**
	 * <p>
	 * Checks if a field has a valid url address.
	 * </p>
	 * 
	 * @param value
	 *            The value validation is being performed on. A
	 *            <code>null</code> value is considered invalid.
	 * @return true if the url is valid.
	 */
	public boolean isValid(String value) {
		if (value == null) {
			return false;
		}

		Perl5Util matchUrlPat = new Perl5Util();
		Perl5Util matchAsciiPat = new Perl5Util();

		if (!matchAsciiPat.match(LEGAL_ASCII_PATTERN, value)) {
			return false;
		}

		// Check the whole url address structure
		if (!matchUrlPat.match(URL_PATTERN, value)) {
			return false;
		}

		if (!isValidScheme(matchUrlPat.group(PARSE_URL_SCHEME))) {
			return false;
		}

		if (!isValidAuthority(matchUrlPat.group(PARSE_URL_AUTHORITY))) {
			return false;
		}

		if (!isValidPath(matchUrlPat.group(PARSE_URL_PATH))) {
			return false;
		}

		if (!isValidQuery(matchUrlPat.group(PARSE_URL_QUERY))) {
			return false;
		}

		if (!isValidFragment(matchUrlPat.group(PARSE_URL_FRAGMENT))) {
			return false;
		}

		return true;
	}

	/**
	 * Validate scheme. If schemes[] was initialized to a non null, then only
	 * those scheme's are allowed. Note this is slightly different than for the
	 * constructor.
	 * 
	 * @param scheme
	 *            The scheme to validate. A <code>null</code> value is
	 *            considered invalid.
	 * @return true if valid.
	 */
	protected boolean isValidScheme(String scheme) {
		if (scheme == null) {
			return false;
		}

		Perl5Util schemeMatcher = new Perl5Util();
		if (!schemeMatcher.match(SCHEME_PATTERN, scheme)) {
			return false;
		}

		if (this.options.isOff(ALLOW_ALL_SCHEMES)) {

			if (!this.allowedSchemes.contains(scheme)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Returns true if the authority is properly formatted. An authority is the
	 * combination of hostname and port. A <code>null</code> authority value is
	 * considered invalid.
	 * 
	 * @param authority
	 *            Authority value to validate.
	 * @return true if authority (hostname and port) is valid.
	 */
	protected boolean isValidAuthority(String authority) {
		if (authority == null) {
			return false;
		}

		Perl5Util authorityMatcher = new Perl5Util();
		Perl5Util matchIPV4Pat = new Perl5Util();

		if (!authorityMatcher.match(AUTHORITY_PATTERN, authority)) {
			return false;
		}

		boolean ipV4Address = false;
		boolean hostname = false;
		// check if authority is IP address or hostname
		String hostIP = authorityMatcher.group(PARSE_AUTHORITY_HOST_IP);
		ipV4Address = matchIPV4Pat.match(IP_V4_DOMAIN_PATTERN, hostIP);

		if (ipV4Address) {
			// this is an IP address so check components
			final int maxSegments = 4;
			final int maxNum = 255;
			for (int i = 1; i <= maxSegments; i++) {
				String ipSegment = matchIPV4Pat.group(i);
				if (ipSegment == null || ipSegment.length() <= 0) {
					return false;
				}

				try {
					if (Integer.parseInt(ipSegment) > maxNum) {
						return false;
					}
				} catch (NumberFormatException e) {
					return false;
				}

			}
		} else {
			// Domain is hostname name
			Perl5Util domainMatcher = new Perl5Util();
			hostname = domainMatcher.match(DOMAIN_PATTERN, hostIP);
		}

		// rightmost hostname will never start with a digit.
		if (hostname) {
			final int maxSetments = 10;
			String[] domainSegment = new String[maxSetments];
			boolean match = true;
			int segmentCount = 0;
			int segmentLength = 0;
			Perl5Util atomMatcher = new Perl5Util();

			while (match) {
				match = atomMatcher.match(ATOM_PATTERN, hostIP);
				if (match) {
					domainSegment[segmentCount] = atomMatcher.group(1);
					segmentLength = domainSegment[segmentCount].length() + 1;
//					hostIP = (segmentLength >= hostIP.length()) ? "" : hostIP
//							.substring(segmentLength);
					if (segmentLength >= hostIP.length()) {
						hostIP = "";
					} else {
						hostIP = hostIP.substring(segmentLength);
					}

					segmentCount++;
				}
			}
			String topLevel = domainSegment[segmentCount - 1];
			if (topLevel.length() < 2 || topLevel.length() > 4) {
				return false;
			}

			// First letter of top level must be a alpha
			Perl5Util alphaMatcher = new Perl5Util();
			if (!alphaMatcher.match(ALPHA_PATTERN, topLevel.substring(0, 1))) {
				return false;
			}

			// Make sure there's a host name preceding the authority.
			if (segmentCount < 2) {
				return false;
			}
		}

		if (!hostname && !ipV4Address) {
			return false;
		}

		String port = authorityMatcher.group(PARSE_AUTHORITY_PORT);
		if (port != null) {
			Perl5Util portMatcher = new Perl5Util();
			if (!portMatcher.match(PORT_PATTERN, port)) {
				return false;
			}
		}

		String extra = authorityMatcher.group(PARSE_AUTHORITY_EXTRA);
		if (!GenericValidator.isBlankOrNull(extra)) {
			return false;
		}

		return true;
	}

	/**
	 * Returns true if the path is valid. A <code>null</code> value is
	 * considered invalid.
	 * 
	 * @param path
	 *            Path value to validate.
	 * @return true if path is valid.
	 */
	protected boolean isValidPath(String path) {
		if (path == null) {
			return false;
		}

		Perl5Util pathMatcher = new Perl5Util();

		if (!pathMatcher.match(PATH_PATTERN, path)) {
			return false;
		}

		int slash2Count = countToken("//", path);
		if (this.options.isOff(ALLOW_2_SLASHES) && (slash2Count > 0)) {
			return false;
		}

		int slashCount = countToken("/", path);
		int dot2Count = countToken("..", path);
		if (dot2Count > 0) {
			if ((slashCount - slash2Count - 1) <= dot2Count) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Returns true if the query is null or it's a properly formatted query
	 * string.
	 * 
	 * @param query
	 *            Query value to validate.
	 * @return true if query is valid.
	 */
	protected boolean isValidQuery(String query) {
		if (query == null) {
			return true;
		}

		Perl5Util queryMatcher = new Perl5Util();
		return queryMatcher.match(QUERY_PATTERN, query);
	}

	/**
	 * Returns true if the given fragment is null or fragments are allowed.
	 * 
	 * @param fragment
	 *            Fragment value to validate.
	 * @return true if fragment is valid.
	 */
	protected boolean isValidFragment(String fragment) {
		if (fragment == null) {
			return true;
		}

		return this.options.isOff(NO_FRAGMENTS);
	}

	/**
	 * Returns the number of times the token appears in the target.
	 * 
	 * @param token
	 *            Token value to be counted.
	 * @param target
	 *            Target value to count tokens in.
	 * @return the number of tokens.
	 */
	protected int countToken(String token, String target) {
		int tokenIndex = 0;
		int count = 0;
		while (tokenIndex != -1) {
			tokenIndex = target.indexOf(token, tokenIndex);
			if (tokenIndex > -1) {
				tokenIndex++;
				count++;
			}
		}
		return count;
	}
}
