import urllib, urllib2, cookielib, json, getpass, base64, sys
from lxml import etree

# globals
jira_serverurl = raw_input("Jira Server URL: ") 
jira_userid = raw_input("Jira Username: ") 
build_id = ""
jira_password = getpass.getpass()
severitylevels = {"1":"Minor","2":"Minor","3":"Critical","4":"Blocker","5":"Blocker"}
jira_tickets = []

veracode_user = raw_input("veracode_username:")
veracode_pw = getpass.getpass() 

# setup cookiejar for handling URLS, this part will eventually be handled with OAuth
cookiejar = cookielib.CookieJar()
myopener = urllib2.build_opener(urllib2.HTTPCookieProcessor(cookiejar))

# FUNCTIONS HERE

# get flaw info from scan results file, generate data, and create the issue
def getFlawInfo(issueids):
  theflaws = {}
  jira_project = issueids[0]
  scanresults_xml = './scanresults.xml'
  source_xml = open(scanresults_xml,"r")
  results = etree.parse(source_xml)

  #there's probably a better way to do this part....
  for child in results.getroot().getchildren():
    if 'severity' in child.tag:
      for category in child.getchildren():
        for cwe in category.getchildren():
          if 'cwe' in cwe.tag:
            for staticflaws in cwe.getchildren():
              if 'staticflaws' in staticflaws.tag:
                for flaw in staticflaws.getchildren():
                  if flaw.attrib['issueid'] in issueids:
                    issueid = flaw.attrib['issueid']
                    categoryname = flaw.attrib['categoryname']
                    sourcefile = flaw.attrib['sourcefile']
                    sourcefilepath = flaw.attrib['sourcefilepath'] 
                    line = flaw.attrib['line']
                    severity = flaw.attrib['severity']
                    flawtype = flaw.attrib['type']
                    flawdescription = flaw.attrib['description']
                    module = flaw.attrib['module']
                    print "generating data for flaw " + issueid + ", file: " + sourcefile + ", module: " + module
                    theflaws = generateData(categoryname,issueid,line,sourcefile,sourcefilepath,severity,flawtype,flawdescription,module,theflaws)
  print "finished generating data, creating issue..."
  createIssue(jira_project,sourcefile,severity,theflaws)
  
# generate the data
def generateData(categoryname,issueid,line,sourcefile,sourcefilepath,severity,flawtype,flawdescription,module,theflaws):
  callstackdata = ""
  callstacks_xml = getCallStacks(build_id,issueid)
  callstacks = etree.fromstring(callstacks_xml)
  for callstack in callstacks.getchildren():
    if callstack.attrib['module_name'] == module:
      for call in callstack[1:]:
        callstackdata = callstackdata + call.attrib['data_path'] + ": file_path= " + call.attrib['file_path'] \
          + ", function_name= " + call.attrib['function_name'] + ", line_number= " + call.attrib['line_number'] + "\n"

  description = "h4. " + categoryname \
    + "\n ||Line #||Severity||Type||File||Veracode ID||Module" \
    + "\n ||" \
    + "\n |"+line+"|"+severity+"|"+flawtype+"|"+sourcefilepath+sourcefile+"|"+issueid+"|"+module+"|" \
    + "\n " + flawdescription \
    + "\n\nCall stack: \\\\ \\\\ {code}"+callstackdata+"{code} \n----"
  theflaws[issueid] = {}
  theflaws[issueid]['description'] = description + "\n"
  return theflaws

# simple wrapper function to encode the username & pass
def encodeUserData(user, password):
    return "Basic " + (user + ":" + password).encode("base64").rstrip()

# returns a call stack for given build/flaw id
def getCallStacks(build_id,flaw_id):
  # login is restricted by ip
  u=veracode_user
  p=veracode_pw
  url='https://analysiscenter.veracode.com/api/2.0/getcallstacks.do'

  # create the request object and set some headers
  req = urllib2.Request(url)
  req.add_header('Accept', 'application/json')
  req.add_header("Content-type", "application/x-www-form-urlencoded")
  req.add_header('Authorization', encodeUserData(u, p))
  data = {"build_id":build_id,"flaw_id":flaw_id}

  # make the request and return the results
  res = urllib2.urlopen(req,urllib.urlencode(data))
  return res.read()
  
# creates an issue in jira
def createIssue(jira_project,sourcefile,severity,theflaws):

  # one description for entire issue, but broken up by each flaw
  description = ""
  for flaw in theflaws:
    description = description + theflaws[flaw]['description']

  # prepare json data
  summary = "Veracode found security flaws in file: " + sourcefile
  data = {"fields":{}}
  data['fields']['project'] = {"key":jira_project}
  data['fields']['summary'] = summary 
  data['fields']['description'] = description
  data['fields']['issuetype'] = {"name":"Bug"}
  data['fields']['customfield_10462'] = [{"value":"Customers"}]
  data['fields']['components'] = [{"name":"Java"}]
  data['fields']['labels'] = ["veracode","security"]
  #data['fields']['priority'] = {"name":"Blocker"}
  data['fields']['priority'] = {"name":severitylevels[severity]}
  print json.dumps(data)
  # create an issue
  queryurl = jira_serverurl + "/rest/api/latest/issue"
  req = urllib2.Request(queryurl)
  req.add_data(json.dumps(data))
  req.add_header("Content-type","application/json")
  req.add_header("Accept","application/json")
  fp = myopener.open(req)
  issueCreated = json.loads(fp.read())['key']
  print jira_serverurl + "/browse/" + issueCreated
  jira_tickets.append(jira_serverurl + "/browse/" + issueCreated)
  fp.close()

# MAIN EXECUTE HERE

# authenticate
creds = { "username" : jira_userid, "password" : jira_password }
queryurl = jira_serverurl + "/rest/auth/latest/session"
req = urllib2.Request(queryurl)
req.add_data(json.dumps(creds))
req.add_header("Content-type","application/json")
req.add_header("Accept","application/json")
fp = myopener.open(req)
fp.close()

#either use 2nd command line argument as issues, or use source file
fileissues = []
if sys.argv[1] == "from":
  f = open(sys.argv[2],"r")
  for line in f.readlines():
    fileissues.append(line)
  build_id = sys.argv[3]
else:
  fileissues.append(sys.argv[1])
  build_id = sys.argv[2]

#iterate through sets of issues, per file, creating a ticket for each file
for fileissue in fileissues:
  getFlawInfo(fileissue.strip().replace(" ","").split(","))

print "the following jira tickets were created:"
for ticket in jira_tickets:
  print ticket
