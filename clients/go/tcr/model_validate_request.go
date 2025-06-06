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

// checks if the ValidateRequest type satisfies the MappedNullable interface at compile time
var _ MappedNullable = &ValidateRequest{}

// ValidateRequest struct for ValidateRequest
type ValidateRequest struct {
	// Issuer details from the VC/VP (e.g., DID/URI)
	Issuer string `json:"issuer"`
	// DID resolved from Trust Pointer
	Did string `json:"did"`
	// Resolved Trust List VC endpoints
	Endpoints []string `json:"endpoints"`
}

type _ValidateRequest ValidateRequest

// NewValidateRequest instantiates a new ValidateRequest object
// This constructor will assign default values to properties that have it defined,
// and makes sure properties required by API are set, but the set of arguments
// will change when the set of required properties is changed
func NewValidateRequest(issuer string, did string, endpoints []string) *ValidateRequest {
	this := ValidateRequest{}
	this.Issuer = issuer
	this.Did = did
	this.Endpoints = endpoints
	return &this
}

// NewValidateRequestWithDefaults instantiates a new ValidateRequest object
// This constructor will only assign default values to properties that have it defined,
// but it doesn't guarantee that properties required by API are set
func NewValidateRequestWithDefaults() *ValidateRequest {
	this := ValidateRequest{}
	return &this
}

// GetIssuer returns the Issuer field value
func (o *ValidateRequest) GetIssuer() string {
	if o == nil {
		var ret string
		return ret
	}

	return o.Issuer
}

// GetIssuerOk returns a tuple with the Issuer field value
// and a boolean to check if the value has been set.
func (o *ValidateRequest) GetIssuerOk() (*string, bool) {
	if o == nil {
		return nil, false
	}
	return &o.Issuer, true
}

// SetIssuer sets field value
func (o *ValidateRequest) SetIssuer(v string) {
	o.Issuer = v
}

// GetDid returns the Did field value
func (o *ValidateRequest) GetDid() string {
	if o == nil {
		var ret string
		return ret
	}

	return o.Did
}

// GetDidOk returns a tuple with the Did field value
// and a boolean to check if the value has been set.
func (o *ValidateRequest) GetDidOk() (*string, bool) {
	if o == nil {
		return nil, false
	}
	return &o.Did, true
}

// SetDid sets field value
func (o *ValidateRequest) SetDid(v string) {
	o.Did = v
}

// GetEndpoints returns the Endpoints field value
func (o *ValidateRequest) GetEndpoints() []string {
	if o == nil {
		var ret []string
		return ret
	}

	return o.Endpoints
}

// GetEndpointsOk returns a tuple with the Endpoints field value
// and a boolean to check if the value has been set.
func (o *ValidateRequest) GetEndpointsOk() ([]string, bool) {
	if o == nil {
		return nil, false
	}
	return o.Endpoints, true
}

// SetEndpoints sets field value
func (o *ValidateRequest) SetEndpoints(v []string) {
	o.Endpoints = v
}

func (o ValidateRequest) MarshalJSON() ([]byte, error) {
	toSerialize,err := o.ToMap()
	if err != nil {
		return []byte{}, err
	}
	return json.Marshal(toSerialize)
}

func (o ValidateRequest) ToMap() (map[string]interface{}, error) {
	toSerialize := map[string]interface{}{}
	toSerialize["issuer"] = o.Issuer
	toSerialize["did"] = o.Did
	toSerialize["endpoints"] = o.Endpoints
	return toSerialize, nil
}

func (o *ValidateRequest) UnmarshalJSON(bytes []byte) (err error) {
    // This validates that all required properties are included in the JSON object
	// by unmarshalling the object into a generic map with string keys and checking
	// that every required field exists as a key in the generic map.
	requiredProperties := []string{
		"issuer",
		"did",
		"endpoints",
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

	varValidateRequest := _ValidateRequest{}

	err = json.Unmarshal(bytes, &varValidateRequest)

	if err != nil {
		return err
	}

	*o = ValidateRequest(varValidateRequest)

	return err
}

type NullableValidateRequest struct {
	value *ValidateRequest
	isSet bool
}

func (v NullableValidateRequest) Get() *ValidateRequest {
	return v.value
}

func (v *NullableValidateRequest) Set(val *ValidateRequest) {
	v.value = val
	v.isSet = true
}

func (v NullableValidateRequest) IsSet() bool {
	return v.isSet
}

func (v *NullableValidateRequest) Unset() {
	v.value = nil
	v.isSet = false
}

func NewNullableValidateRequest(val *ValidateRequest) *NullableValidateRequest {
	return &NullableValidateRequest{value: val, isSet: true}
}

func (v NullableValidateRequest) MarshalJSON() ([]byte, error) {
	return json.Marshal(v.value)
}

func (v *NullableValidateRequest) UnmarshalJSON(src []byte) error {
	v.isSet = true
	return json.Unmarshal(src, &v.value)
}


