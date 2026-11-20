//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.webobjects.jspservlet;

import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOSession;
import com.webobjects.foundation.NSLog;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;

public class WOServletContext extends WOContext implements Cloneable {
    private PageContext _pageContext = null;
    private JspWriter _jspWriter = null;
    private Object _page = null;
    private HttpServletRequest _servletRequest = null;
    private HttpServletResponse _servletResponse = null;
    private ServletConfig _servletConfig = null;
    private ServletContext _servletContext = null;

    public WOServletContext(WORequest aRequest) {
        super(aRequest);
        this._pageContext = (PageContext)this.request().userInfo().valueForKey("PageContext");
        if (this._pageContext != null) {
            this._jspWriter = this._pageContext.getOut();
            this._page = this._pageContext.getPage();
        }

        this._servletRequest = (HttpServletRequest)this.request().userInfo().valueForKey("HttpServletRequest");
        this._servletResponse = (HttpServletResponse)this.request().userInfo().valueForKey("HttpServletResponse");
        this._servletConfig = (ServletConfig)this.request().userInfo().valueForKey("ServletConfig");
        this._servletContext = (ServletContext)this.request().userInfo().valueForKey("SerlvetContext");
    }

    public String _urlWithRequestHandlerKey(String requestHandlerKey, String aRequestHandlerPath, String aQueryString, boolean isSecure) {
        String superURL = super._urlWithRequestHandlerKey(requestHandlerKey, aRequestHandlerPath, aQueryString, isSecure);
        HttpSession servletSession = this.httpServletRequest().getSession(this.hasSession());
        if (servletSession != null) {
            try {
                superURL = this.httpServletResponse().encodeURL(superURL);
            } catch (IllegalStateException e) {
                NSLog._conditionallyLogPrivateException(e);
            }
        }

        return superURL;
    }

    public WOSession session() {
        if (this._session == null && this.httpSession() != null) {
            try {
                String woSessionID = null;
                WOSession woSession = (WOSession)this.httpSession().getAttribute(WOServletSessionStore._sessionAttributeKey);
                if (woSession != null) {
                    woSessionID = woSession.sessionID();
                }

                WOApplication.application().restoreSessionWithID(woSessionID, this);
            } catch (IllegalStateException e) {
                NSLog._conditionallyLogPrivateException(e);
            }
        }

        return this._session == null ? super.session() : this._session;
    }

    public JspWriter jspWriter() {
        return this._jspWriter;
    }

    public Object jspPage() {
        return this._page;
    }

    public HttpServletRequest httpServletRequest() {
        return this._servletRequest;
    }

    public HttpServletResponse httpServletResponse() {
        return this._servletResponse;
    }

    public ServletConfig servletConfig() {
        return this._servletConfig;
    }

    public ServletContext servletContext() {
        return this._servletContext;
    }

    public HttpSession httpSession() {
        return this.httpServletRequest().getSession(false);
    }
}
