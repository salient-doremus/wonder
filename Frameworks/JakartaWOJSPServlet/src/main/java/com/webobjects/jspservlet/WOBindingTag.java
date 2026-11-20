//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.webobjects.jspservlet;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspTagException;
import jakarta.servlet.jsp.tagext.TagSupport;

public class WOBindingTag extends TagSupport {
    private static final long serialVersionUID = 6024833228978897743L;
    private String _key;
    private Object _value;

    public WOBindingTag() {
    }

    public void setKey(String key) {
        this._key = key;
    }

    public void setValue(Object value) {
        this._value = value;
    }

    public int doStartTag() throws JspException {
        try {
            WOComponentTag componentTag = (WOComponentTag)this.getParent();
            componentTag.setBinding(this._key, this._value);
            return 0;
        } catch (Exception e) {
            throw new JspTagException(e.getMessage());
        }
    }
}
