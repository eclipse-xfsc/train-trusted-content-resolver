package main

import (
	"context"
	"encoding/json"
	"fmt"
	tcr "github.com/eclipse-xfsc/train-trusted-content-resolver/clients/go/tcr"
	"io/ioutil"
	"net/url"
	"os"
	"strings"
)

const DEF_URI = "http://localhost:8087/tcr/v1"

type ResolveServiceParams struct {
	Uri      string `json: "uri,omitempty"`
	Data     string `json: "data,omitempty"`
	Endpoint string `json: "endpoint,omitempty"`
}

func main() {
	args := os.Args[1:]
	params := ResolveServiceParams{}
	var baseUri, endpoint, data string
	for _, arg := range args {
		parts := strings.Split(arg, "=")
		if "u" == parts[0] || "uri" == parts[0] {
			baseUri = parts[1]
			continue
		}
		if "e" == parts[0] || "endpoint" == parts[0] {
			endpoint = parts[1]
			continue
		}
		if "d" == parts[0] || "data" == parts[0] {
			data = parts[1]
			continue
		}
		if "f" == parts[0] || "file" == parts[0] {
			file, _ := ioutil.ReadFile(parts[1])
			_ = json.Unmarshal([]byte(file), &params)
			continue
		}

		fmt.Fprintln(os.Stderr, "unknown parameter:", arg)
		os.Exit(1)
	}

	if (params != ResolveServiceParams{}) {
		if baseUri == "" {
			baseUri = params.Uri
		}
		if endpoint == "" {
			endpoint = params.Endpoint
		}
		if data == "" {
			data = params.Data
		}
	}

	if baseUri == "" {
		baseUri = DEF_URI
	}
	if endpoint == "" {
		fmt.Fprintln(os.Stderr, "error: no TCR endpoint specified")
		os.Exit(2)
	}

	var err error
	var rrq tcr.ResolveRequest
	var vrq tcr.ValidateRequest
	if "resolve" == endpoint {
		if data == "" {
			fmt.Fprintln(os.Stderr, "error: no resolution data provided")
			os.Exit(4)
		}
		err = rrq.UnmarshalJSON([]byte(data))
	} else if "validate" == endpoint {
		if data == "" {
			fmt.Fprintln(os.Stderr, "error: no validation data provided")
			os.Exit(4)
		}
		err = vrq.UnmarshalJSON([]byte(data))
	} else {
		fmt.Fprintln(os.Stderr, "error: unknown TCR endpoint: "+endpoint)
		os.Exit(3)
	}

	if err != nil {
		fmt.Fprintln(os.Stderr, "error parsing data:", err)
		os.Exit(5)
	}
	uri, err := url.Parse(baseUri)
	if err != nil {
		fmt.Fprintln(os.Stderr, "error parsing baseUri:", err)
		os.Exit(6)
	}

	config := tcr.NewConfiguration()
	config.AddDefaultHeader("Content-Type", "application/json")
	config.Host = uri.Host
	config.Scheme = uri.Scheme
	client := tcr.NewAPIClient(config)

	var result []byte
	switch endpoint {
	case "resolve":
		resp, _, e := client.TrustedContentResolverAPI.ResolveTrustList(context.Background()).ResolveRequest(rrq).Execute()
		if e == nil {
			result, err = resp.MarshalJSON()
		} else {
			err = e
		}
	case "validate":
		resp, _, e := client.TrustedContentResolverAPI.ValidateTrustList(context.Background()).ValidateRequest(vrq).Execute()
		if e == nil {
			result, err = resp.MarshalJSON()
		} else {
			err = e
		}
	default:
		result = nil
		err = nil
	}
	if err == nil {
		fmt.Println(string(result))
	} else {
		fmt.Println(err)
	}
}
