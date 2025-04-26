## Helm charts

Here we provide helm charts for 

- [TCR Service](./tcr-service)
- [Universal Resolver](./uni-resolver)
- [UPort DID Driver](./uport-driver)

First clone the TRAIN Trusted Content Resolver (TCR) repository:

For manual TCR installation with helm charts go to the `helm` folder and run standard helm commands:

```bash
helm > helm install tcr-svc ./tcr-service
helm > helm install uni-res ./uni-resolver
helm > helm install uport-drv ./uport-driver
```

We also publish prepared charts into Helm repository as part of CICD pipeline, so the charts can be used in overall cluster deployment with tools like `ArgoCD`
