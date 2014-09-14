package com.veracode.apiwrapper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import org.apache.commons.codec.binary.Base64;

public class AdminAPIWrapper {
	
    private static String GET_USER_LIST_URI = "https://analysiscenter.veracode.com/api/2.0/getuserlist.do";
	private static String GET_USER_INFO_URI = "https://analysiscenter.veracode.com/api/2.0/getuserinfo.do";
	private static String CREATE_USER_URI = "https://analysiscenter.veracode.com/api/2.0/createuser.do";
	private static String UPDATE_USER_URI = "https://analysiscenter.veracode.com/api/2.0/updateuser.do";
	private static String DELETE_USER_URI = "https://analysiscenter.veracode.com/api/2.0/deleteuser.do";
	private static String GET_TEAM_LIST_URI = "https://analysiscenter.veracode.com/api/2.0/getteamlist.do";
	private static String CREATE_TEAM_URI = "https://analysiscenter.veracode.com/api/2.0/createteam.do";
	private static String UPDATE_TEAM_URI = "https://analysiscenter.veracode.com/api/2.0/updateteam.do";
	private static String DELETE_TEAM_URI = "https://analysiscenter.veracode.com/api/2.0/deleteteam.do";
	private static String GET_CURRICULUM_LIST_URI = "https://analysiscenter.veracode.com/api/2.0/getcurriculumlist.do";
	private static String GET_TRACK_LIST_URI = "https://analysiscenter.veracode.com/api/2.0/gettracklist.do";
	
	private Proxy proxy;

