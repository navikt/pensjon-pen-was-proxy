Applikasjon som videresender all HTTP spørringer mottatt som pensjon-pen på IBM Websphere
til pensjon-pen på nais.

Bygges på Jenkins internt for deploy.

# Utvikling og test

Modulen `pensjon-pen-was-proxy-pen-mock` er en enkel mock som kan bruker sammen med `Dockerfile` for
å teste proxy-funksjonaliteten.

Fra terminal

Bygg og start pensjon-pen-mock
```
cd pensjon-pen-was-proxy-pen-mock
yarn install
node index.xml
```

I et annet  terminalvindu

```
mvn clean package && docker build -t pensjon-pen-was-proxy . && docker run -p 9080:9080 -p 9990:9990 pensjon-pen-was-proxy
```

Du kan nå nå selftest på http://localhost:9080/pensjon-ws/internal/selftest og se at videresendingen fungerer ved å gå til http://localhost:9080/pensjon-ws/api/ping.

# Henvendelser

Spørsmål knyttet til koden eller prosjektet kan stilles som issues her på GitHub.

### For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen `#pensjon-teknisk`.
