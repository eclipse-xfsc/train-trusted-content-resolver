# TCR Python client


The client source code generated from TCR service OpenApi specification with help of `/openapi-genarator-maven-plugin`. To tune Python generation please see the plugin
configuration details:
- [https://github.com/OpenAPITools/openapi-generator](https://github.com/OpenAPITools/openapi-generator)
- [https://github.com/OpenAPITools/openapi-generator/blob/master/docs/generators/python.md](https://github.com/OpenAPITools/openapi-generator/blob/master/docs/generators/python.md)

# Generate the code

```bash
cd <Repository root>/trusted-content-resolver
mvn clean install
```
or

```bash
cd <Repository root>/trusted-content-resolver/clients/py
mvn clean package
```

# Install and use client

```bash
cd <Repository root>/trusted-content-resolver/clients/py
pip install src/generated
pip install src/cmd

> eu-xfsc-train-tcr --help
Usage: eu-xfsc-train-tcr [OPTIONS] COMMAND [ARGS]...

Options:
  --help  Show this message and exit.

Commands:
  resolve   Example of query
  validate  Example of query

# use path to cli in CI if needed
> which  eu-xfsc-train-tcr
/Users/XXX/.python.d/dev/eclipse/xfsc/dev-ops/testing/bdd-executor/train/bin/eu-xfsc-train-tcr
```
