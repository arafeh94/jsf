#!/bin/bash
function info() {
    echo -e "************************************************************\n\033[1;33m${1}\033[m\n************************************************************"
}


info 'cleaning'
docker stop neo4j
docker stop mongo
docker stop wf

info 'running neo4j'
docker start neo4j

info 'running mongo'
docker start mongo

info 'running wildfly server'
docker start wf

info 'website-url: http://localhost:8080/jsf/index.xhtml'
info 'neo4j-url: http://localhost:7474/browser/'
info 'neo4j-uri: bolt://localhost:7687'
info 'neo4j-credentials: neo4j - admin'
info 'mongo-uri: localhost:27017'
info 'server-management: http://localhost:9990'
info 'server-credentials: admin-admin'
info 'view logs: docker exec -it wf cat Documents/socins/logp/socins.log'


