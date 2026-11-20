//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.webobjects.jspservlet;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyTagSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

abstract class WOTag extends BodyTagSupport {
    protected String _className;
    protected Map<String, List<String>> _extraHeaders;
    protected boolean _bodyContentOnly = true;
    protected boolean _mergeResponseHeaders;

    public WOTag() {
    }

    public void setClassName(String className) {
        this._className = className;
    }

    public void setClassname(String className) {
        this.setClassName(className);
    }

    public void setExtraHeader(String key, String value) {
        List<String> originalValue = (List)this._extraHeaders.get(key);
        if (originalValue == null) {
            List<String> v = new ArrayList();
            v.add(value);
            this._extraHeaders.put(key, v);
        } else {
            originalValue.add(value);
        }

    }

    public void setBodyContentOnly(boolean bodyContentOnly) {
        this._bodyContentOnly = bodyContentOnly;
    }

    public void setMergeResponseHeaders(boolean mergeResponseHeaders) {
        this._mergeResponseHeaders = mergeResponseHeaders;
    }

    public abstract int doStartTag() throws JspException;

    public abstract int doEndTag() throws JspException;
}
