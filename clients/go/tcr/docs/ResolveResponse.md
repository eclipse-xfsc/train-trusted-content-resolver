# ResolveResponse

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**TrustSchemePointers** | [**[]ResolvedPointer**](ResolvedPointer.md) | Trust Framework Pointers (e.g., example.federation1.de) | 
**ResolvedResults** | Pointer to [**[]ResolvedDid**](ResolvedDid.md) | PTR Resolution results | [optional] 

## Methods

### NewResolveResponse

`func NewResolveResponse(trustSchemePointers []ResolvedPointer, ) *ResolveResponse`

NewResolveResponse instantiates a new ResolveResponse object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewResolveResponseWithDefaults

`func NewResolveResponseWithDefaults() *ResolveResponse`

NewResolveResponseWithDefaults instantiates a new ResolveResponse object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetTrustSchemePointers

`func (o *ResolveResponse) GetTrustSchemePointers() []ResolvedPointer`

GetTrustSchemePointers returns the TrustSchemePointers field if non-nil, zero value otherwise.

### GetTrustSchemePointersOk

`func (o *ResolveResponse) GetTrustSchemePointersOk() (*[]ResolvedPointer, bool)`

GetTrustSchemePointersOk returns a tuple with the TrustSchemePointers field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTrustSchemePointers

`func (o *ResolveResponse) SetTrustSchemePointers(v []ResolvedPointer)`

SetTrustSchemePointers sets TrustSchemePointers field to given value.


### GetResolvedResults

`func (o *ResolveResponse) GetResolvedResults() []ResolvedDid`

GetResolvedResults returns the ResolvedResults field if non-nil, zero value otherwise.

### GetResolvedResultsOk

`func (o *ResolveResponse) GetResolvedResultsOk() (*[]ResolvedDid, bool)`

GetResolvedResultsOk returns a tuple with the ResolvedResults field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetResolvedResults

`func (o *ResolveResponse) SetResolvedResults(v []ResolvedDid)`

SetResolvedResults sets ResolvedResults field to given value.

### HasResolvedResults

`func (o *ResolveResponse) HasResolvedResults() bool`

HasResolvedResults returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


