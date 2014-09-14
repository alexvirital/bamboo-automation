package com.veracode.apiwrapper;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import org.apache.commons.codec.binary.Base64;

public class ResultsAPIWrapper {
	
	private static String GET_APP_BUILDS_URI = "https://analysiscenter.veracode.com/api/2.0/getappbuilds.do";
	private static String DETAILED_REPORT_URI = "https://analysiscenter.veracode.com/api/2.0/detailedreport.do";
	private static String DETAILED_REPORT_PDF_URI = "https://analysiscenter.veracode.com/api/2.0/detailedreportpdf.do";
	private static String SUMMARY_REPORT_URI = "https://analysiscenter.veracode.com/api/2.0/summaryreport.do";
	private static String SUMMARY_REPORT_PDF_URI = "https://analysiscenter.veracode.com/api/2.0/summaryreportpdf.do";
	private static String THIRD_PARTY_REPORT_PDF_URI = "https://analysiscenter.veracode.com/api/2.0/thirdpartyreportpdf.do";
	
	private Proxy proxy;

    public ResultsAPIWrapper() {
        
    }
	
	public void setupProxy(String ipAddress, int port) {
		proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ipAddress, port));
	}
	
	public void setupProxy(String ipAddress, int port, String username, String password) throws Exception {
		proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ipAddress, port));
		Authenticator.setDefault(new ProxyAuthenticator(username, password));
	}

    public void setupAuthorization(HttpsURLConnection connection, java.lang.String username, java.lang.String password) throws Exception {
        String login = username + ":" + password;
        String encodedLogin = Base64.encodeBase64String(login.getBytes());
        connection.setRequestProperty("Authorization", "Basic " + encodedLogin.replaceAll("\n",""));
    }

    public String getAppBuilds(String username, String password) throws Exception {
		URL url = new URL(GET_APP_BUILDS_URI);
		HttpsURLConnection m_connect;
		if (proxy != null) m_connect = (HttpsURLConnection) url.openConnection(proxy);
		else m_connect = (HttpsURLConnection) url.openConnection();
        setupAuthorization(m_connect, username, password);
		
        InputStream is = null;
        try {
            is = m_connect.getInputStream();
            return consumeXMLResponse(is);
        } finally {
			if (is != null) is.close();
		}
    }

    public String detailedReport(String username, String password, String buildId) throws Exception {
        URL url = new URL(DETAILED_REPORT_URI + "?build_id=" + buildId);
		HttpsURLConnection m_connect;
		if (proxy != null) m_connect = (HttpsURLConnection) url.openConnection(proxy);
		else m_connect = (HttpsURLConnection) url.openConnection();
        setupAuthorization(m_connect, username, password);
		
        InputStream is = null;
        try {
            is = m_connect.getInputStream();
            return consumeXMLResponse(is);
        } finally {
			if (is != null) is.close();
		}
    }

    public byte[] detailedReportPdf(String username, String password, String buildId) throws Exception {
        URL url = new URL(DETAILED_REPORT_PDF_URI + "?build_id=" + buildId);
		HttpsURLConnection m_connect;
		if (proxy != null) m_connect = (HttpsURLConnection) url.openConnection(proxy);
		else m_connect = (HttpsURLConnection) url.openConnection();
        setupAuthorization(m_connect, username, password);
        
		InputStream is = null;
        try {
            is = m_connect.getInputStream();
            return consumePDFResponse(is);
        } finally {
			if (is != null) is.close();
		}
    }

    public String summaryReport(String username, String password, String buildId) throws Exception {
        URL url = new URL(SUMMARY_REPORT_URI + "?build_id=" + buildId);
		HttpsURLConnection m_connect;
		if (proxy != null) m_connect = (HttpsURLConnection) url.openConnection(proxy);
		else m_connect = (HttpsURLConnection) url.openConnection();
        setupAuthorization(m_connect, username, password);
		
        InputStream is = null;
        try {
            is = m_connect.getInputStream();
            return consumeXMLResponse(is);
        } finally {
			if (is != null) is.close();
		}
    }

    public byte[] summaryReportPdf(String username, String password, String buildId) throws Exception {
        URL url = new URL(SUMMARY_REPORT_PDF_URI + "?build_id=" + buildId);
		HttpsURLConnection m_connect;
		if (proxy != null) m_connect = (HttpsURLConnection) url.openConnection(proxy);
		else m_connect = (HttpsURLConnection) url.openConnection();
        setupAuthorization(m_connect, username, password);
        
		InputStream is = null;
        try {
            is = m_connect.getInputStream();
            return consumePDFResponse(is);
        } finally {
			if (is != null) is.close();
		}
    }

    public byte[] thirdPartyReportPdf(String username, String password, String buildId) throws Exception {
		URL url = new URL(THIRD_PARTY_REPORT_PDF_URI + "?build_id=" + buildId);
		HttpsURLConnection m_connect;
		if (proxy != null) m_connect = (HttpsURLConnection) url.openConnection(proxy);
		else m_connect = (HttpsURLConnection) url.openConnection();
        setupAuthorization(m_connect, username, password);
        
		InputStream is = null;
        try {
            is = m_connect.getInputStream();
            return consumePDFResponse(is);
        } finally {
			if (is != null) is.close();
		}
    }

    public String consumeXMLResponse(InputStream is) throws Exception {
		StringBuilder ret = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(is));
			
			String input = null;
			while((input = br.readLine()) != null) {
                ret.append(input + "\n");
            }
			
			return ret.toString();
        } finally {
			if (br != null) br.close();
		}
    }
	
	public byte[] consumePDFResponse(InputStream is) throws Exception {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		int nRead;
		byte[] data = new byte[16384];
		while((nRead = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}
		
		buffer.flush();
		
		return buffer.toByteArray();
    }

}
