package com.build.bamboo.plugins;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.application.jira.JiraApplicationType;
import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.build.test.TestCollationService;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.atlassian.bamboo.task.TaskType;
import com.atlassian.sal.api.net.Request.MethodType;
import com.atlassian.sal.api.net.ResponseException;
import com.veracode.apiwrapper.ResultsAPIWrapper;
import com.veracode.apiwrapper.UploadAPIWrapper;


public class VeracodeResults implements TaskType
{
	ResultsAPIWrapper resultsWrapper = null;
	UploadAPIWrapper uploadWrapper = null;
	private String api_username = null;
	private String api_password = null;
	private String jira_username = null;
	private String jira_password = null;
	private String appId = null;
	private String buildId = null;
	private BuildLogger buildLogger;
	
	private final TestCollationService testCollationService;
	private final ApplicationLinkService applicationLinkService;
	
	public VeracodeResults(TestCollationService testCollationService, ApplicationLinkService applicationLinkService)
	{
		this.testCollationService = testCollationService;
		this.applicationLinkService = applicationLinkService; 
	}
	
    @NotNull
    @java.lang.Override
    public TaskResult execute(@NotNull final TaskContext taskContext) throws TaskException
    {
    	String buildDir = taskContext.getRootDirectory().getAbsolutePath();
 
        buildLogger = taskContext.getBuildLogger();
        resultsWrapper = new ResultsAPIWrapper();
        uploadWrapper = new UploadAPIWrapper();
        
        api_username = taskContext.getConfigurationMap().get("api_username");
        api_password = taskContext.getConfigurationMap().get("api_password");
        jira_username = taskContext.getConfigurationMap().get("api_username");
        jira_password = taskContext.getConfigurationMap().get("api_password");
        appId = taskContext.getConfigurationMap().get("app_id");
        buildLogger.addBuildLogEntry(api_username);
        buildLogger.addBuildLogEntry(appId);

        try
        {
            buildId = getBuildId();
        	String results = getResults();
        	saveResults(results,buildDir);
        	outputToJunit(results,buildDir);
        }
        catch (Exception e)
        {
            buildLogger.addBuildLogEntry(e.getMessage());
            return TaskResultBuilder.create(taskContext).failed().build();
        }

        //parse test results
        TaskResultBuilder taskResultBuilder = TaskResultBuilder.create(taskContext);
    	final String testFilePattern = "**/test-reports/veracode_results.xml";
   		testCollationService.collateTestResults(taskContext, testFilePattern);
   		buildLogger.addBuildLogEntry("App links = ");
   		//Iterable<ApplicationLink> links = jiraApplinksService.getJiraApplicationLinks();
   		ApplicationLink appLink = applicationLinkService.getPrimaryApplicationLink(JiraApplicationType.class);
   		
   		String url = null;
   		ApplicationLink theLink = null;
        ApplicationLinkRequestFactory factory = appLink.createAuthenticatedRequestFactory();
        
        String response = "initial value";
        try {
        	url = appLink.getRpcUrl().toString();
			response = factory.createRequest(MethodType.GET,"/rest/api/latest/issue/createmeta").addBasicAuthentication(jira_username,jira_password).execute();
			//response = factory.createRequest(MethodType.GET,"/rest/api/latest/search?jql=labels in ('veracode')").addBasicAuthentication("casey.harford","Coolcool8").execute();

		} catch (ResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CredentialsRequiredException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   		//JiraData data = null;
   		//String jql = "labels%20in%20('veracode')";
        buildLogger.addBuildLogEntry("Response = " + response);
   		
   		//this stuff does not work.
//		try 
//		{
//			JiraRestResponse response = issueCreator.go(theLink,jql);
//			//data = new Gson().fromJson(response.body, JiraData.class);
//			if (response != null) {
//			buildLogger.addBuildLogEntry("Response = " + response.toString());
//			}
//			else {
//				buildLogger.addBuildLogEntry("Response = null");
//			}
//			
//		} 
//		catch (Exception e1) 
//		{
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
   		buildLogger.addBuildLogEntry("//end links");
        return taskResultBuilder.checkTestFailures().build();
    }
    
    /**
     * Using the uploadWrapper, retrieves latest build info, and parses the build_id
     * @return String buildId
     * @throws Exception
     */
    private String getBuildId() throws Exception
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
        return getAttribute(buildNodes.item(0),"build_id");
    }
    
