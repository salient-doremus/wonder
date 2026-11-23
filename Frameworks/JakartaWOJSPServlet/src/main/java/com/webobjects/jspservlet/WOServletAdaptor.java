//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.webobjects.jspservlet;

import com.webobjects.appserver.WOApplication;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.PageContext;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.naming.InitialContext;

public class WOServletAdaptor extends HttpServlet {
    private static final long serialVersionUID = 9074528653436056348L;
    private static final String WOROOT = "WOROOT";
    private static final String LOCALROOT = "LOCALROOT";
    private static final String WOAINSTALLROOT = "WOAINSTALLROOT";
    private static final String WEBINFROOT = "WEBINFROOT";
    private static final String LAUNCH_ROOT_ERR_MSG = "You can either define WOROOT, LOCALROOT and WOAINSTALLROOT as Java system properties (e.g. in your application server's launch script as command line arguments) or in the application Deployment Descriptor file (web.xml).";
    private static final String WEBINF_ROOT_ERR_MSG = "WEBINFROOT can only be used if the application is being deployed in a directory (Servlet Single Directory Deployment). It cannot be used if you are deploying the application in a WAR file.";
    private static Class stringParamType = String.class;
    private static Class urlParamType = URL.class;
    private static Class[] woappInitParamTypesOld;
    private static Class[] woappInitParamTypesNew;
    private static _WOServletAppInterface woApplicationWrapper;
    private static ClassLoader classLoader;
    private static String woRoot;
    private static String localRoot;
    private static String woaInstallRoot;
    private static String webInfRoot;
    private static String classpath;
    private static String[] tokenizedClasspath;
    private static String mainBundlePath;
    private static String mainclassname;
    private static String mainBundleName;
    private static JavaArchiveFilter jaf;
    private static volatile ServletContext _servletContext;

    public static void initStatics(ServletContext servletContext) throws UnavailableException {
        if (woApplicationWrapper == null) {
            _applicationInit(servletContext);
        }

    }

    private static _WOServletAppInterface appInstance(ServletContext servletContext) {
        try {
            initStatics(servletContext);
        } catch (UnavailableException ue) {
            throw new RuntimeException(ue.getMessage());
        }

        return woApplicationWrapper;
    }

    public static Object woApplicationObject() {
        return woApplicationWrapper.applicationObject();
    }

    private static String getRealPath(String path) {
        StringBuffer realPath = null;
        int index;
        if ((index = path.indexOf("WOROOT")) != -1) {
            realPath = new StringBuffer();
            realPath.append(woRoot);
            realPath.append(path.substring(index + "WOROOT".length()));
        } else if ((index = path.indexOf("LOCALROOT")) != -1) {
            realPath = new StringBuffer();
            realPath.append(localRoot);
            realPath.append(path.substring(index + "LOCALROOT".length()));
        } else if ((index = path.indexOf("WOAINSTALLROOT")) != -1) {
            realPath = new StringBuffer();
            realPath.append(woaInstallRoot);
            realPath.append(path.substring(index + "WOAINSTALLROOT".length()));
        } else {
            if ((index = path.indexOf("WEBINFROOT")) == -1) {
                return path;
            }

            realPath = new StringBuffer();
            realPath.append(webInfRoot);
            realPath.append(path.substring(index + "WEBINFROOT".length()));
        }

        return realPath.toString();
    }

    private static String[] tokenizeClasspath(String aClasspath) {
        if (tokenizedClasspath == null) {
            StringTokenizer tokenizer = new StringTokenizer(aClasspath, "\r\n");
            ArrayList<String> al = new ArrayList();

            while(tokenizer.hasMoreTokens()) {
                String realPath = getRealPath(tokenizer.nextToken().trim());
                if (realPath != null) {
                    al.add(realPath);
                    if (mainBundlePath == null) {
                        File aFile = new File(realPath);
                        if (aFile.isFile()) {
                            int bundleIndex = realPath.indexOf(".woa/");
                            if (bundleIndex > 0) {
                                String bundlePath = realPath.substring(0, bundleIndex + 4);
                                mainBundlePath = bundlePath;
                                System.out.println("Found MainBundle at: " + bundlePath);
                            }
                        }
                    }
                }
            }

            addArchivesToList(new File(webInfRoot + "/Extensions/"), al);
            addArchivesToList(new File(localRoot + "/Library/WebObjects/Extensions/"), al);
            tokenizedClasspath = (String[])al.toArray(new String[0]);
        }

        return tokenizedClasspath;
    }

