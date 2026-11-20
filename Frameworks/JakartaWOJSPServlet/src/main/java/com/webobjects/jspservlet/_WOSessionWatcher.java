//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.webobjects.jspservlet;

import com.webobjects.appserver.WOSession;
import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionBindingListener;
import java.io.Serializable;

public class _WOSessionWatcher implements Serializable, HttpSessionBindingListener {
    private static final long serialVersionUID = -5396700144337581714L;
    WOSession mySession;

    public _WOSessionWatcher(WOSession aSession) {
        this.mySession = aSession;
    }

    public void _clearWOSession() {
        this.mySession = null;
    }

    public void valueBound(HttpSessionBindingEvent event) {
    }

    public void valueUnbound(HttpSessionBindingEvent event) {
        if (this.mySession != null) {
            this.mySession._setHttpSession((Object)null);
            this.mySession._terminateByJ2EE();
        }

    }
}