    /**
     * Gets results using resultsWrapper
     * @return String response, the results of the request, in XML format
     * @throws Exception
     */
    private String getResults() throws Exception
    {
    	buildLogger.addBuildLogEntry("Calling get results...");
    	String response = resultsWrapper.detailedReport(api_username, api_password, buildId);
    	buildLogger.addBuildLogEntry(response);
    	return response;
    }
    
    /**
     * Save results to a file for artifact in Bamboo
     * @param results
     * @param buildDir
     * @throws IOException 
     */
    private void saveResults(String results, String buildDir) throws IOException
    {
    	//save results for artifact
    	final File file = new File(buildDir + "/scanresults.xml");
        final File parentDirectory = file.getParentFile();
        if(parentDirectory != null)
        {
        	parentDirectory.mkdirs();
        }
        
        PrintWriter out = new PrintWriter(new FileWriter(file));
        out.print(results);
        out.close();
    }
    
    /**
     * Parses the xml, and outputs to JUnit format. Each test case equals a flaw.
     * @param xmlResponse
     * @param buildDir
     * @throws Exception
     */
    private void outputToJunit(String xmlResponse, String buildDir) throws Exception
    {
    	Document doc = createDocument(xmlResponse.getBytes());
        NodeList detailedReportNodes = getChildNodes(doc, doc, "detailedreport");
        if (detailedReportNodes.getLength() != 1) throw new Exception("No results info returned.");
        Node detailedReportNode = detailedReportNodes.item(0);
        NodeList severityNodes = getChildNodes(doc,detailedReportNode,"severity");
        if(severityNodes.getLength() == 0) throw new Exception("No severity nodes found.");
        
        final File file = new File(buildDir + "/test-reports/veracode_results.xml");
        final File parentDirectory = file.getParentFile();
        
        if(parentDirectory != null)
        {
        	parentDirectory.mkdirs();
        }
        
        PrintWriter out = new PrintWriter(new FileWriter(file)); 
    	out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
    	out.println("<testsuite errors=\"0\" tests=\"5\" time=\"0\" failures=\"0\" name=\"com.build.veracode.ResultsAPI\">");
    	
        for(int i=0; i<severityNodes.getLength(); i++)
        {
        	NodeList categoryNodes = getChildNodes(doc,severityNodes.item(i),"category");
        	if(categoryNodes.getLength() > 0)
        	{
        		for(int j=0; j<categoryNodes.getLength(); j++)
        		{
        			NodeList cweNodes = getChildNodes(doc,categoryNodes.item(j),"cwe");
        			if(cweNodes.getLength() > 0)
        			{
        				for(int k=0; k<cweNodes.getLength(); k++)
        				{
        					NodeList staticFlaws = getChildNodes(doc,cweNodes.item(k),"staticflaws");
        					for(int l=0; l<staticFlaws.getLength(); l++)
        					{
        						NodeList flawNodes = getChildNodes(doc,staticFlaws.item(l),"flaw");
        						for(int m=0; m<flawNodes.getLength(); m++)
        						{
        							int mitigations = getChildNodes(doc,flawNodes.item(m),"mitigations").getLength();
        							buildLogger.addBuildLogEntry("mitigations = " + mitigations);
        							String remediation_status = getAttribute(flawNodes.item(m),"remediation_status");
        							if(mitigations == 0 && (remediation_status.equalsIgnoreCase("Open") || remediation_status.equalsIgnoreCase("New") || remediation_status.equalsIgnoreCase("Re-Open")))
        							{
        								//get attributes
        								String issueId = getAttribute(flawNodes.item(m),"issueid");
	        							String categoryName = getAttribute(flawNodes.item(m),"categoryname");
	        							String severity = getAttribute(flawNodes.item(m),"severity");
	        							String module = getAttribute(flawNodes.item(m),"module");
	        							String description = getAttribute(flawNodes.item(m),"description");
	        							String sourceFile = getAttribute(flawNodes.item(m),"sourcefile");
	        							String sourcePath = getAttribute(flawNodes.item(m),"sourcefilepath");
	        							String line = getAttribute(flawNodes.item(m),"line");
	        							String type = getAttribute(flawNodes.item(m),"type");
	        							
	        							//output test case
	        							outputTestCase(issueId, categoryName, severity, module, description, sourceFile, sourcePath, line, type, out);
        							}
        						}
        					}
        				}
        			}
        		}
        	}
        }
        out.println("</testsuite>");
        out.close();
        buildLogger.addBuildLogEntry("# of severity nodes returned = " + severityNodes.getLength());
    }
    