    private static void addArchivesToList(File archiveDir, ArrayList<String> al) {
        if (archiveDir != null && archiveDir.exists() && archiveDir.isDirectory()) {
            File[] archiveDirFiles = archiveDir.listFiles(jaf);

            for(int i = 0; i < archiveDirFiles.length; ++i) {
                try {
                    al.add(archiveDirFiles[i].getCanonicalPath());
                } catch (IOException var6) {
                }
            }

            try {
                al.add(archiveDir.getCanonicalPath().concat(File.separator));
            } catch (IOException var5) {
            }
        }

    }

    private static URL[] mangleClasspathForClassLoader(String aClasspath) {
        String fileURLPrefix = File.pathSeparatorChar == ';' ? "file:///" : "file://";
        String[] classpathElements = tokenizeClasspath(aClasspath);
        URL[] urls = new URL[classpathElements.length];

        for(int i = 0; i < classpathElements.length; ++i) {
            try {
                urls[i] = new URL(fileURLPrefix.concat(classpathElements[i]));
            } catch (Throwable ex) {
                throw new RuntimeException("Error creating URL " + ex);
            }
        }

        return urls;
    }

    private static String mangleClasspathForBundle(String aClasspath) {
        String[] classpathElements = tokenizeClasspath(aClasspath);
        StringBuffer cpbuff = new StringBuffer(aClasspath.length() + classpathElements.length);
        cpbuff.append(classpathElements[0]);

        for(int i = 1; i < classpathElements.length; ++i) {
            cpbuff.append(File.pathSeparator);
            cpbuff.append(classpathElements[i]);
        }

        return cpbuff.toString();
    }

    private static _WOServletAppInterface getAppWrapper(Object app) {
        _WOServletAppInterface appWrapper = null;

        try {
            Class woApplicationWrapperClass = classLoader.loadClass("com.webobjects.jspservlet._WOApplicationWrapper");
            appWrapper = (_WOServletAppInterface)woApplicationWrapperClass.newInstance();
            Class cl = Object.class;
            Class[] paramTypes = new Class[]{cl};
            Object[] params = new Object[]{app};
            Method setter = woApplicationWrapperClass.getMethod("setApplication", paramTypes);
            setter.invoke(appWrapper, params);
            return appWrapper;
        } catch (Throwable ex) {
            ex.printStackTrace();
            throw new RuntimeException("Unable to create an instance of class com.webobjects.jspservlet._WOApplicationWrapper. jakartawojspservlet.jar must be in WOClasspath in your web.xml");
        }
    }

