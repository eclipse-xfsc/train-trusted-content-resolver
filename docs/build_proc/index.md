## Build Procedures

Ensure you have JDK 21 (or newer), Maven 3.5.4 (or newer) and Git installed

First clone the TCR repository:

```bash
git clone --recurse-submodules https://github.com/eclipse-xfsc/train-trusted-content-resolver.git
```

Or, if you already cloned this repo and still need the subrepository run `git submodule update --init`

Then go to the project folder and build it with maven:

```bash
mvn clean install
```

This will build all modules and run TCR testsuite.

Now we can run TCR locally. Go to `/service` folder and start it with maven:

```bash
cd service
mvn spring-boot:run
```
 
This command will start TCR service on your localhost at port `8087`, to check service status open its health page in browser at `http://localhost:8087/actuator/health`'.
 
We can also run the TCR Service with docker: go to `/docker` folder and follow instructions in the [readme](../../docker).
