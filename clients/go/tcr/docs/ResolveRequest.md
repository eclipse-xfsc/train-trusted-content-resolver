# ResolveRequest

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Issuer** | **string** | Issuer details from the VC/VP (e.g., DID/URI) | 
**TrustSchemePointers** | **[]string** | Trust Framework Pointers (e.g., example.federation1.de) | 
**EndpointTypes** | Pointer to **[]string** | Service endpoint types to be considered during the resolving | [optional] 

## Methods

### NewResolveRequest

`func NewResolveRequest(issuer string, trustSchemePointers []string, ) *ResolveRequest`

NewResolveRequest instantiates a new ResolveRequest object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewResolveRequestWithDefaults

`func NewResolveRequestWithDefaults() *ResolveRequest`

NewResolveRequestWithDefaults instantiates a new ResolveRequest object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetIssuer

`func (o *ResolveRequest) GetIssuer() string`

GetIssuer returns the Issuer field if non-nil, zero value otherwise.

### GetIssuerOk

`func (o *ResolveRequest) GetIssuerOk() (*string, bool)`

GetIssuerOk returns a tuple with the Issuer field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetIssuer

`func (o *ResolveRequest) SetIssuer(v string)`

SetIssuer sets Issuer field to given value.


### GetTrustSchemePointers

`func (o *ResolveRequest) GetTrustSchemePointers() []string`

GetTrustSchemePointers returns the TrustSchemePointers field if non-nil, zero value otherwise.

### GetTrustSchemePointersOk

`func (o *ResolveRequest) GetTrustSchemePointersOk() (*[]string, bool)`

GetTrustSchemePointersOk returns a tuple with the TrustSchemePointers field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTrustSchemePointers

`func (o *ResolveRequest) SetTrustSchemePointers(v []string)`

SetTrustSchemePointers sets TrustSchemePointers field to given value.


### GetEndpointTypes

`func (o *ResolveRequest) GetEndpointTypes() []string`

GetEndpointTypes returns the EndpointTypes field if non-nil, zero value otherwise.

### GetEndpointTypesOk

`func (o *ResolveRequest) GetEndpointTypesOk() (*[]string, bool)`

GetEndpointTypesOk returns a tuple with the EndpointTypes field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetEndpointTypes

`func (o *ResolveRequest) SetEndpointTypes(v []string)`

SetEndpointTypes sets EndpointTypes field to given value.

### HasEndpointTypes

`func (o *ResolveRequest) HasEndpointTypes() bool`

HasEndpointTypes returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


