# Trusted Content Resolver

## Description
TRAIN provides a trust management infrastructure for Gaia-X Federation Services (GXFS). Trusted Content Resolver service allows Trusted Framework Pointers resolution and verification. The Trust Services API (TSA) component can use the Trusted Content Resolver libraries to verify the institutional trust of verifiable credentials. The Organizational Credential Manager (OCM) service can use the Trusted Content Resolver libraries to validate the trust of the organizational verifiable credentials before storing them in the wallet. The Notary service (via the Notarization API) uses the TSPA Connector to enroll trusted entities into the trust framework and add them via the TSPA/Federator to the Trust List.

So here is the reference implementation of the [Gaia-X TRAIN Lot](https://eclipse.dev/xfsc/train/train/#trusted-content-resolver).

## [Click here to view the TCR Documentation](./docs)
All service documentation including installation instructions, operation and usage guides and other materials can be found by the link above.

## Support
To get support you can open an issue in the project [Issues](https://github.com/eclipse-xfsc/train-trusted-content-resolver/-/issues) section.


## Getting Started
To start with TCR project please follow the instructions: [Steps to build TCR](./docker/README.md).

## FAQ
See our global FAQ Page: https://gitlab.eclipse.org/eclipse/xfsc/train/TRAIN-Documentation#frequently-asked-questions

## Keyword Directory
See our global [Keyword Directory](https://gitlab.eclipse.org/eclipse/xfsc/xfsc-spec-2/-/blob/main/docs/train/train.md?ref_type=heads#definitions-acronyms-and-abbreviations)

## Roadmap
TCR version 1.0.0 was released in December 2023. The current release version is 1.0.5. All TCR releases can be found the standard GitLab [Releases](https://github.com/eclipse-xfsc/train-trusted-content-resolver/-/releases) section.

## Contributing
If you want to contribute to the project - please request a membership at [Project Members](https://github.com/eclipse-xfsc/train-trusted-content-resolver/-/project_members) section or drop an email to project maintainer [Lauresha Memeti](https://gitlab.eclipse.org/laureshamemeti).

## Authors and acknowledgment
The project is implemented by T-Systems International GmbH, project members are:
- [Andrei Danciuc](https://gitlab.eclipse.org/andreidanciuc)
- [Denis Sukhoroslov](https://gitlab.eclipse.org/dsukhoroslov)
- [Frank Kassigkeit](https://gitlab.eclipse.org/fkassigk)
- [Julius Helm](https://gitlab.eclipse.org/jhtsi)
- [Martin Siegloch](https://gitlab.eclipse.org/pmsmartin)
- [Michael Zigldrum](https://gitlab.eclipse.org/mzigldrum)

## License
XFSC Trusted Content Resolver Service is Open Source software released under the [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0.html).
