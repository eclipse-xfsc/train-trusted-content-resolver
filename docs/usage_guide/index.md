## Usage guide for installation of libraries with GO, Javascript, Java and Python

TCR provides client libraries to allow seamless integration with TCR REST API from various programming languages. Client REST API is generated as part of TCR CICD procedure, and can be downloaded from the following locations:

- GO: generated sources are published at [clients/go module](../../clients/go), to import it see [cmd/main.go app](../../clients/go/cmd/main.go)
- Java: generated sources are not stored in the repository, but can be downloaded as artifacts from GitLab CICD pipeline: [https://gitlab.eclipse.org/api/v4/projects/5223/jobs/artifacts/main/download?job=maven:test](https://gitlab.eclipse.org/api/v4/projects/5223/jobs/artifacts/main/download?job=maven:test). After downloading and unziping the artifacts archive java client library can be accessed as `./clients/java/target/trusted-content-resolver-java-client-1.1.0-SNAPSHOT.jar`. It can be installed in local maven repository and then can be used in the client project build procedure. For more details see [this discussion](https://github.com/eclipse-xfsc/train-trusted-content-resolver/-/issues/62#note_1800083).
- JavaScript: generated sources are not stored in the repository, but can be downloaded as artifacts from GitLab CICD pipeline as above. After downloading and unziping the archive JS client artifacts can be accessed as `./clients/js` folder.
- Python: generated sources are not stored in the repository, but can be downloaded as artifacts from GitLab CICD pipeline as above. After downloading and unziping the archive JS client artifacts can be accessed as `./clients/py` folder.

## Usage guide to integrate with Notarization Service (NOT)

Integration with NOT is detailed in the [TRAIN Concept Document](https://gitlab.eclipse.org/eclipse/xfsc/xfsc-spec-2/-/blob/main/docs/traincd/traincd.md?ref_type=heads#integration-with-notary) and out of scope of TCR functionality.