    public AdminAPIWrapper() {
		
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

    public String getUserList(String username, String password) throws Exception {
        return getUserList(username, password, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    public String getUserList(String username, String password, String customId, String firstName, String lastName, String loginAccountType, String emailAddress, String isSAMLUser, String loginEnabled, String requiresToken, String teams, String roles, String isELearningManager, String eLearningManager, String eLearningTrack, String eLearningCurriculum, String keepELearningActive) throws Exception {
		
        URL url = new URL(GET_USER_LIST_URI);
		ClientHttpRequest request = null;
		if (proxy != null) request = new ClientHttpRequest(url, proxy);
		else request = new ClientHttpRequest(url);
        setupAuthorization(request, username, password);
		
        if(customId != null) request.setParameter("custom_id", customId);
		if(firstName != null) request.setParameter("first_name", firstName);
		if(lastName != null) request.setParameter("last_name", lastName);
		if(loginAccountType != null) request.setParameter("login_account_type", loginAccountType);
		if(emailAddress != null) request.setParameter("email_address", emailAddress);
		if(isSAMLUser != null) request.setParameter("is_saml_user", isSAMLUser);
		if(loginEnabled != null) request.setParameter("login_enabled", loginEnabled);
		if(requiresToken != null) request.setParameter("requires_token", requiresToken);
		if(teams != null) request.setParameter("teams", teams);
		if(roles != null) request.setParameter("roles", roles);
		if(isELearningManager != null) request.setParameter("is_elearning_manager", isELearningManager);
		if(eLearningManager != null) request.setParameter("elearning_manager", eLearningManager);
		if(eLearningTrack != null) request.setParameter("elearning_track", eLearningTrack);
		if(eLearningCurriculum != null) request.setParameter("elearning_curriculum", eLearningCurriculum);
		if(keepELearningActive != null) request.setParameter("keep_elearning_active", keepELearningActive);
		
        InputStream is = null;
        try {
            is = request.post();
            return consumeResponse(is);
        } finally {
			if (is != null) is.close();
		}
    }

    public String getUserInfo(String username, String password, String targetUsername, String targetCustomId) throws Exception {
		
        URL url = new URL(GET_USER_INFO_URI);
        ClientHttpRequest request = null;
		if (proxy != null) request = new ClientHttpRequest(url, proxy);
		else request = new ClientHttpRequest(url);
        setupAuthorization(request, username, password);
		
        if(targetUsername != null) request.setParameter("username", targetUsername);
		if(targetCustomId != null) request.setParameter("custom_id", targetCustomId);
		
        InputStream is = null;
        try {
            is = request.post();
            return consumeResponse(is);
        } finally {
			if (is != null) is.close();
		}
    }

    public String createUser(String username, String password, String firstName, String lastName, String emailAddress) throws Exception {
        return createUser(username, password, null, firstName, lastName, null, emailAddress, null, null, null, null, null, null, null, null, null, null);
    }

    public String createUser(String username, String password, String customId, String firstName, String lastName, String loginAccountType, String emailAddress, String isSAMLUser, String loginEnabled, String requiresToken, String teams, String roles, String isELearningManager, String eLearningManager, String eLearningTrack, String eLearningCurriculum, String keepELearningActive) throws Exception {
        URL url = new URL(CREATE_USER_URI);
        ClientHttpRequest request = null;
		if (proxy != null) request = new ClientHttpRequest(url, proxy);
		else request = new ClientHttpRequest(url);
        setupAuthorization(request, username, password);
		
        if(customId != null) request.setParameter("custom_id", customId);
        if(firstName != null) request.setParameter("first_name", firstName);
        if(lastName != null) request.setParameter("last_name", lastName);
        if(loginAccountType != null) request.setParameter("login_account_type", loginAccountType);
        if(emailAddress != null) request.setParameter("email_address", emailAddress);
        if(isSAMLUser != null) request.setParameter("is_saml_user", isSAMLUser);
        if(loginEnabled != null) request.setParameter("login_enabled", loginEnabled);
        if(requiresToken != null) request.setParameter("requires_token", requiresToken);
        if(teams != null) request.setParameter("teams", teams);
        if(roles != null) request.setParameter("roles", roles);
        if(isELearningManager != null) request.setParameter("is_elearning_manager", isELearningManager);
        if(eLearningManager != null) request.setParameter("elearning_manager", eLearningManager);
        if(eLearningTrack != null) request.setParameter("elearning_track", eLearningTrack);
        if(eLearningCurriculum != null) request.setParameter("elearning_curriculum", eLearningCurriculum);
        if(keepELearningActive != null) request.setParameter("keep_elearning_active", keepELearningActive);
		
        InputStream is = null;
        try {
            is = request.post();
            return consumeResponse(is);
        } finally {
			if (is != null) is.close();
		}
    }

    public String updateUser(String username, String password, String targetUsername, String targetCustomId, String newCustomId, String firstName, String lastName, String loginAccountType, String emailAddress, String isSAMLUser, String loginEnabled, String requiresToken, String teams, String roles, String isELearningManager, String eLearningManager, String eLearningTrack, String eLearningCurriculum, String keepELearningActive) throws Exception {
        URL url = new URL(UPDATE_USER_URI);
        ClientHttpRequest request = null;
		if (proxy != null) request = new ClientHttpRequest(url, proxy);
		else request = new ClientHttpRequest(url);
        setupAuthorization(request, username, password);
		
        if(targetUsername != null) request.setParameter("username", targetUsername);
        if(targetCustomId != null) request.setParameter("custom_id", targetCustomId);
        if(newCustomId != null) request.setParameter("new_custom_id", newCustomId);
        if(firstName != null) request.setParameter("first_name", firstName);
        if(lastName != null) request.setParameter("last_name", lastName);
        if(loginAccountType != null) request.setParameter("login_account_type", loginAccountType);
        if(emailAddress != null) request.setParameter("email_address", emailAddress);
        if(isSAMLUser != null) request.setParameter("is_saml_user", isSAMLUser);
        if(loginEnabled != null) request.setParameter("login_enabled", loginEnabled);
        if(requiresToken != null) request.setParameter("requires_token", requiresToken);
        if(teams != null) request.setParameter("teams", teams);
        if(roles != null) request.setParameter("roles", roles);
        if(isELearningManager != null) request.setParameter("is_elearning_manager", isELearningManager);
        if(eLearningManager != null) request.setParameter("elearning_manager", eLearningManager);
        if(eLearningTrack != null) request.setParameter("elearning_track", eLearningTrack);
        if(eLearningCurriculum != null) request.setParameter("elearning_curriculum", eLearningCurriculum);
        if(keepELearningActive != null) request.setParameter("keep_elearning_active", keepELearningActive);
		
		InputStream is = null;
        try {
            is = request.post();
            return consumeResponse(is);
        } finally {
			if (is != null) is.close();
		}
    }

    public String deleteUser(String username, String password, String targetUsername, String targetCustomId) throws Exception {
        URL url = new URL(DELETE_USER_URI);
        ClientHttpRequest request = null;
		if (proxy != null) request = new ClientHttpRequest(url, proxy);
		else request = new ClientHttpRequest(url);
        setupAuthorization(request, username, password);
		
        if(targetUsername != null) request.setParameter("username", targetUsername);
        if(targetCustomId != null) request.setParameter("custom_id", targetCustomId);
		
		InputStream is = null;
        try {
            is = request.post();
            return consumeResponse(is);
        } finally {
			if (is != null) is.close();
		}
    }

    public String getTeamList(String username, String password) throws Exception {
        URL url = new URL(GET_TEAM_LIST_URI);
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

    public String createTeam(String username, String password, String teamName, String members) throws Exception {
        URL url = new URL(CREATE_TEAM_URI);
        ClientHttpRequest request = null;
		if (proxy != null) request = new ClientHttpRequest(url, proxy);
		else request = new ClientHttpRequest(url);
        setupAuthorization(request, username, password);
		
        if(teamName != null) request.setParameter("team_name", teamName);
        if(members != null) request.setParameter("members", members);
		
		InputStream is = null;
        try {
            is = request.post();
            return consumeResponse(is);
        } finally {
			if (is != null) is.close();
		}
    }

    public String updateTeam(String username, String password, String teamName, String members) throws Exception {
        URL url = new URL(UPDATE_TEAM_URI);
        ClientHttpRequest request = null;
		if (proxy != null) request = new ClientHttpRequest(url, proxy);
		else request = new ClientHttpRequest(url);
        setupAuthorization(request, username, password);
		
        if(teamName != null) request.setParameter("team_name", teamName);
        if(members != null) request.setParameter("members", members);
		
		InputStream is = null;
        try {
            is = request.post();
            return consumeResponse(is);
        } finally {
			if (is != null) is.close();
		}
    }

    public String deleteTeam(String username, String password, String teamName) throws Exception {
        URL url = new URL(DELETE_TEAM_URI);
        ClientHttpRequest request = null;
		if (proxy != null) request = new ClientHttpRequest(url, proxy);
		else request = new ClientHttpRequest(url);
        setupAuthorization(request, username, password);
		
        if(teamName != null) request.setParameter("team_name", teamName);
		
		InputStream is = null;
        try {
            is = request.post();
            return consumeResponse(is);
        } finally {
			if (is != null) is.close();
		}
    }

    public String getCurriculumList(String username, String password) throws Exception {
        return getCurriculumList(username, password, null);
    }

    public String getCurriculumList(String username, String password, String eLearningTrack) throws Exception {
        URL url = new URL(GET_CURRICULUM_LIST_URI);
        ClientHttpRequest request = null;
		if (proxy != null) request = new ClientHttpRequest(url, proxy);
		else request = new ClientHttpRequest(url);
        setupAuthorization(request, username, password);
		
        if(eLearningTrack != null) request.setParameter("elearning_track", eLearningTrack);
		
		InputStream is = null;
        try {
            is = request.post();
            return consumeResponse(is);
        } finally {
			if (is != null) is.close();
		}
    }

    public String getTrackList(String username, String password) throws Exception {
		URL url = new URL(GET_TRACK_LIST_URI);
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
