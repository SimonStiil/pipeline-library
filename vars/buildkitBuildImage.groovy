#!/usr/bin/env groovy
def call(TreeMap scmData, Map properties) {
  container('buildkit') {
    stage('Build Docker Image') {
      withEnv(["GIT_COMMIT=${scmData.GIT_COMMIT}", "PACKAGE_NAME=${properties.PACKAGE_NAME}", "PACKAGE_CONTAINER_PLATFORMS=${properties.PACKAGE_CONTAINER_PLATFORMS}", "PACKAGE_DESTINATION=${properties.PACKAGE_DESTINATION}", "PACKAGE_CONTAINER_SOURCE=${properties.PACKAGE_CONTAINER_SOURCE}", "GIT_BRANCH=${BRANCH_NAME}"]) {
        sh '''
          buildctl --addr 'tcp://buildkitd:1234'\
          --tlscacert /certs/client/ca.crt \
          --tlscert /certs/client/tls.crt \
          --tlskey /certs/client/tls.key \
          build \
          --frontend dockerfile.v0 \
          --opt filename=Dockerfile --opt platform=$PACKAGE_CONTAINER_PLATFORMS \
          --local context=$(pwd) --local dockerfile=$(pwd) \
          --import-cache $PACKAGE_DESTINATION/$PACKAGE_NAME:buildcache \
          --export-cache $PACKAGE_DESTINATION/$PACKAGE_NAME:buildcache \
          --output=type=image,name=$PACKAGE_DESTINATION/$PACKAGE_NAME:$BRANCH_NAME,push=true,annotation.org.opencontainers.image.description="Build based on $PACKAGE_CONTAINER_SOURCE/commit/$GIT_COMMIT",annotation.org.opencontainers.image.revision=$GIT_COMMIT,annotation.org.opencontainers.image.version=$GIT_BRANCH,annotation.org.opencontainers.image.source="https://github.com/SimonStiil/cfdyndns",annotation.org.opencontainers.image.licenses=GPL-2.0-only
          '''
      }
    }
  }
}