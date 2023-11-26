#!/usr/bin/env groovy
boolean call(Map properties) {
    if (!properties.credentialId){
        properties.credentialId = "github-login-secret"
    }
    if (!properties.debug){
        properties.debug = false
    }
    if (!properties.quiet){
        properties.quiet = true
    }
    // https://docs.github.com/en/rest/packages/packages?apiVersion=2022-11-28#delete-package-version-for-a-user
    withCredentials([usernamePassword(credentialsId: properties.credentialId,
                                      usernameVariable : 'GITHUB_USERNAME',
                                      passwordVariable: 'GITHUB_PERSONAL' )]) {
        def response = httpRequest customHeaders: [[name: 'Accept', value: 'application/vnd.github+json'],
                                                   [name: 'Authorization', value: 'Bearer ' + GITHUB_PERSONAL],
                                                   [name: 'X-GitHub-Api-Version', value: '2022-11-28']],
                httpMode: 'DELETE',
                url: "https://api.github.com/users/${properties.organization}/packages/${properties.PACKAGE_TYPE}/${properties.PACKAGE_NAME}/versions/${properties.version}",
                consoleLogResponseBody: properties.debug,
                quiet: properties.quiet,
                wrapAsMultipart: false
        if (response.status != 204) {
            echo "Failed to delete ${properties.PACKAGE_TYPE} ${properties.organization}/${properties.PACKAGE_NAME} version ${properties.version}"
            return false
        }
    }
    return true
}
