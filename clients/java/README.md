You should have parent TCR project copied from GitLab.

# How to build java client

```bash
cd clients/java
mvn clean install
```

this command will build two java libraries:
- the small java lib with TCR client classes (no TCR API classes): `trusted-content-resolver-java-client-1.1.0-SNAPSHOT.jar` (11KB)
- the uber jar with all dependencies packaged inside: `trusted-content-resolver-java-client-1.1.0-SNAPSHOT-full.jar` (36,1MB)

to use client from another application you should add a dependency

```
        <dependency>
            <groupId>eu.xfsc.train</groupId>
            <artifactId>trusted-content-resolver-java-client</artifactId>
            <version>1.1.0-SNAPSHOT</version>
        </dependency>
```
, then use `ResolveServiceClient` class to access TCR REST API. 
      
# How to run java client

```bash

$ cd target/
$ java -jar trusted-content-resolver-java-client-1.0.0-SNAPSHOT-full.jar <client arguments>
```

client arguments can be passed as set of key/value pairs: `k1=p1 k2=p2`. Supported parameters are: 

- `uri/u` - uri to Trusted Content Resolver (TCR) service
- `endpoint/e` - TCR method to be invoked (resolve/validate)
- `data/d` - parameters for invoked TCR method in JSON format
    - ResolveRequest: `{"issuer": "issuer1", "trustSchemePointers": ["ptr1", "ptr2"], "endpointTypes": ["eptype1", "eptype2"]}`
    - ValidateRequest: `{"issuer": "issuer1", "did": "did1", "endpoints": ["endpoint1", "endpoint2"]}`
- `file/f` - file, containing parameters specified above, also in JSON format
- parameters specified via command-line arguments overwrite parameters from file

client prints invocation results to console
