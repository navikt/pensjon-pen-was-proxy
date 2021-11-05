FROM jboss/wildfly:24.0.0.Final

RUN /opt/jboss/wildfly/bin/add-user.sh admin admin

COPY standalone.xml /opt/jboss/wildfly/standalone/configuration/standalone.xml

COPY pensjon-pen-was-proxy-jee/target/pensjon-pen-was-proxy-jee-*.ear /opt/jboss/wildfly/standalone/deployments/pensjon-pen-was-proxy-jee.ear

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
