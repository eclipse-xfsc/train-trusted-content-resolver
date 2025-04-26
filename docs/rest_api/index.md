## Trusted Content Resolver REST API

The current version of TCR service is published at `/tcr/v1` path. It exposes two endpoints for clients:

- /resolve - allows resolution of the provided TrustFramework pointers to corresponding DIDs, VCs and TrustLists to find the issuer details in the trust list. The resolution process is controllable by given list of endpoint types which are considered during the resolving.  Also verifies resolved DID Documents against their well-known configuration and VC signatures. 
- /validate - performs validation of the provided DID to its Document and well-known DID configuration, then verifies provided Trust List endpoints and extracts issuer details from the resolved Trust Lists.

The service contract is specified in [OpenAPI document](../../openapi/tcr_openapi.yaml).

The TCR API Request/Response Model is:

![TCR Request/Response Model](./images/rest_api.drawio.png "TCR Request/Response Model")

Here the `ResolvedDoc.document` attribute presents the [DIDDocument](https://www.w3.org/TR/did-core/#did-documents) resolved from DID, containing all DIDDocument standard attributes. In the same way, the `ResolvedTrustList.trustList` attribute contains [TrustServiceProvider](https://www.digicert.com/faq/signature-trust/what-is-a-trust-services-provider) structure resolved for the provided `issuer`.

All API structures and their attributes are explained in the [OpenAPI contract](../../openapi/tcr_openapi.yaml) in details.