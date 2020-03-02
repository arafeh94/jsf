<h1>Ontology Based Recommender System Using Social Network Data</h1>
A framework to run sampling strategies with maximum scalability that can decrease the budget and time required for mining while producing identical results. 

<h2>Deployment</h2>
<h3>Prerequisites</h3>
<ul>
<li>Linux (Ubuntu)</li>
<li>Docker</li>
<li>Accessible release online (dropbox, ftp, drive), a copy is found in out/artifacts</li>
</ul>
<h3>Install</h3>
<ul>
<li>Clone or Source Code</li>
<li>run install.sh [release url]</li>
the script will download, and startup all the required container inside docker, setup the needed environment
and print out the required urls
</ul>
<h2>Step By Step Deployment for development</h2>
<h3>Prerequisites</h3>
<ul>
<li>jboss server, tested on version 10.1.0.Final </li>
<li>Mongo Server, version 3.4</li>
<li>Neo4j Server, version 3.5.3</li>
</ul>
<h3>Steps</h3>
<ul>
<li>run wildfly-10.1.0.Final\bin\add-user (.bat or .sh depending on the host) to create a new user
<li>run mongo and neo4j services in case they aren't running
<li>update passwords inside src\main\resources\conf\app.properties if needed
<li>on intellij, create a new run configuration under jboss server and specify its path.
<li>intellij should handle all the rest upon the first run. username and password should be the same used in the first step, The main url sould be http://localhost:8080/jsf/
</ul>

<h2>Documentation<h2>