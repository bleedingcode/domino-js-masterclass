#Tips and Tricks: Domino and JavaScript Development Master Class
Spend this session with some of the Domino community's application development experts to learn how they are approaching creating new Domino applications using the last frameworks and languages such as node.js and JavaScript. Learn their architectural approach, best practices and how best to get started.

##Setting the Scene
###Domino The Monolith
Standard Domino application architecture:

- Domino for directory management
- Domino TLS
- NoSQL data store (NSF)
- Domino API for workflow
- HTTP Server / port 1352
- nupdate  for indexing
- Domino for replication / clustering
- Standard templates for auditing (log.nsf etc)
- Domino SMTP for mail routing

Link to https://paulswithers.github.io/blog/2016/07/13/thoughts-on-domino

###But Actually...
Examples of how other things in real world sat around Domino:

- LDAP / nginx etc for directory management
- nginx / IBM HTTP Server for proxy, failover, SSL etc
- ODBC / JDBC / relational data sources / LEI / agents to use data from other in-house systems
- Web services to connect externally
- Lotus Workflow for workflow
- Third-party tools for more robust auditing
- Export to data warehouses for reporting
- Mail routing via non-Domino mail server

Links: https://frostillic.us/blog/posts/6AF303DE836BA02D85257D570058B1CA

###mutato nomine de tu fabula narratur
Domino apps have often sat around non-Domino systems to manage workflow

Microservices is nothing new, just the tools we use and the extent to which we break up the pieces

###Pros and Cons
- Greater flexibility
- Best of breed
- Increase standardisation

- Code for failure
- Stepping outside comfort zone
- Integration points

###REST Service Options
- DAS
- XAgent
- SmartNSF
- ODA Starter Servlet - tool = Eclipse, point to OpenNTF blog post
- Darwino Microservices

###HTTP Response Codes
- What they are?
- When to use which?
- Properly handle HTTP 500

###Data Integrity
Code for failure and "bad data" on both sides. This is why DAS is not recommended - no validation / conversion

DO YOU WANT TO EXPOSE EDITABILITY OF YOUR "STATUS" FIELD? WOULD YOU MAKE IT EDITABLE ON A FORM?

###Swagger
- Provides a contract
- Swagger Editor to design, describe and document contract as JSON
- Swagger UI allows interaction
- Documentation NEEDS examples: parameter is integer, I'm passing 366, why is it breaking on both sides? Because it takes an integer where 0 means X, 1 means Y, 2 means Z and nothing else is expected

###Testing
- Postman (Electron) desktop app
- RESTClient plugin for Eclipse
- Other REST clients

###Background "Agents"
Xots - demo

###Scheduling - Node-RED
Do we know of any other potential schedulers to suggest?

- Docker install
- On premises node.js install
- IBM Cloud

### Node-RED Overview
- Flows
- Inject node
- Basic authentication (or John's node)
- Flow stored as JSON
- Big Timer, if required

### Domino on Docker
- Supported since FP10
- Allows containerisation (easy setup / deploy / move / feature-pack testing)
- **_May_** allow separation of services in the future - reboot JUST http, indexing in separate container etc.
