# \TrustedContentResolverAPI

All URIs are relative to *https://tcr.train.xfsc.dev/tcr/v1*

Method | HTTP request | Description
------------- | ------------- | -------------
[**ResolveTrustList**](TrustedContentResolverAPI.md#ResolveTrustList) | **Post** /resolve | Verification result based on issuer &amp; trust scheme pointers
[**ValidateTrustList**](TrustedContentResolverAPI.md#ValidateTrustList) | **Post** /validate | Validation result based on /resolve output



## ResolveTrustList

> ResolveResponse ResolveTrustList(ctx).ResolveRequest(resolveRequest).Execute()

Verification result based on issuer & trust scheme pointers



### Example

```go
package main

import (
    "context"
    "fmt"
    "os"
    openapiclient "github.com/eclipse-xfsc/train-trusted-content-resolver/clients/go/tcr"
)

func main() {
    resolveRequest := *openapiclient.NewResolveRequest("Issuer_example", []string{"TrustSchemePointers_example"}) // ResolveRequest | Verification params

    configuration := openapiclient.NewConfiguration()
    apiClient := openapiclient.NewAPIClient(configuration)
    resp, r, err := apiClient.TrustedContentResolverAPI.ResolveTrustList(context.Background()).ResolveRequest(resolveRequest).Execute()
    if err != nil {
        fmt.Fprintf(os.Stderr, "Error when calling `TrustedContentResolverAPI.ResolveTrustList``: %v\n", err)
        fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
    }
    // response from `ResolveTrustList`: ResolveResponse
    fmt.Fprintf(os.Stdout, "Response from `TrustedContentResolverAPI.ResolveTrustList`: %v\n", resp)
}
```

### Path Parameters



### Other Parameters

Other parameters are passed through a pointer to a apiResolveTrustListRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **resolveRequest** | [**ResolveRequest**](ResolveRequest.md) | Verification params | 

### Return type

[**ResolveResponse**](ResolveResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## ValidateTrustList

> ValidateResponse ValidateTrustList(ctx).ValidateRequest(validateRequest).Execute()

Validation result based on /resolve output



### Example

```go
package main

import (
    "context"
    "fmt"
    "os"
    openapiclient "github.com/eclipse-xfsc/train-trusted-content-resolver/clients/go/tcr"
)

func main() {
    validateRequest := *openapiclient.NewValidateRequest("Issuer_example", "Did_example", []string{"Endpoints_example"}) // ValidateRequest | Validation params

    configuration := openapiclient.NewConfiguration()
    apiClient := openapiclient.NewAPIClient(configuration)
    resp, r, err := apiClient.TrustedContentResolverAPI.ValidateTrustList(context.Background()).ValidateRequest(validateRequest).Execute()
    if err != nil {
        fmt.Fprintf(os.Stderr, "Error when calling `TrustedContentResolverAPI.ValidateTrustList``: %v\n", err)
        fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
    }
    // response from `ValidateTrustList`: ValidateResponse
    fmt.Fprintf(os.Stdout, "Response from `TrustedContentResolverAPI.ValidateTrustList`: %v\n", resp)
}
```

### Path Parameters



### Other Parameters

Other parameters are passed through a pointer to a apiValidateTrustListRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **validateRequest** | [**ValidateRequest**](ValidateRequest.md) | Validation params | 

### Return type

[**ValidateResponse**](ValidateResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)