    public static synchronized void _applicationInit(ServletContext servletContext) throws UnavailableException {
        if (woApplicationWrapper == null) {
            try {
                _servletContext = servletContext;
                String adaptorURL = null;
                Method setpropertyMethod = null;
                Class[] woappInitParamTypes = null;
                Object[] woappInitParams = null;
                if (!"YES".equalsIgnoreCase(servletContext.getInitParameter("WOJarBundle")) && !"true".equalsIgnoreCase(servletContext.getInitParameter("WOJarBundle"))) {
                    woRoot = System.getProperty("WOROOT", servletContext.getInitParameter("WOROOT"));
                    localRoot = System.getProperty("LOCALROOT", servletContext.getInitParameter("LOCALROOT"));
                    woaInstallRoot = System.getProperty("WOAINSTALLROOT", servletContext.getInitParameter("WOAINSTALLROOT"));
                    webInfRoot = _servletContext.getRealPath("WEB-INF");
                    classpath = servletContext.getInitParameter("WOClasspath");
                    mainclassname = servletContext.getInitParameter("WOApplicationClass");
                    if (classpath == null || classpath.equals("") || mainclassname == null || mainclassname.equals("")) {
                        throw new UnavailableException("Both WOClasspath and WOApplicationClass must be defined.");
                    }

                    if (classpath.indexOf("WOROOT") != -1 && (woRoot == null || woRoot.equals(""))) {
                        throw new UnavailableException("WOROOT was referenced in the classpath, but not defined. (WOROOT='" + woRoot + "'. " + "You can either define WOROOT, LOCALROOT and WOAINSTALLROOT as Java system properties (e.g. in your application server's launch script as command line arguments) or in the application Deployment Descriptor file (web.xml).");
                    }

                    if (classpath.indexOf("LOCALROOT") != -1 && (localRoot == null || localRoot.equals(""))) {
                        throw new UnavailableException("LOCALROOT was referenced in the classpath, but not defined. (LOCALROOT='" + localRoot + "'. " + "You can either define WOROOT, LOCALROOT and WOAINSTALLROOT as Java system properties (e.g. in your application server's launch script as command line arguments) or in the application Deployment Descriptor file (web.xml).");
                    }

                    if (classpath.indexOf("WOAINSTALLROOT") != -1 && (woaInstallRoot == null || woaInstallRoot.equals(""))) {
                        throw new UnavailableException("WOAINSTALLROOT was referenced in the classpath, but not defined. (WOAINSTALLROOT='" + woaInstallRoot + "'. " + "You can either define WOROOT, LOCALROOT and WOAINSTALLROOT as Java system properties (e.g. in your application server's launch script as command line arguments) or in the application Deployment Descriptor file (web.xml).");
                    }

                    if (classpath.indexOf("WEBINFROOT") != -1 && (webInfRoot == null || webInfRoot.equals(""))) {
                        throw new UnavailableException("WEBINFROOT was referenced in the classpath, but not defined. (WEBINFROOT='" + webInfRoot + "'. " + "WEBINFROOT can only be used if the application is being deployed in a directory (Servlet Single Directory Deployment). It cannot be used if you are deploying the application in a WAR file.");
                    }

                    classLoader = new URLClassLoader(mangleClasspathForClassLoader(classpath), WOServletAdaptor.class.getClassLoader());
                    Class nspropertiesClass = classLoader.loadClass("com.webobjects.foundation.NSProperties");
                    setpropertyMethod = nspropertiesClass.getMethod("_setProperty", String.class, String.class);
                    String cp = mangleClasspathForBundle(classpath);
                    setpropertyMethod.invoke((Object)null, "com.webobjects.classpath", cp);
                    System.out.println("Classpath for WebObjects classloader is " + cp);
                    if (mainBundlePath == null) {
                        throw new UnavailableException("Can't find application bundle. You can either define WOROOT, LOCALROOT and WOAINSTALLROOT as Java system properties (e.g. in your application server's launch script as command line arguments) or in the application Deployment Descriptor file (web.xml).");
                    }

                    setpropertyMethod.invoke((Object)null, "webobjects.user.dir", mainBundlePath);
                    int indexOfWoa = mainBundlePath.indexOf(".woa");
                    int indexOfName = mainBundlePath.lastIndexOf(47, indexOfWoa);
                    adaptorURL = "http://localhost/".concat(mainBundlePath.substring(indexOfName, indexOfWoa)).concat("/WebObjects");
                    woappInitParamTypes = woappInitParamTypesOld;
                    woappInitParams = new Object[]{mainBundlePath, mainclassname};
                } else {
                    mainclassname = servletContext.getInitParameter("WOApplicationClass");
                    if (mainclassname == null || mainclassname.equals("")) {
                        throw new UnavailableException("WOApplicationClass must be defined.");
                    }

                    mainBundleName = servletContext.getInitParameter("WOMainBundle");
                    classLoader = WOServletAdaptor.class.getClassLoader();
                    Class nspropertiesClass = classLoader.loadClass("com.webobjects.foundation.NSProperties");
                    setpropertyMethod = nspropertiesClass.getMethod("_setProperty", String.class, String.class);
                    System.out.println("Classes and frameworks will be loaded out of the WEB-INF/lib and WEB-INF/classes directories.");
                    String applicationName = null;
                    if (mainBundleName != null && !mainBundleName.equals("")) {
                        Method setMainBundleMethod = nspropertiesClass.getMethod("_setMainBundleName", String.class);
                        if (setMainBundleMethod != null) {
                            setMainBundleMethod.invoke((Object)null, mainBundleName);
                            System.out.println("MainBundle will be set to " + mainBundleName);
                        }

                        applicationName = mainBundleName;
                    } else {
                        String realPath = _servletContext.getRealPath("WEB-INF");
                        int indexOfWoa = realPath.indexOf(".woa");
                        int indexOfName = realPath.lastIndexOf(47, indexOfWoa);
                        applicationName = realPath.substring(indexOfName, indexOfWoa);
                    }

                    adaptorURL = "http://localhost/".concat(applicationName).concat("/WebObjects");
                    woappInitParamTypes = woappInitParamTypesNew;
                    woappInitParams = new Object[]{mainBundleName, null, mainclassname};
                }

                setpropertyMethod.invoke((Object)null, "WOAdaptorURL", adaptorURL);
                setpropertyMethod.invoke((Object)null, "WOContextClassName", "com.webobjects.jspservlet.WOServletContext");
                setpropertyMethod.invoke((Object)null, "WOSessionStoreClassName", "com.webobjects.jspservlet.WOServletSessionStore");
                setpropertyMethod.invoke((Object)null, "WOAutoOpenInBrowser", "false");
                setpropertyMethod.invoke((Object)null, "WOAutoOpenClientApplication", "false");
                setpropertyMethod.invoke((Object)null, "WOLifebeatEnabled", "false");
                if (_isDeployed(_servletContext)) {
                    setpropertyMethod.invoke((Object)null, "WOAllowRapidTurnaround", "false");
                }

                Class woApplicationClass = classLoader.loadClass("com.webobjects.appserver.WOApplication");
                Boolean useLog4j = Boolean.FALSE;
                InitialContext ctx = null;

                try {
                    ctx = new InitialContext();
                    useLog4j = (Boolean)ctx.lookup("java:comp/env/wo/NSUseLog4jLogging");
                } catch (Exception var21) {
                }

                if (useLog4j) {
                    Class logClass = classLoader.loadClass("com.webobjects.foundation.NSLog");
                    Class log4jLogClass = classLoader.loadClass("com.webobjects.foundation.NSLog$Log4JLogger");
                    Class nsLogLoggerClass = classLoader.loadClass("com.webobjects.foundation.NSLog$Logger");
                    Class apacheLoggerClass = classLoader.loadClass("org.apache.log4j.Logger");
                    Constructor log4jLogConstructor = log4jLogClass.getConstructor(apacheLoggerClass, Integer.TYPE);
                    Method getLoggerMethod = apacheLoggerClass.getMethod("getLogger", String.class);
                    String applicationName = null;

                    try {
                        if (ctx != null) {
                            applicationName = (String)ctx.lookup("java:comp/env/wo/NSLog4jLoggerName");
                        } else {
                            applicationName = "WebObjectsApp";
                        }
                    } catch (Exception var20) {
                        applicationName = "WebObjectsApp";
                    }

                    Object apacheLogger = getLoggerMethod.invoke((Object)null, applicationName);
                    Method nslogSetOutMethod = logClass.getMethod("setOut", nsLogLoggerClass);
                    Method nslogSetErrMethod = logClass.getMethod("setErr", nsLogLoggerClass);
                    Method nslogSetDebugMethod = logClass.getMethod("setDebug", nsLogLoggerClass);
                    Object log4jLogInstance = log4jLogConstructor.newInstance(apacheLogger, new Integer(2));
                    nslogSetOutMethod.invoke((Object)null, log4jLogInstance);
                    log4jLogInstance = log4jLogConstructor.newInstance(apacheLogger, new Integer(1));
                    nslogSetErrMethod.invoke((Object)null, log4jLogInstance);
                    log4jLogInstance = log4jLogConstructor.newInstance(apacheLogger, new Integer(3));
                    nslogSetDebugMethod.invoke((Object)null, log4jLogInstance);
                }

                Object woApplicationInstance = null;
                if (woApplicationClass == null) {
                    throw new UnavailableException("Unable to find class com.webobjects.appserver.WOApplication. The JavaWebObjects framework must either be in WOClasspath in your web.xml or in the WEB-INF/lib directory.");
                }

                Method getwoApplicationInstance = woApplicationClass.getMethod("application");
                if (getwoApplicationInstance == null) {
                    throw new UnavailableException("Unable to find WOApplication.application().");
                }

                woApplicationInstance = getwoApplicationInstance.invoke(woApplicationClass);
                if (woApplicationInstance == null) {
                    Method primeapp = woApplicationClass.getMethod("primeApplication", woappInitParamTypes);
                    if (primeapp == null) {
                        throw new UnavailableException("Unable to find WOApplication.primeApplication().");
                    }

                    primeapp.invoke(woApplicationClass, woappInitParams);
                    woApplicationInstance = getwoApplicationInstance.invoke(woApplicationClass);
                    if (woApplicationInstance == null) {
                        throw new UnavailableException("WOApplication.primeApplication() failed to initialize WOApplication.");
                    }

                    woApplicationWrapper = getAppWrapper(woApplicationInstance);
                }
            } catch (UnavailableException ue) {
                throw ue;
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new UnavailableException("Error initializing servlet adaptor: " + ex.getMessage());
            }
        }

    }