    /**
     * 
     * @param flawId
     * @return
     * @throws Exception
     */
    private String getCallStacks(String flawId) throws Exception
    {
    	buildLogger.addBuildLogEntry("Calling get call stacks...");
    	String response = uploadWrapper.getCallStacks(api_username, api_password, buildId, flawId);
    	buildLogger.addBuildLogEntry(response);
    	return response;
    }
    
    private void outputTestCase(String issueId, String categoryName, String severity, 
    							String module, String description, String sourceFile, 
    							String sourcePath, String line, String type, PrintWriter out) throws Exception
    {
		out.println("<testcase time=\"0\" name=\"Severity: " + severity + ", "+categoryName+", Veracode ID: "+issueId+"\">");
		out.println("<failure type=\""+categoryName+"\">");
		out.println(" - Description \n \n" + WordUtils.wrap(description,80) + "\n");
		out.println(" - Module: " + module);
		out.println(" - Line #: " + line);
		out.println(" - Severity: " + severity);
		out.println(" - Type: " + type);
		out.println(" - File: " + sourcePath + sourceFile);
		out.println(" - Veracode id: " + issueId);	        							
		out.println(" - Call Stack: ");        								
    	outputCallStacks(issueId,out);
		out.println("</failure>");
		out.println("</testcase>");
    }
    
	private void outputCallStacks(String issueId, PrintWriter out) throws Exception
	{
		Document docStack = createDocument(getCallStacks(issueId).getBytes());
		NodeList callStackNodes = getChildNodes(docStack, docStack, "callstacks");
		if(callStackNodes.getLength() > 0)
		{
			Node callStackNode = callStackNodes.item(0);
			NodeList callStackChildNodes = getChildNodes(docStack,callStackNode,"callstack");
			if(callStackChildNodes.getLength() > 0)
			{
				for(int n = 0; n < callStackChildNodes.getLength(); n++)
				{
					Node callStack = callStackChildNodes.item(n);
		    		NodeList callNodes = getChildNodes(docStack,callStack,"call");
		    		if(callNodes.getLength() > 0)
		    		{
		    			for(int p = 0; p < callNodes.getLength(); p++)
		    			{
		    				String dataPath = getAttribute(callNodes.item(p),"data_path");
		    				String filePath = getAttribute(callNodes.item(p),"file_path");
		    				String functionName = getAttribute(callNodes.item(p),"function_name").replace("<","&lt;").replace(">","&gt;");
		    				String lineNumber = getAttribute(callNodes.item(p),"line_number");
		    				out.println("   " + dataPath 
		    									+ "; file_path= " 
		    									+ filePath + "; function_name= " 
		    									+ functionName + "; line_number= " 
		    									+ lineNumber);
		    			}
		    		}
				}
			}
		}
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
    
    private String getAttribute(Node node, String name)
    {
    	return node.getAttributes().getNamedItem(name).getTextContent().trim();
    }
}