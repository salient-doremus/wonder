//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.webobjects.jspservlet;

import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOSession;
import com.webobjects.appserver.WOSessionStore;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class WOServletSessionStore extends WOSessionStore {
    static String _sessionAttributeKey = null;
    static String _sessionWatcherAttributeKey = null;

    public WOServletSessionStore() {
        this._timeoutManager.terminate();
        _sessionAttributeKey = "WOSession." + WOApplication.application().name();
        _sessionWatcherAttributeKey = "WOSessionWatcher." + WOApplication.application().name();
    }

    public WOSession removeSessionWithID(String sessionID) {
        return null;
    }

    public WOSession restoreSessionWithID(String sessionID, WORequest woRequest) {
        WOSession woSession = null;
        HttpServletRequest servletRequest = (HttpServletRequest)woRequest.userInfo().valueForKey("HttpServletRequest");
        if (servletRequest != null) {
            woSession = (WOSession)servletRequest.getSession().getAttribute(_sessionAttributeKey);
            if (woSession != null) {
                if (!sessionID.equals(woSession.sessionID())) {
                    woSession._setSessionID(sessionID);
                }

                woSession._setHttpSession(servletRequest.getSession());
            }
        }

        return woSession;
    }

    public void saveSessionForContext(WOContext context) {
        WOSession woSession = context._session();
        WORequest woRequest = context.request();
        if (woSession == null) {
            throw new IllegalStateException(this.toString() + ": Cannot save session because the current context has no WOSession.");
        } else {
            HttpSession servletSession = ((HttpServletRequest)woRequest.userInfo().valueForKey("HttpServletRequest")).getSession();
            if (servletSession != null) {
                try {
                    if (servletSession.getAttribute(_sessionAttributeKey) != woSession) {
                        servletSession.setAttribute(_sessionAttributeKey, woSession);
                        servletSession.setAttribute(_sessionWatcherAttributeKey, new _WOSessionWatcher(woSession));
                    }

                    woSession._setHttpSessionWatcher(servletSession.getAttribute(_sessionWatcherAttributeKey));
                } catch (IllegalStateException var6) {
                    throw new IllegalStateException(this.toString() + ": Cannot save session because the current context's JSP/Servlet session is invalid.");
                }
            } else {
                throw new IllegalStateException(this.toString() + ": Cannot save session because the current context has no JSP/Servlet session.");
            }
        }
    }

    public static WOSession woSessionFromHttpSession(HttpSession anHttpSession) {
        WOSession woSession = null;
        if (anHttpSession != null) {
            woSession = (WOSession)anHttpSession.getAttribute(_sessionAttributeKey);
        }

        return woSession;
    }
}
