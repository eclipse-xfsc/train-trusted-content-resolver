#!/usr/bin/env node

"use strict";

const { Command } = require('commander');
const jsClient = require("trusted-content-resolver-js-client");
const program = new Command();

function multipleArg(value, previous) {
    return (previous || []).concat([value]);
}

function requestCallback(error, data, _response) {
    if (error) {
        console.error(error);
    } else {
        console.log(JSON.stringify(data));
    }
}

function api(uri = null) {
    const apiClient = uri ? new jsClient.ApiClient(uri): new jsClient.ApiClient()
    return new jsClient.TrustedContentResolverApi(apiClient)
}


program.command('resolve')
    .requiredOption('--issuer <string>')
    .requiredOption('--trust-scheme-pointer <value>', 'repeatable value', multipleArg)
    .option('--endpoint-type <value>', 'repeatable value', [])
    .option('--uri <sting>')
    .action((options) => {
        const resolveRequest = jsClient.ResolveRequest.constructFromObject(
            {
                issuer: options.issuer,
                trustSchemePointers:  options.trustSchemePointer,
                endpointTypes: options.endpointType
            }
        );

        api(options.uri).resolveTrustList(resolveRequest, requestCallback);
    });


program.command('validate')
    .requiredOption('--issuer <string>')
    .requiredOption('--did <string>')
    .option('--endpoint <value>', 'repeatable value', multipleArg)
    .option('--uri <sting>')
    .action((options) => {
        const validateRequest = jsClient.ValidateRequest.constructFromObject(
            {
                issuer: options.issuer,
                did:  options.did,
                endpoints: options.endpoint
            }
        );

        api(options.uri).validateTrustList(validateRequest, requestCallback);
    });

program.parse();
