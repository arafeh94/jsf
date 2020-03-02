#!/bin/bash
function info() {
    echo -e "************************************************************\n\033[1;33m${1}\033[m\n************************************************************"
}

if [[ -z "$1" ]]
then
    echo ERROR: Script requires one parameter which is the jsf web url
    exit 1
fi

info 'cleaning'
docker stop neo4j
docker stop mongo
docker stop wf
docker rm neo4j
docker rm mongo
docker rm wf

info 'running neo4j'
docker run -d --network=host --name neo4j -p=7474:7474 -p=7687:7687 neo4j

info 'running mongo'
docker run -d --network=host --name mongo -p 27017-27019:27017-27019 mongo:4.0.4

info 'running wildfly server'
docker run -d --network=host --name wf -p 8080:8080 -p 9990:9990 -it jboss/wildfly /opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0

info 'setting server credentials'
docker exec -it wf bash ./wildfly/bin/add-user.sh --silent=true admin admin

info 'downloading latest updates'
docker exec -it wf curl "$1" -L -o "jsf archive.war"

info 'deploying website'
docker exec -it wf bash ./wildfly/bin/jboss-cli.sh  --connect  --command="deploy jsf\ archive.war"

info 'waiting for neo4j to start'
sleep 5
info 'setting neo4j credentials'
docker exec -it neo4j bash ./bin/cypher-shell -u neo4j -p neo4j 'CALL dbms.security.changePassword("admin")'



info 'website-url: http://localhost:8080/jsf/index.xhtml'
info 'neo4j-url: http://localhost:7474/browser/'
info 'neo4j-uri: bolt://localhost:7687'
info 'neo4j-credentials: neo4j - admin'
info 'mongo-uri: localhost:27017'
info 'server-management: http://localhost:9990'
info 'server-credentials: admin-admin'
info 'view logs: docker exec -it wf cat Documents/socins/logp/socins.log'


