//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.webobjects.jspservlet;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspTagException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class WODirectActionTag extends WOTag {
    private static final long serialVersionUID = 1114185219117581429L;
    private String _actionName;
    private Map<String, Object> _formValues;
    private InputStream _contentStream;

    public WODirectActionTag() {
    }

    public void setActionName(String name) {
        this._actionName = name;
    }

    public void setFormValue(String key, String value) {
        this._formValues.put(key, value);
    }

    public void setContentStream(InputStream is) {
        this._contentStream = is;
    }

    public int doStartTag() throws JspException {
        try {
            this._formValues = new HashMap();
            this._extraHeaders = new HashMap();
            return 2;
        } catch (Exception e) {
            throw new JspTagException(e.getMessage());
        }
    }

    public int doEndTag() throws JspException {
        try {
            String content = WOServletAdaptor.embeddedDirectActionResponse(this._actionName, this._className, this._formValues, this._contentStream, this._extraHeaders, this.pageContext, this._bodyContentOnly, this._mergeResponseHeaders);
            this.pageContext.getOut().print(content);
            return 6;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JspTagException(e.getMessage());
        }
    }
}
