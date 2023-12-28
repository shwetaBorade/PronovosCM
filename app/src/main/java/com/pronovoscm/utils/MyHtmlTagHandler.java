package com.pronovoscm.utils;

import android.text.Editable;
import android.text.Html;
import android.util.Log;

import org.xml.sax.XMLReader;

public class MyHtmlTagHandler implements Html.TagHandler {
    String UL = "CUSTOM_UL";
    String OL = "CUSTOM_OL";
    String LI = "CUSTOM_LI";
    String DD = "CUSTOM_DD";
    boolean first = true;
    String parent = null;
    int index = 1;

    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        Log.d("HTML_TAG_HANDLER", "handleTag: tag =  " + tag);
        if (tag.equalsIgnoreCase(UL) || tag.equalsIgnoreCase("ul")) {
            parent = "ul";
        } else if (tag.equalsIgnoreCase(OL) || tag.equalsIgnoreCase("ol")) {
            parent = "ol";
        }

        if (tag.equalsIgnoreCase(LI) || tag.equalsIgnoreCase("li")) {
            if (parent.equalsIgnoreCase("ul")) {
                if (first) {
                    output.append("\n\t•");
                    first = false;
                } else {
                    first = true;
                }
            } else {
                if (first) {
                    output.append("\n\t" + index + ". ");
                    first = false;
                    index++;
                } else {
                    first = true;
                }
            }
        }
        Log.d("HTML_TAG_HANDLER", " handleTag: output =  " + output);
    }

}
