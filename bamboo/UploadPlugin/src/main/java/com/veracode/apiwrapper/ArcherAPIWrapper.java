package com.veracode.apiwrapper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import org.apache.commons.codec.binary.Base64;

public class ArcherAPIWrapper {
	
    private static String ARCHER_REPORT_URI = "https://analysiscenter.veracode.com/api/archer.do";
	
	private Proxy proxy;

    public ArcherAPIWrapper() {
        
    }
	
	public void setupProxy(String ipAddress, int port) {
		proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ipAddress, port));
	}
	
	public void setupProxy(String ipAddress, int port, String username, String password) throws Exception {
		proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ipAddress, port));
		Authenticator.setDefault(new ProxyAuthenticator(username, password));
	}

    public void setupAuthorization(ClientHttpRequest request, String username, String password) throws Exception {
        String login = username + ":" + password;
        String encodedLogin = Base64.encodeBase64String(login.getBytes());
        request.getConnection().setRequestProperty("Authorization", "Basic " + encodedLogin);
    }

    public String archerReport(String username, String password) throws Exception {
        return archerReport(username, password, null, null, null, null, null);
    }

    public String archerReport(String username, String password, String appId, String period, String fromDate, String toDate, String scanType) throws Exception {
        URL url = new URL(ARCHER_REPORT_URI);
		ClientHttpRequest request = null;
		if (proxy != null) request = new ClientHttpRequest(url, proxy);
		else request = new ClientHttpRequest(url);
        setupAuthorization(request, username, password);

        if(appId != null) request.setParameter("app_id", appId);
        if(period != null) request.setParameter("period", period);
        if(fromDate != null) request.setParameter("from_date", fromDate);
        if(toDate != null) request.setParameter("to_date", toDate);
        if(scanType != null) request.setParameter("scan_type", scanType);
        
		InputStream is = null;
        try {
            is = request.post();
            return consumeResponse(is);
        } finally {
			if (is != null) is.close();
		}
    }

    public String consumeResponse(InputStream is) throws Exception {
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

}
