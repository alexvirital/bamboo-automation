package com.veracode.apiwrapper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import org.apache.commons.codec.binary.Base64;

public class UploadAPIWrapper {
	
	private static String CREATE_APP_URI = "https://analysiscenter.veracode.com/api/4.0/createapp.do";
	private static String DELETE_APP_URI = "https://analysiscenter.veracode.com/api/4.0/deleteapp.do";
	private static String GET_VENDOR_LIST_URI = "https://analysiscenter.veracode.com/api/4.0/getvendorlist.do";
	private static String CREATE_BUILD_URI = "https://analysiscenter.veracode.com/api/4.0/createbuild.do";
	private static String DELETE_BUILD_URI = "https://analysiscenter.veracode.com/api/4.0/deletebuild.do";
	private static String UPLOAD_FILE_URI = "https://analysiscenter.veracode.com/api/4.0/uploadfile.do";
	private static String REMOVE_FILE_URI = "https://analysiscenter.veracode.com/api/4.0/removefile.do";
	private static String GET_FILE_LIST_URI = "https://analysiscenter.veracode.com/api/4.0/getfilelist.do";
	private static String GET_APP_LIST_URI = "https://analysiscenter.veracode.com/api/4.0/getapplist.do";
	private static String GET_APP_INFO_URI = "https://analysiscenter.veracode.com/api/4.0/getappinfo.do";
	private static String GET_BUILD_LIST_URI = "https://analysiscenter.veracode.com/api/4.0/getbuildlist.do";
	private static String GET_BUILD_INFO_URI = "https://analysiscenter.veracode.com/api/4.0/getbuildinfo.do";
	private static String BEGIN_PRESCAN_URI = "https://analysiscenter.veracode.com/api/4.0/beginprescan.do";
	private static String GET_PRESCAN_RESULTS_URI = "https://analysiscenter.veracode.com/api/4.0/getprescanresults.do";
	private static String BEGIN_SCAN_URI = "https://analysiscenter.veracode.com/api/4.0/beginscan.do";
	private static String GET_CALL_STACKS_URI = "https://analysiscenter.veracode.com/api/2.0/getcallstacks.do";
	
	private Proxy proxy;

    public UploadAPIWrapper() {
		
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
        request.getConnection().setRequestProperty("Authorization", "Basic " + encodedLogin.replaceAll("\n",""));
    }

