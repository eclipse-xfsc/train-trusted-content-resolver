## Installation & Configuration Guide

The application supports three different ways to run it:

- Local run with `maven`. See detailed instructions in this [document](../build_proc). Configured with [application.yml](../../service/src/main/resources/application.yml)
- Run TR image in `docker`. See docker-related details in this [readme](../../docker). Original configuration from application.tyml can be overwritten with [.env file](../../docker/.env)
- Deploy and run TCR service in `kubernetes` environment. See the [Helm charts](../../deploy/helm) for TCR service itself and for Universal Resolver and its DID Drivers. Application deployment configuration is managed through the Helm [values file](../../deploy/helm/tcr-service/values.yaml)


The most important TCR configuration settings are specified below: 

| Property                             | Description                                      | Default value                                                 |
|--------------------------------------|:-------------------------------------------------|:--------------------------------------------------------------|
| tcr.did.base-uri                     | Universal Resolver url if external version is used. To use embedded Resolver version set it to `local`. | `local` |
| tcr.did.config-path                  | Path to file with Universal Resolver DID Driver settings if embedded version is used. | `uni-resolver-config.json` |
| tcr.did.cache.size                   | Size of local cache for DID/VC Documents, in entries. 0 means no size limitation. | `0`                          |
| tcr.did.cache.timeout                | Time to live for cached DID/VC documents. 0 disables caching | `1H` - 1 hour.                                    |
| tcr.dns.doh.enabled                  | if `true` then DNS over HTTPs Resolver will be used, otherwise standard Resolver | `false`                        |
| tcr.dns.hosts                        | DNS server IPs or hosts if `DoH` enabled. When several IPs/hosts specified, they all used for PTR resolution. | empty - then default env DNS is used. |
| tcr.dns.timeout                      | DNS request timeout, in milliseconds.            | `500`                                                         |
| tcr.dns.dnssec.enabled               | Enable/disable DNSSEC protocol.                  | `true` - DNSSEC enabled.<BR/>(In production environments, `false` must not be used, according to TRAIN specification. Other values than the default `true` may only be used for demo and showcase environments)                                      |
| tcr.dns.dnssec.rootPath              | Path to file containing DNSSEC root key.         | empty - then hardcoded internal value will be used            |
| tcr.tl.cache.size                    | Size of local cache for Trust Lists, in entries. 0 means no size limitation. | `0`                               |
| tcr.tl.cache.timeout                 | Time to live for cached Trust Lists. 0 disables caching | `1H` - 1 hour.                                         |
|                                      |                                                  |                                                               |
| otlp.collector.url                   | The Open Telemetry Collector url                 | empty - metrics/traces/logs publication via Open Telemetry Protocol disabled. |