    protected static String embeddedComponentResponse(String name, Map<String, Object> bindings, Map<String, ? extends List<String>> extraHeaders, PageContext pageContext, boolean bodyContentOnly, boolean mergeResponseHeaders) {
        _servletContext = pageContext.getServletContext();
        HashMap<String, Object> userInfo = new HashMap(6);
        userInfo.put("PageContext", pageContext);
        userInfo.put("HttpServletRequest", pageContext.getRequest());
        userInfo.put("HttpServletResponse", pageContext.getResponse());
        userInfo.put("ServletConfig", pageContext.getServletConfig());
        userInfo.put("ServletContext", _servletContext);
        return _embeddedComponentResponse(name, bindings, extraHeaders, userInfo, (String)null, (String)null, bodyContentOnly, mergeResponseHeaders);
    }

    public static String embeddedComponentResponse(String name, Map<String, Object> bindings, Map<String, ? extends List<String>> extraHeaders, HttpServletRequest servletRequest, HttpServletResponse servletResponse, HttpServlet servlet, boolean bodyContentOnly, boolean mergeResponseHeaders) {
        _servletContext = servlet.getServletContext();
        HashMap<String, Object> userInfo = new HashMap(5);
        userInfo.put("HttpServletRequest", servletRequest);
        userInfo.put("HttpServletResponse", servletResponse);
        userInfo.put("ServletConfig", servlet.getServletConfig());
        userInfo.put("ServletContext", _servletContext);
        return _embeddedComponentResponse(name, bindings, extraHeaders, userInfo, (String)null, (String)null, bodyContentOnly, mergeResponseHeaders);
    }

