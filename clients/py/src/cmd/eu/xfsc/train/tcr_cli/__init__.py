import click

import eu_xfsc_train_tcr as tcr


@click.group()
def cli_resolve() -> None:
    pass


@cli_resolve.command()
@click.option('--uri')
@click.option('--issuer', required=True)
@click.option('--trust-scheme-pointer', 'trust_scheme_pointers', multiple=True, required=True)
@click.option('--endpoint-type', 'endpoint_types', multiple=True, required=False, default=[])
def resolve(
        uri: str,
        issuer: str,
        trust_scheme_pointers: list[str],
        endpoint_types: list[str]
) -> None:
    """
    Example of query

    eu-xfsc-train-tcr resolve \
        --issuer=1 \
        --trust-scheme-pointer=22 \
        --trust-scheme-pointer=11 \
        --endpoint-type=33 \
        --endpoint-type=44 \
        --uri=http://localhost:13001/tcr/v1
    """
    configuration = tcr.Configuration(host=uri)

    with tcr.ApiClient(configuration) as api_client:
        api_instance = tcr.TrustedContentResolverApi(api_client)
        resolve_request = tcr.ResolveRequest(
            issuer=issuer,
            trust_scheme_pointers=trust_scheme_pointers,
            endpoint_types=endpoint_types
        )

        api_response = api_instance.resolve_trust_list(resolve_request)

        print(api_response.model_dump_json())


@click.group()
def cli_validate() -> None:
    pass


@cli_validate.command()
@click.option('--uri')
@click.option('--issuer', required=True)
@click.option('--did', required=True)
@click.option('--endpoint', 'endpoints', multiple=True, required=True)
def validate(uri: str, issuer: str, did: str, endpoints: list[str]) -> None:
    """
    Example of query

    eu-xfsc-train-tcr validate endpoint \
        --issuer=1 \
        --did=some-did \
        --endpoint=33 \
        --endpoint=44 \
        --uri=http://localhost:13001/tcr/v1
    """

    configuration = tcr.Configuration(
        host=uri
    )

    with tcr.ApiClient(configuration) as api_client:
        api_instance = tcr.TrustedContentResolverApi(api_client)
        validate_request = tcr.ValidateRequest(
            issuer=issuer,
            did=did,
            endpoints=endpoints
        )

        api_response = api_instance.validate_trust_list(validate_request)
        print(api_response.model_dump_json())


cli = click.CommandCollection(sources=[cli_resolve, cli_validate])


def entry_point() -> None:
    cli()


if __name__ == '__main__':
    entry_point()
