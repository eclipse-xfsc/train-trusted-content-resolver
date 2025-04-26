# ValidateRequest

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Issuer** | **string** | Issuer details from the VC/VP (e.g., DID/URI) | 
**Did** | **string** | DID resolved from Trust Pointer | 
**Endpoints** | **[]string** | Resolved Trust List VC endpoints | 

## Methods

### NewValidateRequest

`func NewValidateRequest(issuer string, did string, endpoints []string, ) *ValidateRequest`

NewValidateRequest instantiates a new ValidateRequest object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewValidateRequestWithDefaults

`func NewValidateRequestWithDefaults() *ValidateRequest`

NewValidateRequestWithDefaults instantiates a new ValidateRequest object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetIssuer

`func (o *ValidateRequest) GetIssuer() string`

GetIssuer returns the Issuer field if non-nil, zero value otherwise.

### GetIssuerOk

`func (o *ValidateRequest) GetIssuerOk() (*string, bool)`

GetIssuerOk returns a tuple with the Issuer field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetIssuer

`func (o *ValidateRequest) SetIssuer(v string)`

SetIssuer sets Issuer field to given value.


### GetDid

`func (o *ValidateRequest) GetDid() string`

GetDid returns the Did field if non-nil, zero value otherwise.

### GetDidOk

`func (o *ValidateRequest) GetDidOk() (*string, bool)`

GetDidOk returns a tuple with the Did field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetDid

`func (o *ValidateRequest) SetDid(v string)`

SetDid sets Did field to given value.


### GetEndpoints

`func (o *ValidateRequest) GetEndpoints() []string`

GetEndpoints returns the Endpoints field if non-nil, zero value otherwise.

### GetEndpointsOk

`func (o *ValidateRequest) GetEndpointsOk() (*[]string, bool)`

GetEndpointsOk returns a tuple with the Endpoints field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetEndpoints

`func (o *ValidateRequest) SetEndpoints(v []string)`

SetEndpoints sets Endpoints field to given value.



[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


