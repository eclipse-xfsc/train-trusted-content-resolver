# ResolvedPointer

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Pointer** | **string** | Trust Framework Pointer (e.g., example.federation1.de) | 
**Dids** | Pointer to **[]string** | DIDs resolved from the pointer | [optional] 
**Error** | Pointer to [**Error**](Error.md) |  | [optional] 

## Methods

### NewResolvedPointer

`func NewResolvedPointer(pointer string, ) *ResolvedPointer`

NewResolvedPointer instantiates a new ResolvedPointer object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewResolvedPointerWithDefaults

`func NewResolvedPointerWithDefaults() *ResolvedPointer`

NewResolvedPointerWithDefaults instantiates a new ResolvedPointer object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetPointer

`func (o *ResolvedPointer) GetPointer() string`

GetPointer returns the Pointer field if non-nil, zero value otherwise.

### GetPointerOk

`func (o *ResolvedPointer) GetPointerOk() (*string, bool)`

GetPointerOk returns a tuple with the Pointer field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetPointer

`func (o *ResolvedPointer) SetPointer(v string)`

SetPointer sets Pointer field to given value.


### GetDids

`func (o *ResolvedPointer) GetDids() []string`

GetDids returns the Dids field if non-nil, zero value otherwise.

### GetDidsOk

`func (o *ResolvedPointer) GetDidsOk() (*[]string, bool)`

GetDidsOk returns a tuple with the Dids field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetDids

`func (o *ResolvedPointer) SetDids(v []string)`

SetDids sets Dids field to given value.

### HasDids

`func (o *ResolvedPointer) HasDids() bool`

HasDids returns a boolean if a field has been set.

### GetError

`func (o *ResolvedPointer) GetError() Error`

GetError returns the Error field if non-nil, zero value otherwise.

### GetErrorOk

`func (o *ResolvedPointer) GetErrorOk() (*Error, bool)`

GetErrorOk returns a tuple with the Error field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetError

`func (o *ResolvedPointer) SetError(v Error)`

SetError sets Error field to given value.

### HasError

`func (o *ResolvedPointer) HasError() bool`

HasError returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


