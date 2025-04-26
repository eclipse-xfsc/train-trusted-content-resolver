# ValidateResponse

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**DidVerified** | **bool** | DID verified via well-known did-configuration | 
**Endpoints** | Pointer to [**[]ResolvedTrustList**](ResolvedTrustList.md) | If Issuer details are found, display meta data information, public keys, schema from the trust list..? | [optional] 

## Methods

### NewValidateResponse

`func NewValidateResponse(didVerified bool, ) *ValidateResponse`

NewValidateResponse instantiates a new ValidateResponse object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewValidateResponseWithDefaults

`func NewValidateResponseWithDefaults() *ValidateResponse`

NewValidateResponseWithDefaults instantiates a new ValidateResponse object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetDidVerified

`func (o *ValidateResponse) GetDidVerified() bool`

GetDidVerified returns the DidVerified field if non-nil, zero value otherwise.

### GetDidVerifiedOk

`func (o *ValidateResponse) GetDidVerifiedOk() (*bool, bool)`

GetDidVerifiedOk returns a tuple with the DidVerified field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetDidVerified

`func (o *ValidateResponse) SetDidVerified(v bool)`

SetDidVerified sets DidVerified field to given value.


### GetEndpoints

`func (o *ValidateResponse) GetEndpoints() []ResolvedTrustList`

GetEndpoints returns the Endpoints field if non-nil, zero value otherwise.

### GetEndpointsOk

`func (o *ValidateResponse) GetEndpointsOk() (*[]ResolvedTrustList, bool)`

GetEndpointsOk returns a tuple with the Endpoints field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetEndpoints

`func (o *ValidateResponse) SetEndpoints(v []ResolvedTrustList)`

SetEndpoints sets Endpoints field to given value.

### HasEndpoints

`func (o *ValidateResponse) HasEndpoints() bool`

HasEndpoints returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


