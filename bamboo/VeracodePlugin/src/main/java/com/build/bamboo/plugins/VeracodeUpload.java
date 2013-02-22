package com.build.bamboo.plugins;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.atlassian.bamboo.task.TaskType;
import com.veracode.apiwrapper.UploadAPIWrapper;

public class VeracodeUpload implements TaskType
{
	UploadAPIWrapper uploadWrapper = null;
	private String api_username = null;
	private String api_password = null;
	private BuildLogger buildLogger;
	
    @NotNull
    @java.lang.Override
    public TaskResult execute(@NotNull final TaskContext taskContext) throws TaskException
    {
        buildLogger = taskContext.getBuildLogger();
        uploadWrapper = new UploadAPIWrapper();

        api_username = taskContext.getConfigurationMap().get("api_username");
        api_password = taskContext.getConfigurationMap().get("api_password");
        final String appId = taskContext.getConfigurationMap().get("app_id");
        final String appPlatform = taskContext.getConfigurationMap().get("app_platform");
        final String buildId = taskContext.getConfigurationMap().get("build_id");
        final String sourceFile = taskContext.getConfigurationMap().get("source_file");
        buildLogger.addBuildLogEntry(api_username);
        buildLogger.addBuildLogEntry(appId);
        buildLogger.addBuildLogEntry(appPlatform);
        buildLogger.addBuildLogEntry(buildId);

        try
        {
        	ArrayList<String> sourceFiles = getSourceFiles(sourceFile);
	        
	        createBuild(appId,buildId,appPlatform);
	        
	        for(String file : sourceFiles)
	        {
	        	uploadFile(appId, file);
	        }
	        
	        beginPrescan(appId);
	        
	        while(!isPrescanComplete(appId))
	        {
	            Thread.sleep(30000);
	        }
	        
	        beginScan(appId);
        }
        catch (Exception e)
        {
            buildLogger.addBuildLogEntry(e.getMessage());
            return TaskResultBuilder.create(taskContext).failed().build();
        }

        return TaskResultBuilder.create(taskContext).success().build();
    }
    
    private void createBuild(String appId, String version, String platform) throws Exception
    {
    	buildLogger.addBuildLogEntry("Calling create build:");
        String response = uploadWrapper.createBuild(api_username, api_password, appId, version, platform);
        buildLogger.addBuildLogEntry(response);
    }
    
    private void uploadFile(String appId, String filePath) throws Exception
    {
        buildLogger.addBuildLogEntry("Calling upload file:");
        String response = uploadWrapper.uploadFile(api_username, api_password, appId, filePath);
        buildLogger.addBuildLogEntry(response);
    }
    
    private void beginPrescan(String appId) throws Exception
    {
        buildLogger.addBuildLogEntry("Calling begin prescan:");
        String response = uploadWrapper.beginPrescan(api_username, api_password, appId);
        buildLogger.addBuildLogEntry(response);
    }
    
    private boolean isPrescanComplete(String appId) throws Exception
    {
    	buildLogger.addBuildLogEntry("Calling get build info:");
        String response = uploadWrapper.getBuildInfo(api_username, api_password, appId);
        buildLogger.addBuildLogEntry(response);
        Document doc = createDocument(response.getBytes());
        NodeList buildInfoNodes = getChildNodes(doc, doc, "buildinfo");
        if (buildInfoNodes.getLength() != 1) throw new Exception("No build info returned");
        Node buildInfoNode = buildInfoNodes.item(0);
        NodeList buildNodes = getChildNodes(doc, buildInfoNode, "build");
        if (buildNodes.getLength() != 1) throw new Exception("More than one build returned.");
        Node buildNode = buildNodes.item(0);
        NodeList analysisUnitNodes = getChildNodes(doc, buildNode, "analysis_unit");
        if (analysisUnitNodes.getLength() < 1) throw new Exception("No analysis units returned.");
        Node analysisUnitNode = null;
        for (int i = 0; i < analysisUnitNodes.getLength(); i++) {
          Node temp = analysisUnitNodes.item(i);
          Node analysisTypeNode = temp.getAttributes().getNamedItem("analysis_type");
          String analysisType = analysisTypeNode.getTextContent().trim();
          if ("Static".equals(analysisType)) {
            analysisUnitNode = temp;
          }
        }
        if (analysisUnitNode == null) throw new Exception("No static analysis units.");
        Node status = analysisUnitNode.getAttributes().getNamedItem("status");
        if ("Pre-Scan Success".equals(status.getTextContent().trim())) {
          return true;
        } else {
          return false;
        }
    }
    
    private void beginScan(String appId) throws Exception
    {
    	buildLogger.addBuildLogEntry("Calling begin scan:");
        String response = uploadWrapper.beginScan(api_username, api_password, appId);
        buildLogger.addBuildLogEntry(response);
    }
    
    private NodeList getChildNodes(Document doc, Node parent, String pattern) throws Exception
    {
        DocumentFragment docFrag = doc.createDocumentFragment();
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
          Node child = children.item(i);
          if (pattern != null && pattern.equals(child.getLocalName())) {
            docFrag.appendChild(child);
          }
        }
        return docFrag.getChildNodes();
    }
    
    private static Document createDocument(byte[] bytes) throws Exception
    {
        DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
        dFactory.setNamespaceAware(true);
        DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
        return dBuilder.parse(new ByteArrayInputStream(bytes));
    }
    
    private ArrayList<String> getSourceFiles(String sourcefile) throws Exception
    {
    	buildLogger.addBuildLogEntry("Retrieving list of files to upload...");
    	ArrayList<String> whichFiles = new ArrayList<String>();
    	FileInputStream fstream = new FileInputStream(sourcefile);

		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		while ((strLine = br.readLine()) != null)   {	 
			whichFiles.add(strLine);
		}
		in.close();
    	return whichFiles; 
    }
}