    private static String _embeddedComponentResponse(String name, Map<String, Object> bindings, Map<String, ? extends List<String>> extraHeaders, Map<String, Object> userInfo, String urlPrefix, String appName, boolean bodyContentOnly, boolean mergeResponseHeaders) {
        _WOServletAppInterface appWrapper = appInstance(_servletContext);
        String anUrlPrefix;
        if (urlPrefix != null) {
            anUrlPrefix = urlPrefix;
        } else {
            anUrlPrefix = ((HttpServletRequest)userInfo.get("HttpServletRequest")).getContextPath().concat("/WebObjects");
        }

        String anAppName;
        if (appName != null) {
            anAppName = appName;
        } else {
            anAppName = appWrapper.applicationName().concat(".woa");
        }

        boolean isDeployed = _isDeployed(_servletContext);
        String contentString = appWrapper.servletResponseForComponentWithName(name, bindings, extraHeaders, userInfo, anUrlPrefix, anAppName, mergeResponseHeaders, isDeployed);
        if (bodyContentOnly) {
            contentString = extractBodyFromContent(contentString);
        }

        return contentString;
    }

    protected static String embeddedDirectActionResponse(String actionName, String className, Map<String, Object> formValues, InputStream contentStream, Map<String, ? extends List<String>> extraHeaders, PageContext pageContext, boolean bodyContentOnly, boolean mergeResponseHeaders) {
        _servletContext = pageContext.getServletContext();
        HashMap<String, Object> userInfo = new HashMap(6);
        userInfo.put("PageContext", pageContext);
        userInfo.put("HttpServletRequest", pageContext.getRequest());
        userInfo.put("HttpServletResponse", pageContext.getResponse());
        userInfo.put("ServletConfig", pageContext.getServletConfig());
        userInfo.put("ServletContext", _servletContext);
        return _embeddedDirectActionResponse(actionName, className, formValues, contentStream, extraHeaders, userInfo, (String)null, (String)null, bodyContentOnly, mergeResponseHeaders);
    }

    public static String embeddedDirectActionResponse(String actionName, String className, Map<String, Object> formValues, InputStream contentStream, Map<String, ? extends List<String>> extraHeaders, HttpServletRequest servletRequest, HttpServletResponse servletResponse, HttpServlet servlet, boolean bodyContentOnly, boolean mergeResponseHeaders) {
        _servletContext = servlet.getServletContext();
        HashMap<String, Object> userInfo = new HashMap(5);
        userInfo.put("HttpServletRequest", servletRequest);
        userInfo.put("HttpServletResponse", servletResponse);
        userInfo.put("ServletConfig", servlet.getServletConfig());
        userInfo.put("ServletContext", _servletContext);
        return _embeddedDirectActionResponse(actionName, className, formValues, contentStream, extraHeaders, userInfo, (String)null, (String)null, bodyContentOnly, mergeResponseHeaders);
    }

