# TCR JavaScript client


The client source code generated from TCR service OpenApi specification with help of `/openapi-genarator-maven-plugin`. To tune JS generation please see the plugin
configuration details:
- [https://github.com/OpenAPITools/openapi-generator](https://github.com/OpenAPITools/openapi-generator)
- [https://github.com/OpenAPITools/openapi-generator/blob/master/docs/generators/javascript.md](https://github.com/OpenAPITools/openapi-generator/blob/master/docs/generators/javascript.md)

# Generate the code

```bash
cd <Repository root>/trusted-content-resolver
mvn clean install
```
or 

```bash
cd <Repository root>/trusted-content-resolver/clients/js
mvn clean package
```

# Install and use client

```bash

# step [1]
cd <Repository root>/trusted-content-resolver/clients/js/src/generated/

# step [2]
# do to some unsolved bug the code generation is little broken
# it is fixed with some small text replace 
# where \x27 is single quote '
sed --in-place=.bkp 's|babel src -d dist|babel tcr -d dist|g' ./package.json
sed --in-place=.bkp 's|tcr-address|\x27tcr-address\x27|g' ./tcr/ApiClient.js

# step [3]
npm install

# step [4]
cd ../cmd
npm install

> ./cli.js --help
Usage: cli [options] [command]

Options:
  -h, --help          display help for command

Commands:
  resolve [options]
  validate [options]
  help [command]      display help for command

# use path to cli in CI if needed
<Repository root>/trusted-content-resolver/clients/js/src/cmd/cli.js
```


