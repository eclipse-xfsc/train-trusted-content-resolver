/*
Eclipse XFSC TRAIN Trusted Content Resolver

XFSC TRAIN Trusted Content Resolver REST API

API version: 1.0.0
Contact: denis.sukhoroslov@telekom.com
*/

// Code generated by OpenAPI Generator (https://openapi-generator.tech); DO NOT EDIT.

package tcr

import (
	"encoding/json"
	"fmt"
)

// checks if the ResolveResponse type satisfies the MappedNullable interface at compile time
var _ MappedNullable = &ResolveResponse{}

// ResolveResponse struct for ResolveResponse
type ResolveResponse struct {
	// Trust Framework Pointers (e.g., example.federation1.de)
	TrustSchemePointers []ResolvedPointer `json:"trustSchemePointers"`
	// PTR Resolution results
	ResolvedResults []ResolvedDid `json:"resolvedResults,omitempty"`
}

type _ResolveResponse ResolveResponse

// NewResolveResponse instantiates a new ResolveResponse object
// This constructor will assign default values to properties that have it defined,
// and makes sure properties required by API are set, but the set of arguments
// will change when the set of required properties is changed
func NewResolveResponse(trustSchemePointers []ResolvedPointer) *ResolveResponse {
	this := ResolveResponse{}
	this.TrustSchemePointers = trustSchemePointers
	return &this
}

// NewResolveResponseWithDefaults instantiates a new ResolveResponse object
// This constructor will only assign default values to properties that have it defined,
// but it doesn't guarantee that properties required by API are set
func NewResolveResponseWithDefaults() *ResolveResponse {
	this := ResolveResponse{}
	return &this
}

// GetTrustSchemePointers returns the TrustSchemePointers field value
func (o *ResolveResponse) GetTrustSchemePointers() []ResolvedPointer {
	if o == nil {
		var ret []ResolvedPointer
		return ret
	}

	return o.TrustSchemePointers
}

// GetTrustSchemePointersOk returns a tuple with the TrustSchemePointers field value
// and a boolean to check if the value has been set.
func (o *ResolveResponse) GetTrustSchemePointersOk() ([]ResolvedPointer, bool) {
	if o == nil {
		return nil, false
	}
	return o.TrustSchemePointers, true
}

// SetTrustSchemePointers sets field value
func (o *ResolveResponse) SetTrustSchemePointers(v []ResolvedPointer) {
	o.TrustSchemePointers = v
}

// GetResolvedResults returns the ResolvedResults field value if set, zero value otherwise.
func (o *ResolveResponse) GetResolvedResults() []ResolvedDid {
	if o == nil || IsNil(o.ResolvedResults) {
		var ret []ResolvedDid
		return ret
	}
	return o.ResolvedResults
}

// GetResolvedResultsOk returns a tuple with the ResolvedResults field value if set, nil otherwise
// and a boolean to check if the value has been set.
func (o *ResolveResponse) GetResolvedResultsOk() ([]ResolvedDid, bool) {
	if o == nil || IsNil(o.ResolvedResults) {
		return nil, false
	}
	return o.ResolvedResults, true
}

// HasResolvedResults returns a boolean if a field has been set.
func (o *ResolveResponse) HasResolvedResults() bool {
	if o != nil && !IsNil(o.ResolvedResults) {
		return true
	}

	return false
}

// SetResolvedResults gets a reference to the given []ResolvedDid and assigns it to the ResolvedResults field.
func (o *ResolveResponse) SetResolvedResults(v []ResolvedDid) {
	o.ResolvedResults = v
}

func (o ResolveResponse) MarshalJSON() ([]byte, error) {
	toSerialize,err := o.ToMap()
	if err != nil {
		return []byte{}, err
	}
	return json.Marshal(toSerialize)
}

func (o ResolveResponse) ToMap() (map[string]interface{}, error) {
	toSerialize := map[string]interface{}{}
	toSerialize["trustSchemePointers"] = o.TrustSchemePointers
	if !IsNil(o.ResolvedResults) {
		toSerialize["resolvedResults"] = o.ResolvedResults
	}
	return toSerialize, nil
}

func (o *ResolveResponse) UnmarshalJSON(bytes []byte) (err error) {
    // This validates that all required properties are included in the JSON object
	// by unmarshalling the object into a generic map with string keys and checking
	// that every required field exists as a key in the generic map.
	requiredProperties := []string{
		"trustSchemePointers",
	}

	allProperties := make(map[string]interface{})

	err = json.Unmarshal(bytes, &allProperties)

	if err != nil {
		return err;
	}

	for _, requiredProperty := range(requiredProperties) {
		if _, exists := allProperties[requiredProperty]; !exists {
			return fmt.Errorf("no value given for required property %v", requiredProperty)
		}
	}

	varResolveResponse := _ResolveResponse{}

	err = json.Unmarshal(bytes, &varResolveResponse)

	if err != nil {
		return err
	}

	*o = ResolveResponse(varResolveResponse)

	return err
}

type NullableResolveResponse struct {
	value *ResolveResponse
	isSet bool
}

func (v NullableResolveResponse) Get() *ResolveResponse {
	return v.value
}

func (v *NullableResolveResponse) Set(val *ResolveResponse) {
	v.value = val
	v.isSet = true
}

func (v NullableResolveResponse) IsSet() bool {
	return v.isSet
}

func (v *NullableResolveResponse) Unset() {
	v.value = nil
	v.isSet = false
}

func NewNullableResolveResponse(val *ResolveResponse) *NullableResolveResponse {
	return &NullableResolveResponse{value: val, isSet: true}
}

func (v NullableResolveResponse) MarshalJSON() ([]byte, error) {
	return json.Marshal(v.value)
}

func (v *NullableResolveResponse) UnmarshalJSON(src []byte) error {
	v.isSet = true
	return json.Unmarshal(src, &v.value)
}


