# ResolvedDid

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Did** | **string** | Corresponding DID mapped to Trust Framework Pointer | 
**ResolvedDoc** | Pointer to [**ResolvedDoc**](ResolvedDoc.md) |  | [optional] 
**Error** | Pointer to [**Error**](Error.md) |  | [optional] 

## Methods

### NewResolvedDid

`func NewResolvedDid(did string, ) *ResolvedDid`

NewResolvedDid instantiates a new ResolvedDid object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewResolvedDidWithDefaults

`func NewResolvedDidWithDefaults() *ResolvedDid`

NewResolvedDidWithDefaults instantiates a new ResolvedDid object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetDid

`func (o *ResolvedDid) GetDid() string`

GetDid returns the Did field if non-nil, zero value otherwise.

### GetDidOk

`func (o *ResolvedDid) GetDidOk() (*string, bool)`

GetDidOk returns a tuple with the Did field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetDid

`func (o *ResolvedDid) SetDid(v string)`

SetDid sets Did field to given value.


### GetResolvedDoc

`func (o *ResolvedDid) GetResolvedDoc() ResolvedDoc`

GetResolvedDoc returns the ResolvedDoc field if non-nil, zero value otherwise.

### GetResolvedDocOk

`func (o *ResolvedDid) GetResolvedDocOk() (*ResolvedDoc, bool)`

GetResolvedDocOk returns a tuple with the ResolvedDoc field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetResolvedDoc

`func (o *ResolvedDid) SetResolvedDoc(v ResolvedDoc)`

SetResolvedDoc sets ResolvedDoc field to given value.

### HasResolvedDoc

`func (o *ResolvedDid) HasResolvedDoc() bool`

HasResolvedDoc returns a boolean if a field has been set.

### GetError

`func (o *ResolvedDid) GetError() Error`

GetError returns the Error field if non-nil, zero value otherwise.

### GetErrorOk

`func (o *ResolvedDid) GetErrorOk() (*Error, bool)`

GetErrorOk returns a tuple with the Error field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetError

`func (o *ResolvedDid) SetError(v Error)`

SetError sets Error field to given value.

### HasError

`func (o *ResolvedDid) HasError() bool`

HasError returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


