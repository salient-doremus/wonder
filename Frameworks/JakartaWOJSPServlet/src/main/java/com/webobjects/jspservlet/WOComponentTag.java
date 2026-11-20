//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.webobjects.jspservlet;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspTagException;
import java.util.HashMap;

public class WOComponentTag extends WOTag {
    private static final long serialVersionUID = 1915333196866810491L;
    private HashMap<String, Object> _bindings;

    public WOComponentTag() {
    }

    public void setBinding(String key, Object value) {
        this._bindings.put(key, value);
    }

    public int doStartTag() throws JspException {
        try {
            this._bindings = new HashMap();
            this._extraHeaders = new HashMap();
            return 2;
        } catch (Exception e) {
            throw new JspTagException(e.getMessage());
        }
    }

    public int doEndTag() throws JspException {
        try {
            String content = WOServletAdaptor.embeddedComponentResponse(this._className, this._bindings, this._extraHeaders, this.pageContext, this._bodyContentOnly, this._mergeResponseHeaders);
            this.pageContext.getOut().print(content);
            return 6;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JspTagException(e.getMessage());
        }
    }
}
