# ResolvedTrustList

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**VcUri** | Pointer to **string** | Resolved VC endpoint URI | [optional] 
**TlUri** | Pointer to **string** | Resolved Trust List URI | [optional] 
**TrustList** | Pointer to **map[string]interface{}** | Trust List structure | [optional] 
**VcVerified** | **bool** | VC signature verified and VC hash matches Trust List content | 

## Methods

### NewResolvedTrustList

`func NewResolvedTrustList(vcVerified bool, ) *ResolvedTrustList`

NewResolvedTrustList instantiates a new ResolvedTrustList object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewResolvedTrustListWithDefaults

`func NewResolvedTrustListWithDefaults() *ResolvedTrustList`

NewResolvedTrustListWithDefaults instantiates a new ResolvedTrustList object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetVcUri

`func (o *ResolvedTrustList) GetVcUri() string`

GetVcUri returns the VcUri field if non-nil, zero value otherwise.

### GetVcUriOk

`func (o *ResolvedTrustList) GetVcUriOk() (*string, bool)`

GetVcUriOk returns a tuple with the VcUri field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetVcUri

`func (o *ResolvedTrustList) SetVcUri(v string)`

SetVcUri sets VcUri field to given value.

### HasVcUri

`func (o *ResolvedTrustList) HasVcUri() bool`

HasVcUri returns a boolean if a field has been set.

### GetTlUri

`func (o *ResolvedTrustList) GetTlUri() string`

GetTlUri returns the TlUri field if non-nil, zero value otherwise.

### GetTlUriOk

`func (o *ResolvedTrustList) GetTlUriOk() (*string, bool)`

GetTlUriOk returns a tuple with the TlUri field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTlUri

`func (o *ResolvedTrustList) SetTlUri(v string)`

SetTlUri sets TlUri field to given value.

### HasTlUri

`func (o *ResolvedTrustList) HasTlUri() bool`

HasTlUri returns a boolean if a field has been set.

### GetTrustList

`func (o *ResolvedTrustList) GetTrustList() map[string]interface{}`

GetTrustList returns the TrustList field if non-nil, zero value otherwise.

### GetTrustListOk

`func (o *ResolvedTrustList) GetTrustListOk() (*map[string]interface{}, bool)`

GetTrustListOk returns a tuple with the TrustList field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTrustList

`func (o *ResolvedTrustList) SetTrustList(v map[string]interface{})`

SetTrustList sets TrustList field to given value.

### HasTrustList

`func (o *ResolvedTrustList) HasTrustList() bool`

HasTrustList returns a boolean if a field has been set.

### GetVcVerified

`func (o *ResolvedTrustList) GetVcVerified() bool`

GetVcVerified returns the VcVerified field if non-nil, zero value otherwise.

### GetVcVerifiedOk

`func (o *ResolvedTrustList) GetVcVerifiedOk() (*bool, bool)`

GetVcVerifiedOk returns a tuple with the VcVerified field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetVcVerified

`func (o *ResolvedTrustList) SetVcVerified(v bool)`

SetVcVerified sets VcVerified field to given value.



[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


