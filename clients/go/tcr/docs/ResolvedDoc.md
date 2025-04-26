# ResolvedDoc

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Document** | **map[string]interface{}** | DID Document of the DID | 
**Endpoints** | Pointer to [**[]ResolvedTrustList**](ResolvedTrustList.md) | Resolved Trust List VC endpoints | [optional] 
**DidVerified** | **bool** | Well-known did-configuration verification result | 

## Methods

### NewResolvedDoc

`func NewResolvedDoc(document map[string]interface{}, didVerified bool, ) *ResolvedDoc`

NewResolvedDoc instantiates a new ResolvedDoc object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewResolvedDocWithDefaults

`func NewResolvedDocWithDefaults() *ResolvedDoc`

NewResolvedDocWithDefaults instantiates a new ResolvedDoc object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetDocument

`func (o *ResolvedDoc) GetDocument() map[string]interface{}`

GetDocument returns the Document field if non-nil, zero value otherwise.

### GetDocumentOk

`func (o *ResolvedDoc) GetDocumentOk() (*map[string]interface{}, bool)`

GetDocumentOk returns a tuple with the Document field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetDocument

`func (o *ResolvedDoc) SetDocument(v map[string]interface{})`

SetDocument sets Document field to given value.


### GetEndpoints

`func (o *ResolvedDoc) GetEndpoints() []ResolvedTrustList`

GetEndpoints returns the Endpoints field if non-nil, zero value otherwise.

### GetEndpointsOk

`func (o *ResolvedDoc) GetEndpointsOk() (*[]ResolvedTrustList, bool)`

GetEndpointsOk returns a tuple with the Endpoints field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetEndpoints

`func (o *ResolvedDoc) SetEndpoints(v []ResolvedTrustList)`

SetEndpoints sets Endpoints field to given value.

### HasEndpoints

`func (o *ResolvedDoc) HasEndpoints() bool`

HasEndpoints returns a boolean if a field has been set.

### GetDidVerified

`func (o *ResolvedDoc) GetDidVerified() bool`

GetDidVerified returns the DidVerified field if non-nil, zero value otherwise.

### GetDidVerifiedOk

`func (o *ResolvedDoc) GetDidVerifiedOk() (*bool, bool)`

GetDidVerifiedOk returns a tuple with the DidVerified field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetDidVerified

`func (o *ResolvedDoc) SetDidVerified(v bool)`

SetDidVerified sets DidVerified field to given value.



[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


