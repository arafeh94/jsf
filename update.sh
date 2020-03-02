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
docker stop wf
docker rm wf

info 'running wildfly server'
docker run -d --network=host --name wf -p 8080:8080 -p 9990:9990 -it jboss/wildfly /opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0

info 'setting server credentials'
docker exec -it wf bash ./wildfly/bin/add-user.sh --silent=true admin admin

info 'downloading latest updates'
docker exec -it wf curl "$1" -L -o "jsf archive.war"

info 'deploying website'
docker exec -it wf bash ./wildfly/bin/jboss-cli.sh  --connect  --command="deploy jsf\ archive.war"



info 'website-url: http://localhost:8080/jsf/index.xhtml'
info 'neo4j-url: http://localhost:7474/browser/'
info 'neo4j-uri: bolt://localhost:7687'
info 'neo4j-credentials: neo4j - admin'
info 'mongo-uri: localhost:27017'
info 'server-management: http://localhost:9990'
info 'server-credentials: admin-admin'
info 'view logs: docker exec -it wf cat Documents/socins/logp/socins.log'


