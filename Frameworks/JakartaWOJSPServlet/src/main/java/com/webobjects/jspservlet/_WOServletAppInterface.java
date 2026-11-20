//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.webobjects.jspservlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface _WOServletAppInterface {
    String applicationName();

    Object applicationObject();

    void setSessionStoreClassName(String var1);

    void setContextClassName(String var1);

    void servletDispatchRequest(Map<String, Object> var1, boolean var2) throws IOException;

    String servletResponseForComponentWithName(String var1, Map<String, Object> var2, Map<String, ? extends List<String>> var3, Map<String, Object> var4, String var5, String var6, boolean var7, boolean var8);

    String servletResponseForDirectActionWithNameAndClass(String var1, String var2, Map<String, Object> var3, InputStream var4, Map<String, ? extends List<String>> var5, Map<String, Object> var6, String var7, String var8, boolean var9, boolean var10);
}
