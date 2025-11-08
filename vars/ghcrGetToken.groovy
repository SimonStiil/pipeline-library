#!/usr/bin/env groovy
def call(Map properties) {
    // Get bearer token from GHCR using GitHub PAT
    if (!properties.credentialId){
        properties.credentialId = "github-login-secret"
    }
    if (!properties.debug){
        properties.debug = false
    }
    if (!properties.quiet){
        properties.quiet = true
    }
    withCredentials([usernamePassword(credentialsId: properties.credentialId,
            usernameVariable: 'GITHUB_USERNAME',
            passwordVariable: 'GITHUB_PERSONAL')]) {
        
        def scope = "repository:${properties.organization}/${properties.PACKAGE_NAME}:pull"
        
        def response = httpRequest(
            customHeaders: [
                [name: 'Authorization', value: "Bearer ${GITHUB_PERSONAL}"]
            ],
            url: "https://ghcr.io/token?scope=${scope}",
            consoleLogResponseBody: properties.debug,
            quiet: properties.quiet,
            wrapAsMultipart: false
        )
        
        def tokenResponse = readJSON text: response.content
        return tokenResponse.token
    }
}