    public String createApp(String username, String password, String appName, String businessCriticality) throws Exception {
        return createApp(username, password, appName, null, null, businessCriticality, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    public String createApp(String username, String password, String appName, String description, String vendorId, String businessCriticality, String policy, String businessUnit, String businessOwner, String businessOwnerEmail, String teams, String origin, String industry, String appType, String deploymentType, String webApplication, String archerAppName, String tags) throws Exception {
        URL url = new URL(CREATE_APP_URI);
		ClientHttpRequest request = null;
		if (proxy != null) request = new ClientHttpRequest(url, proxy);
		else request = new ClientHttpRequest(url);
        setupAuthorization(request, username, password);
		
        if(appName != null) request.setParameter("app_name", appName);
        if(description != null) request.setParameter("description", description);
        if(vendorId != null) request.setParameter("vendor_id", vendorId);
        if(businessCriticality != null) request.setParameter("business_criticality", businessCriticality);
        if(policy != null) request.setParameter("policy", policy);
        if(businessUnit != null) request.setParameter("business_unit", businessUnit);
        if(businessOwner != null) request.setParameter("business_owner", businessOwner);
        if(businessOwnerEmail != null) request.setParameter("business_owner_email", businessOwnerEmail);
        if(teams != null) request.setParameter("teams", teams);
        if(origin != null) request.setParameter("origin", origin);
        if(industry != null) request.setParameter("industry", industry);
        if(appType != null) request.setParameter("app_type", appType);
        if(deploymentType != null) request.setParameter("deployment_type", deploymentType);
        if(webApplication != null) request.setParameter("web_application", webApplication);
        if(archerAppName != null) request.setParameter("archer_app_name", archerAppName);
        if(tags != null) request.setParameter("tags", tags);
		
        InputStream is = null;
        try {
            is = request.post();
            return consumeResponse(is);
        } finally {
			if (is != null) is.close();
		}
    }

    public String deleteApp(String username, String password, String appId) throws Exception {
        URL url = new URL(DELETE_APP_URI);
        ClientHttpRequest request = null;
		if (proxy != null) request = new ClientHttpRequest(url, proxy);
		else request = new ClientHttpRequest(url);
        setupAuthorization(request, username, password);
		
        if(appId != null) request.setParameter("app_id", appId);
		
        InputStream is = null;
        try {
            is = request.post();
            return consumeResponse(is);
        } finally {
			if (is != null) is.close();
		}
    }

    public String getVendorList(String username, String password) throws Exception {
        URL url = new URL(GET_VENDOR_LIST_URI);
        ClientHttpRequest request = null;
		if (proxy != null) request = new ClientHttpRequest(url, proxy);
		else request = new ClientHttpRequest(url);
        setupAuthorization(request, username, password);
		
		InputStream is = null;
        try {
            is = request.post();
            return consumeResponse(is);
        } finally {
			if (is != null) is.close();
		}
    }

    public String createBuild(String username, String password, String appId, String version) throws Exception {
        return createBuild(username, password, appId, version, null, null, null);
    }
    
    public String createBuild(String username, String password, String appId, String version, String platform) throws Exception {
        return createBuild(username, password, appId, version, platform, null, null);
    }

    public String createBuild(String username, String password, String appId, String version, String platform, String lifecycleStage, String launchDate) throws Exception {
        URL url = new URL(CREATE_BUILD_URI);
        ClientHttpRequest request = null;
		if (proxy != null) request = new ClientHttpRequest(url, proxy);
		else request = new ClientHttpRequest(url);
        setupAuthorization(request, username, password);
		
        if(appId != null) request.setParameter("app_id", appId);
        if(version != null) request.setParameter("version", version);
        if(platform != null) request.setParameter("platform", platform);
        if(lifecycleStage != null) request.setParameter("lifecycle_stage", lifecycleStage);
        if(launchDate != null) request.setParameter("launch_date", launchDate);
        
		InputStream is = null;
        try {
            is = request.post();
            return consumeResponse(is);
        } finally {
			if (is != null) is.close();
		}
    }

    public String deleteBuild(String username, String password, String appId) throws Exception {
        URL url = new URL(DELETE_BUILD_URI);
        ClientHttpRequest request = null;
		if (proxy != null) request = new ClientHttpRequest(url, proxy);
		else request = new ClientHttpRequest(url);
        setupAuthorization(request, username, password);
		
        if(appId != null) request.setParameter("app_id", appId);
        
		InputStream is = null;
        try {
            is = request.post();
            return consumeResponse(is);
        } finally {
			if (is != null) is.close();
		}
    }

    public String uploadFile(String username, String password, String appId, String filePath) throws Exception {
        URL url = new URL(UPLOAD_FILE_URI);
        ClientHttpRequest request = null;
		if (proxy != null) request = new ClientHttpRequest(url, proxy);
		else request = new ClientHttpRequest(url);
        setupAuthorization(request, username, password);
		
        if(appId != null) request.setParameter("app_id", appId);
        if(filePath != null) request.setParameter("file", new java.io.File(filePath));
        
		InputStream is = null;
        try {
            is = request.post();
            return consumeResponse(is);
        } finally {
			if (is != null) is.close();
		}
    }

    public String removeFile(String username, String password, String appId, String fileId) throws Exception {
        URL url = new URL(REMOVE_FILE_URI);
        ClientHttpRequest request = null;
		if (proxy != null) request = new ClientHttpRequest(url, proxy);
		else request = new ClientHttpRequest(url);
        setupAuthorization(request, username, password);
		
        if(appId != null) request.setParameter("app_id", appId);
        if(fileId != null) request.setParameter("file_id", fileId);
        
		InputStream is = null;
        try {
            is = request.post();
            return consumeResponse(is);
        } finally {
			if (is != null) is.close();
		}
    }

    public String getFileList(String username, String password, String appId) throws Exception {
        return getFileList(username, password, appId, null);
    }

    public String getFileList(String username, String password, String appId, String buildId) throws Exception {
        URL url = new URL(GET_FILE_LIST_URI);
        ClientHttpRequest request = null;
		if (proxy != null) request = new ClientHttpRequest(url, proxy);
		else request = new ClientHttpRequest(url);
        setupAuthorization(request, username, password);
		
        if(appId != null) request.setParameter("app_id", appId);
        if(buildId != null) request.setParameter("build_id", buildId);
        
		InputStream is = null;
        try {
            is = request.post();
            return consumeResponse(is);
        } finally {
			if (is != null) is.close();
		}
    }

    public String getAppList(String username, String password) throws Exception {
        URL url = new URL(GET_APP_LIST_URI);
        ClientHttpRequest request = null;
		if (proxy != null) request = new ClientHttpRequest(url, proxy);
		else request = new ClientHttpRequest(url);
        setupAuthorization(request, username, password);
		
		InputStream is = null;
        try {
            is = request.post();
            return consumeResponse(is);
        } finally {
			if (is != null) is.close();
		}
    }

    public String getAppInfo(String username, String password, String appId) throws Exception {
        URL url = new URL(GET_APP_INFO_URI);
        ClientHttpRequest request = null;
		if (proxy != null) request = new ClientHttpRequest(url, proxy);
		else request = new ClientHttpRequest(url);
        setupAuthorization(request, username, password);
		
        if(appId != null) request.setParameter("app_id", appId);
        
		InputStream is = null;
        try {
            is = request.post();
            return consumeResponse(is);
        } finally {
			if (is != null) is.close();
		}
    }

    public String getBuildList(String username, String password, String appId) throws Exception {
        URL url = new URL(GET_BUILD_LIST_URI);
        ClientHttpRequest request = null;
		if (proxy != null) request = new ClientHttpRequest(url, proxy);
		else request = new ClientHttpRequest(url);
        setupAuthorization(request, username, password);
		
        if(appId != null) request.setParameter("app_id", appId);
        
		InputStream is = null;
        try {
            is = request.post();
            return consumeResponse(is);
        } finally {
			if (is != null) is.close();
		}
    }

    public String getBuildInfo(String username, String password, String appId) throws Exception {
        return getBuildInfo(username, password, appId, null);
    }

    public String getBuildInfo(String username, String password, String appId, String buildId) throws Exception {
        URL url = new URL(GET_BUILD_INFO_URI);
        ClientHttpRequest request = null;
		if (proxy != null) request = new ClientHttpRequest(url, proxy);
		else request = new ClientHttpRequest(url);
        setupAuthorization(request, username, password);
		
        if(appId != null) request.setParameter("app_id", appId);
        if(buildId != null) request.setParameter("build_id", buildId);
        
		InputStream is = null;
        try {
            is = request.post();
            return consumeResponse(is);
        } finally {
			if (is != null) is.close();
		}
    }

    public String beginPrescan(String username, String password, String appId) throws Exception {
        URL url = new URL(BEGIN_PRESCAN_URI);
        ClientHttpRequest request = null;
		if (proxy != null) request = new ClientHttpRequest(url, proxy);
		else request = new ClientHttpRequest(url);
        setupAuthorization(request, username, password);
		
        if(appId != null) request.setParameter("app_id", appId);
        
		InputStream is = null;
        try {
            is = request.post();
            return consumeResponse(is);
        } finally {
			if (is != null) is.close();
		}
    }

    public String getPrescanResults(String username, String password, String appId) throws Exception {
        return getPrescanResults(username, password, appId, null);
    }

    public String getPrescanResults(String username, String password, String appId, String buildId) throws Exception {
        URL url = new URL(GET_PRESCAN_RESULTS_URI);
        ClientHttpRequest request = null;
		if (proxy != null) request = new ClientHttpRequest(url, proxy);
		else request = new ClientHttpRequest(url);
        setupAuthorization(request, username, password);
		
        if(appId != null) request.setParameter("app_id", appId);
        if(buildId != null) request.setParameter("build_id", buildId);
        
		InputStream is = null;
        try {
            is = request.post();
            return consumeResponse(is);
        } finally {
			if (is != null) is.close();
		}
    }

    public String beginScan(String username, String password, String appId) throws Exception {
        return beginScan(username, password, appId, null, "true");
    }

    public String beginScan(String username, String password, String appId, String modules, String scanAllTopLevelModules) throws Exception {
        URL url = new URL(BEGIN_SCAN_URI);
        ClientHttpRequest request = null;
		if (proxy != null) request = new ClientHttpRequest(url, proxy);
		else request = new ClientHttpRequest(url);
        setupAuthorization(request, username, password);
		
        if(appId != null) request.setParameter("app_id", appId);
        if(modules != null) request.setParameter("modules", modules);
        if(scanAllTopLevelModules != null) request.setParameter("scan_all_top_level_modules", scanAllTopLevelModules);
        
		InputStream is = null;
        try {
            is = request.post();
            return consumeResponse(is);
        } finally {
			if (is != null) is.close();
		}
    }
    
    public String getCallStacks(String username, String password, String buildId, String flawId) throws Exception {
        URL url = new URL(GET_CALL_STACKS_URI);
        ClientHttpRequest request = null;
		if (proxy != null) request = new ClientHttpRequest(url, proxy);
		else request = new ClientHttpRequest(url);
        setupAuthorization(request, username, password);
		
        if(buildId != null) request.setParameter("build_id", buildId);
        if(flawId != null) request.setParameter("flaw_id", flawId);

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
