Veracode Jira Integration
=

These scripts will allow a user to create a Jira issue for Veracode flaws

Usage
=

Specify issues at command line:

    python createissues.py [JIRA_PROJECT,issueid1,issueid2,issueid3,...,n] [build_id]

Example:

    python createissues.py IDS,23,3,4,2 65504

For input from file:

    python createissues.py from [file_name] [build_id]

Example:

    python createissues.py from myinputfile 65504

Example of file for input:

    IDS,32,2,1,4
    OMC,32,1,5,3
