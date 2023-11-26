#!/usr/bin/env groovy
def call(Map properties) {
    if (!properties.credentialId){
        properties.credentialId = "github-login-secret"
    }
    if (!properties.debug){
        properties.debug = false
    }
    if (!properties.quiet){
        properties.quiet = true
    }
    // https://docs.github.com/en/rest/packages/packages?apiVersion=2022-11-28#list-package-versions-for-a-package-owned-by-a-user
    withCredentials([usernamePassword(credentialsId: properties.credentialId,
            usernameVariable : 'GITHUB_USERNAME',
            passwordVariable: 'GITHUB_PERSONAL' )]) {
        def response = httpRequest customHeaders: [[name: 'Accept', value: 'application/vnd.github+json'],
                                                   [name: 'Authorization', value: 'Bearer ' + GITHUB_PERSONAL],
                                                   [name: 'X-GitHub-Api-Version', value: '2022-11-28']],
                url: "https://api.github.com/users/${properties.organization}/packages/${properties.PACKAGE_TYPE}/${properties.PACKAGE_NAME}/versions",
                consoleLogResponseBody: properties.debug,
                quiet: properties.quiet,
                wrapAsMultipart: false
        def jsonResponse = readJSON text: response.content
        return jsonResponse
    }
}