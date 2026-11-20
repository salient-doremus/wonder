//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.webobjects.jspservlet;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspTagException;
import jakarta.servlet.jsp.tagext.TagSupport;

public class WOFormValueTag extends TagSupport {
    private static final long serialVersionUID = 6854090379927036523L;
    private String _key;
    private String _value;

    public WOFormValueTag() {
    }

    public void setKey(String key) {
        this._key = key;
    }

    public void setValue(String value) {
        this._value = value;
    }

    public int doStartTag() throws JspException {
        try {
            WODirectActionTag directActionTag = (WODirectActionTag)this.getParent();
            directActionTag.setFormValue(this._key, this._value);
            return 0;
        } catch (Exception e) {
            throw new JspTagException(e.getMessage());
        }
    }
}