    private static String _embeddedDirectActionResponse(String actionName, String className, Map<String, Object> formValues, InputStream contentStream, Map<String, ? extends List<String>> extraHeaders, Map<String, Object> userInfo, String urlPrefix, String appName, boolean bodyContentOnly, boolean mergeResponseHeaders) {
        _WOServletAppInterface appWrapper = appInstance(_servletContext);
        String anUrlPrefix;
        if (urlPrefix != null) {
            anUrlPrefix = urlPrefix;
        } else {
            anUrlPrefix = ((HttpServletRequest)userInfo.get("HttpServletRequest")).getContextPath().concat("/WebObjects");
        }

        String anAppName;
        if (appName != null) {
            anAppName = appName;
        } else {
            anAppName = appWrapper.applicationName().concat(".woa");
        }

        boolean isDeployed = _isDeployed(_servletContext);
        String contentString = appWrapper.servletResponseForDirectActionWithNameAndClass(actionName, className, formValues, contentStream, extraHeaders, userInfo, anUrlPrefix, anAppName, mergeResponseHeaders, isDeployed);
        if (bodyContentOnly) {
            contentString = extractBodyFromContent(contentString);
        }

        return contentString;
    }

    private static String extractBodyFromContent(String contentString) {
        String lowercaseContentString = contentString.toLowerCase();
        int bodyStart = 0;
        int bodyTagStart = lowercaseContentString.indexOf("<body");
        if (bodyTagStart != -1) {
            bodyStart = lowercaseContentString.indexOf(">", bodyTagStart) + 1;
        } else {
            bodyStart = 0;
        }

        int bodyStop = lowercaseContentString.lastIndexOf("</body>");
        if (bodyStop == -1) {
            bodyStop = contentString.length();
        }

        return contentString.substring(bodyStart, bodyStop);
    }

    public WOServletAdaptor() throws ServletException {
    }

    public void init() throws ServletException {
        initStatics(this.getServletContext());
    }

    public void destroy() {
        try {
            if (classLoader != null) {
                Class woApplicationClass = classLoader.loadClass(WOApplication.class.getName());
                if (woApplicationClass != null) {
                    Method getwoApplicationInstance = woApplicationClass.getMethod("application");
                    if (getwoApplicationInstance == null) {
                        throw new UnavailableException("Unable to find WOApplication.application().");
                    }

                    WOApplication woApplicationInstance = (WOApplication)getwoApplicationInstance.invoke(woApplicationClass);
                    woApplicationInstance.terminate();
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            super.destroy();
        }

    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        this._handleRequest(request, response);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        this._handleRequest(request, response);
    }

    private void _handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HashMap<String, Object> userInfo = new HashMap(4);
        userInfo.put("HttpServletRequest", request);
        userInfo.put("HttpServletResponse", response);
        userInfo.put("ServletConfig", this.getServletConfig());
        userInfo.put("ServletContext", this.getServletContext());
        boolean isDeployed = _isDeployed(this.getServletContext());
        woApplicationWrapper.servletDispatchRequest(userInfo, isDeployed);
    }

    private static boolean _isDeployed(ServletContext servletContext) {
        boolean isDeployed = false;
        if (servletContext != null) {
            String resourceMode = servletContext.getInitParameter("WOAppMode");
            if (resourceMode != null && resourceMode.equalsIgnoreCase("Deployment")) {
                isDeployed = true;
            }
        }

        return isDeployed;
    }

    public String toString() {
        return "<WOServletAdaptor> classpath: " + classpath + ", mainBundlePath: " + mainBundlePath + ", mainclassname: " + mainclassname + ", ServletConfig: " + this.getServletConfig() + ", ServletContext: " + this.getServletContext();
    }

    static {
        woappInitParamTypesOld = new Class[]{stringParamType, stringParamType};
        woappInitParamTypesNew = new Class[]{stringParamType, urlParamType, stringParamType};
        woApplicationWrapper = null;
        jaf = new JavaArchiveFilter();
    }

    public static class JavaArchiveFilter implements FilenameFilter {
        private static final String JAR = ".jar";
        private static final String ZIP = ".zip";

        public JavaArchiveFilter() {
        }

        public boolean accept(File dir, String aName) {
            if (aName != null) {
                String lowerName = aName.toLowerCase();
                if (lowerName.endsWith(".jar") || lowerName.endsWith(".zip")) {
                    return true;
                }
            }

            return false;
        }
    }
}
