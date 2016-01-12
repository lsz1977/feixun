/**
 * Project: Callga
 * Create At 2015-3-5.
 * @author hhool
 */
package com.cchat.common.base.data;

import android.net.Uri;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Hbutils {

    public static String URIEncoder(String text, String allow) {
        if (text == null) {
            text = "";
        }
        return Uri.encode(text, allow);
    }

    public static String URIDecoder(String text) {
        if (text == null) {
            text = "";
        }
        return Uri.decode(text);
    }

    /*public static String URLEncoderUTF8(String text) {
        String encoderText = null;
        if (text == null) {
            text = "";
        }
        try {
            encoderText = URLEncoder.encode(text, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return text;
        }
        return encoderText;
    }

    public static String URLDecoderUTF8(String text) {
        String decoderText = null;
        if (text == null) {
            text = "";
        }
        try {
            decoderText = URLDecoder.decode(text, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return text;
        }
        return decoderText;
    }*/

    public static String toLowerCase(String text) {
        Locale defloc = Locale.getDefault();
        if (text == null) {
            text = "";
        }
        return text.toLowerCase(defloc);
    }

    public static String toUpperCase(String text) {
        Locale defloc = Locale.getDefault();
        if (text == null) {
            text = "";
        }
        return text.toUpperCase(defloc);
    }

    public static boolean checkSpecialCharacter(String str) {
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }

    public static String filterSpecialCharacter(String str) {
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    public static String htmlEncode(String s) {
        StringBuilder sb = new StringBuilder();
        char c;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            switch (c) {
                case '<':
                    sb.append("&lt;"); //$NON-NLS-1$
                    break;
                case '>':
                    sb.append("&gt;"); //$NON-NLS-1$
                    break;
                case '&':
                    sb.append("&amp;"); //$NON-NLS-1$
                    break;
                case '\'':
                    sb.append("&apos;"); //$NON-NLS-1$
                    break;
                case '"':
                    sb.append("&quot;"); //$NON-NLS-1$
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }
}
