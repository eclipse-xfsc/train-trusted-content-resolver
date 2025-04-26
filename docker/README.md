## Build Procedure

Ensure you have JDK 21, Maven 3.5.4 (or newer) and Git installed

First clone the TRAIN Trusted Content Resolver (TCR) repository:

```bash
git clone --recurse-submodules git@github.com/eclipse-xfsc/train-trusted-content-resolver.git
```

Then build project with `maven`:

```bash
cd <tcr_root_folder>
mvn clean install 
```

## Run TCR in docker with external universal-resolver
Go to `/docker` folder, use docker compose to start TCR
 
```bash
>cd docker
>docker-compose up -d
```

In this (default) TCR setup we use external universal-resolver, configured as `TCR_DID_BASE_URI: https://dev.uniresolver.io/1.0` in `docker-compose` file. TCR starts on port `8887` its health can be tested at [http://localhost:8887/actuator/health](http://localhost:8887/actuator/health) page.

## Run TCR in docker with local universal-resolver
Go to `/docker` folder. Use docker compose to start universal-resolver with did:web driver only:

```bash
>cd docker
>docker-compose --env-file unires.env -f uni-resolver-web.yml up -d
```

or with all available universal-resolver drivers: 

```bash
docker-compose --env-file unires.env -f uni-resolver-all.yml up -d
```

To test did:web resolution you can try the following CURL commands:

```bash
curl -X GET http://localhost:8080/1.0/identifiers/did:web:did.actor:alice
curl -X GET http://localhost:8080/1.0/identifiers/did:web:did.actor:bob
curl -X GET http://localhost:8080/1.0/identifiers/did:web:did.actor:mike
```

For more information on universal-resolver please see the project on [GitHub](https://github.com/decentralized-identity/universal-resolver)


## Known issues
when TCR configured to use particular DNS servers it can fail on start on Mac platform, see [https://github.com/dnsjava/dnsjava/issues/260](https://github.com/dnsjava/dnsjava/issues/260) for more details. To prevent this timeout issue we can force docker to start TCR with Linux platform: 

```bash
DOCKER_DEFAULT_PLATFORM=linux/amd64 docker-compose up
```

