//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.webobjects.jspservlet;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspTagException;
import jakarta.servlet.jsp.tagext.TagSupport;

public class WOExtraHeaderTag extends TagSupport {
    private static final long serialVersionUID = 8569112941620367535L;
    private String _key;
    private String _value;

    public WOExtraHeaderTag() {
    }

    public void setKey(String key) {
        this._key = key;
    }

    public void setValue(String value) {
        this._value = value;
    }

    public int doStartTag() throws JspException {
        try {
            WOTag woTag = (WOTag)this.getParent();
            woTag.setExtraHeader(this._key, this._value);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JspTagException(e.getMessage());
        }
    }
}
