#!/usr/bin/env groovy
def call(Map data) {
/* Required:
 *   github_event
 *   repository
 *   repository_short
 * Optional :
 *   branch
 *   credentialId
 *   organization
 */
    if (!data.credentialId){
        data.credentialId = "dockerhub-login-secret"
    }
    if (!data.organization){
        data.organization = data.PACKAGE_DESTINATION.replace("docker.io/","")
    }
    if (data.branch && data.github_event == "delete") {
        data.token = dockerhubLogin data
        def packageVersions = dockerhubListTags data
        echo "Contains "+ packageVersions['count'] + " tags."
        config.version = data.branch
        for (item in packageVersions['results']){
            if (item['name'] == data.branch ) {
                if (dockerhubDeleteTag(data)) {
                    echo "Deleted ${data.PACKAGE_DESTINATION}/${data.PACKAGE_TYPE}:${data.branch}"
                }
            }
        }
    } else {
        echo "Not a delete event, branch: ${data.branch}, data: ${data.github_event}"
    }
}
