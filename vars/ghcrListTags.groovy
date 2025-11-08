#!/usr/bin/env groovy
def call(Map properties, String token) {
    if (!properties.credentialId){
        properties.credentialId = "github-login-secret"
    }
    if (!properties.debug){
        properties.debug = false
    }
    if (!properties.quiet){
        properties.quiet = true
    }
    def acceptHeader = [
        'application/vnd.oci.image.manifest.v1+json',
        'application/vnd.oci.image.index.v1+json',
        'application/vnd.docker.distribution.manifest.v2+json',
        'application/vnd.docker.distribution.manifest.list.v2+json',
        'application/vnd.docker.distribution.manifest.v1+json',
        'application/json'
    ].join(', ')
    
    def response = httpRequest(
        customHeaders: [
            [name: 'Accept', value: acceptHeader],
            [name: 'Authorization', value: "Bearer ${token}"]
        ],
        url: "https://ghcr.io/v2/${properties.organization}/${properties.PACKAGE_NAME}/tags/list",
        consoleLogResponseBody: properties.debug,
        quiet: properties.quiet,
        wrapAsMultipart: false
    )
    
    def tagsList = readJSON text: response.content
    return tagsList
}