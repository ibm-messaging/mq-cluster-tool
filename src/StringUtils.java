package com.ibm.xmq.cluster;

import java.util.regex.Pattern;

/**
 * Copyright 2018 IBM Corporation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ---------------------------------------------------------------------------- 
 * 
 * StringUtils Class
 * 
 * A class implementing various String manipulation methods
 * 
 * @author Oliver Fisse (IBM) - fisse@us.ibm.com
 * @version 1.0
 *
 */
public final class StringUtils {
	
	public static boolean blank(String original) {
		
		if (original == null) return false;
		
		return (trim(original).compareTo("") == 0);
	} // end of method blank()
	
	public static String chop(String original) {
		
		if (original == null) return null;
		
		return original.substring(0, original.length() - 2);
	} // end of method chop()
	
	public static String compressSpaces(String original) {
		
		if (original == null) return null;
		
		char c;
		int l = original.length();
		boolean spaceFound = false;
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0; i < l; i++) {
			c = original.charAt(i);
			if (c == ' ') {
				if (!spaceFound) {
					spaceFound = true;
					sb.append(c);
				} // end if
			} else {
				spaceFound = false;
				sb.append(c);
			} // end if
		} // end for
		
		return sb.toString();
	} // end of method compressSpaces()
	
	public static int countChar(String original, char character) {
		
		if (original == null) return -1;
		
		int count = 0;
		int l = original.length();
		
		for (int i = 0; i < l; i++)
			if (original.charAt(i) == character) count++;
		
		return count;
	} // end of method countChar()
	
	public static int countString(String original, String string) {
		
		if (original == null) return -1;
		
		int count = 0;
		int index = 0;
		int offset = 0;
		int stringLength = string.length();
		
		while ((index = original.indexOf(string, offset)) != -1) {
			count++;
			offset = index + stringLength;
		}
		
		return count;
	} // end of method countString()
	
	public static boolean empty(String original) {
		
		if (original == null) return false;
		
		return (original.compareTo("") == 0);
	} // end of method empty()
	
	public static String extractName(String nameValuePair, String delimiter) {
		
		if (nameValuePair == null) return null;
		if (delimiter == null) return nameValuePair;
		
		int p = nameValuePair.indexOf(delimiter);
		
		if (p == -1) return nameValuePair;
		
		return nameValuePair.substring(0, p);
	} // end of method extractName()
	
	public static String extractValue(String nameValuePair, String delimiter) {
		
		if (nameValuePair == null) return null;
		if (delimiter == null) return nameValuePair;
		
		int p = nameValuePair.indexOf(delimiter);
		
		if (p == -1) return nameValuePair;
		
		return nameValuePair.substring(p + delimiter.length());
	} // end of method extractValue()
	
	public static String extractValue(String nameValuePair, String prefixDelim, String suffixDelim) {
		
		if (nameValuePair == null) return null;
		if (prefixDelim == null) return nameValuePair;
		
		int p1 = nameValuePair.indexOf(prefixDelim);
		
		if (p1 == -1) return nameValuePair;
		
		int l = prefixDelim.length();
		
		if (suffixDelim != null) {
			int p2 = nameValuePair.indexOf(suffixDelim);
		
			if (p2 == -1) return nameValuePair.substring(p1 + l);
			else return nameValuePair.substring(p1 + l, p2);
		} else return nameValuePair.substring(p1 + l);
		
	} // end of method extractValue()
	
	public static boolean hasAlNum(String original) {
		
		if (hasAlpha(original) && hasDigit(original)) return true;
		else return false;
	} // end of method hasAlNum()
	
	public static boolean hasAlpha(String original) {
		
		char c;
		
		if (original == null) return false;
		
		for (int i = 0; i < original.length(); i++) {
			c = original.charAt(i);
			if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) return true;
		}
		
		return false;
	} // end of method hasAlpha()
	
	public static boolean hasDigit(String original) {
		
		char c;
		
		if (original == null) return false;
		
		for (int i = 0; i < original.length(); i++) {
			c = original.charAt(i);
			if (c >= '0' && c <= '9') return true;
		}
		
		return false;
	} // end of method hasDigit()
	
	public static boolean isDigit(String original) {
		
		char c;
		
		if (original == null) return false;
		
		for (int i = 0; i < original.length(); i++) {
			c = original.charAt(i);
			if (c < '0' || c > '9') return false;
		}
		
		return true;
	} // end of method isDigit()
	
	
	public static boolean inList(String original, String[] list) {
		
		if (original == null) return false;
		
		boolean inList = false;
		
		for (int i = 0; i < list.length; i++) {
			if (original.compareTo(list[i]) == 0) {
				inList = true;
				break;
			} // end if	
		} // end for
		
		return inList;
	} // end of method inList()
	
	public static boolean inWildcardList(String original, String[] wildcardList) {
		
		if (original == null) return false;
		
		boolean inWildcardList = false;
		
		for (int i = 0; i < wildcardList.length; i++) {
			if (matchWildcard(original, wildcardList[i])) {
				inWildcardList = true;
				break;
			} // end if	
		} // end for
		
		return inWildcardList;
	} // end of method inWildcardList()
	
	public static String left(String original, int nChar) {
		
		if (original == null) return null;
		
		if (nChar < 1) return "";
		else if (nChar >= original.length()) return original;
		     else return original.substring(0, nChar); 
	} // end of method left()
	
	public static String ltrim(String original) {
		
		return ltrim(original, ' ');
	} // end of method ltrim()
	
	public static String ltrim(String original, char ch) {
		
		if (original == null) return null;
			
		int l = original.length();
			
		if (l == 0 || original.charAt(0) != ch) return original;
		
		int i = 1;
		while (i < l && original.charAt(i) == ch) i++;
		
		if (i == l) return "";
		return original.substring(i); 
	} // end of method ltrim()	
	
	public static boolean matchRegexp(String original, String regexp) {
		
		if (original == null) return false;
		
		return Pattern.matches(regexp, original);
	} // end of method matchRegexp()
	
	public static boolean matchWildcard(String original, String wildcard) {
		
		if (original == null) return false;
		
		int l = wildcard.length();
		StringBuffer sb = new StringBuffer();
		
		// Convert wildcard string to regexp string
        sb.append('^');
        
        for (int i = 0, is = l; i < is; i++) {
            char c = wildcard.charAt(i);
            
            switch(c) {
            	// Single character wildcard
            	case '?':
            		sb.append(".");
            		break;
            	// Multiple characters wildcard
                case '*':
                    sb.append(".*");
                    break;
                // Escape special regexp characters
                case '$':
                case '^':
                case '.':
                case '(': 
                case ')':
                case '[':
                case ']':
                case '{':
                case '}':
                case '|':
                case '\\':
                    sb.append("\\");
                    sb.append(c);
                    break;
                // All other characters
                default:
                    sb.append(c);
                    break;
            } // end switch
        } // end for
        sb.append('$');
		
        return Pattern.matches(sb.toString(), original);
	} //  end of method matchWildcard()
	
	public static String padc(String original, int length, char padChar) {
		
		if (original == null) return null;
		
		int l = original.length();
		
		if (l >= length) return original;
		
		int iL = (length - l) / 2;
		int iR = iL + (length - l) % 2;
		return replicate(padChar, iL) + original + replicate(padChar, iR);
	} // end of method padc()
	
	public static String padl(String original, int length, char padChar) {
		
		if (original == null) return null;
		    
		int l = original.length();
		
		if (l >= length) return original;
		return replicate(padChar, length - l) + original;
	} // end of method padl()
	
	public static String padr(String original, int length, char padChar) {
		
		if (original == null) return null;
		    
		int l = original.length();
		
		if (l >= length) return original;
		return original + replicate(padChar, length - l);
	} // end of method padr()
	
	public static String quote(String original) {
		
		if (original == null) return null;
		
		return "'" + original + "'";
	} // end of method quote()
	
	public static String quote(String original, char quoteChar) {
		
		if (original == null) return null;
		
	    return "" + quoteChar + original + quoteChar;
	} // end of method quote()
	
	public static String quote(String original, String quoteChars) {
		
		if (original == null) return null;
		if (quoteChars == null) return original;
		
		int l = quoteChars.length();
		
		if (l <= 0) return StringUtils.quote(original);
		if (l == 1) return StringUtils.quote(original, quoteChars.charAt(0));
		
		return quoteChars.charAt(0) + original + quoteChars.charAt(1);
	} // end of method quote()
	
	public static String removeSpaces(String original) {
		
		if (original == null) return null;
		
		char c;
		int l = original.length();
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0; i < l; i++) {
			c = original.charAt(i);
			if (c != ' ') sb.append(c); 
		} // end for
		
		return sb.toString();
	} // end of method removeSpaces()
	
	public static String replicate(String original, int times) {
		
		if (original == null) return null;
		    
		StringBuffer sb = new StringBuffer();
		
		for (int i = 1; i <= times; i++) {
			sb.append(original);    
		}
		return sb.toString();
	} // end of method replicate()
	
	public static String replicate(char c, int times) {
		    
		return replicate(new Character(c).toString(), times);
	} // end of method replicate()
	
	public static String reverse(String original) {
		
		if (original == null) return null;
		
		StringBuffer sb = new StringBuffer();
		
		for (int i = original.length() - 1; i >= 0; i--) sb.append(original.charAt(i));
		
		return sb.toString();
	} // end of method reverse()
	
	public static String rtrim(String original) {
		
		return rtrim(original, ' ');
	} // end of method rtrim()
	
	public static String rtrim(String original, char ch) {
		
		if (original == null) return null;
		
		int l = original.length();
			
		if (l == 0 || original.charAt(l - 1) != ch) return original;
		
		int i = l - 2;
		while (i >= 0 && original.charAt(i) == ch) i--;
		
		if (i < 0) return "";
		return original.substring(0, i + 1);
	} // end of method rtrim()	
	
	public static String right(String original, int nChar) {
		
		if (original == null) return null;
			
		if (nChar < 1) return "";
		else if (nChar >= original.length()) return original;
		     else return original.substring(original.length() - nChar, original.length());
	} // end of method right()
	
	public static String soundex(String original, boolean useHWRule) {
		
		int l;
		boolean hwRule = false;
		
		if (original == null) return null;
		if ((l = original.length()) == 0) return "0000";
		
		String s = original.toUpperCase();
		StringBuffer sb = new StringBuffer(s.charAt(0) + "");
		int digit, prevDigit = soundexDigit(s.charAt(0)), savedPrevDigit = 0;
		
		for (int i=1; i < l; i++) {
		
			char c = s.charAt(i);
			
			digit = soundexDigit(c);
			
			if (useHWRule && hwRule && digit == savedPrevDigit) {
				prevDigit = digit;
				hwRule = false;
			}
			
			if (digit != 0 && digit != prevDigit) sb.append(digit);
			if (useHWRule && (c == 'H' || c == 'W') && prevDigit != 0) {
				hwRule = true;
				savedPrevDigit = prevDigit;
			} else hwRule = false;
				
			prevDigit = digit;
			
		} // end for
		
		sb.append("000");
		
		return sb.toString().substring(0, 4);
	} // end of method soundex()
	
	private static int soundexDigit(char c) {
		
		int digit;
		
		switch (c) {
			case 'B': case 'F': case 'P': case 'V':
				digit = 1;
				break;
			case 'C': case 'G': case 'J': case 'K': case 'Q': case 'S': case 'X': case 'Z':
				digit = 2;
				break;
		    case 'D': case 'T':
		    	digit = 3;
				break;
		    case 'L':
		    	digit = 4;
				break;
		    case 'M': case 'N':
		    	digit = 5;
				break;
		    case 'R':
		    	digit = 6;
				break;
		    default:
		    	digit = 0;
		} // end switch
		
		return digit;
	} // end of method soundexDigit
	
	public static String space(int nChar) {
		
		if (nChar < 1) return "";
		
		StringBuffer buffer = new StringBuffer(nChar);
		for (int i = 0; i < nChar; i++) {
		  	buffer.append(' ');
		}
		return buffer.toString();
	} // end of method space()
	
	public static boolean startsWithInList(String original, String[] list) {
		
		if (original == null) return false;
		
		boolean inList = false;
		
		for (int i = 0; i < list.length; i++) {
			if (original.startsWith(list[i])) {
				inList = true;
				break;
			} // end if
		} // end for
		
		return inList;
	} // end of method startsWithInList()	
	
	public static String strip(String original, String charSet) {
		
		if (original == null) return null;
		
		int l = original.length();
		StringBuffer buffer = new StringBuffer();
		
		for (int i = 0; i < l; i++) {
			char c = original.charAt(i);
			if (charSet.indexOf(c) == 0) buffer.append(c);
		}
		
		return buffer.toString();
	} // end of method strip()
	
	public static String toHex(byte[] original) {
		
		if (original == null) return null;
		
		int l = original.length;
		StringBuffer buffer = new StringBuffer(l * 2);
		
		for (int i = 0; i < l; i++){
			buffer.append(Integer.toString((original[i] & 0xff) + 0x100, 16).substring(1));
		}

		return buffer.toString();
	} // end of method toHex()
	
	public static String toHex(String original) {
		
		if (original == null) return null;
		
		char[] chars = original.toCharArray();
		StringBuffer buffer = new StringBuffer(original.length() * 2);
		
		for (int i = 0; i < chars.length; i++){
			buffer.append(Integer.toString((chars[i] & 0xff) + 0x100, 16).substring(1));
		}

		return buffer.toString();
	} // end of method toHex()
	
	public static String trim(String original) {
		
		if (original == null) return null;
		
		return StringUtils.rtrim(StringUtils.ltrim(original));
	} // end of method trim()
	
	public static String trim(String original, char ch) {
		
		if (original == null) return null;
		
		return StringUtils.rtrim(StringUtils.ltrim(original, ch), ch);
	} // end of method trim()	
	
	public static String wordwarp(String original, int width, String breakStr, boolean cut) {
		
		return "";
	} // end of method wordwrap()

}
