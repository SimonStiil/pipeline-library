#!/usr/bin/env groovy
Object call(Map data) {
/*
Required:
    gitMap: map from scmGetOrgRepo
Optional:
    credentialId
 */
    if (!data.credentialId){
        data.credentialId = "github-login-secret"
    }
    Object jsonResponse
    if (data.gitMap.host == "github.com") {
        // https://docs.github.com/en/rest/repos/webhooks?apiVersion=2022-11-28
        // curl -L -H "Accept: application/vnd.github+json" -H "Authorization: Bearer $GITHUB_PERSONAL" -H "X-GitHub-Api-Version: 2022-11-28" https://api.github.com/repos/SimonStiil/keyvaluedatabase/hooks
        withCredentials([usernamePassword(credentialsId: data.credentialId,
                usernameVariable: 'GITHUB_USERNAME',
                passwordVariable: 'GITHUB_PERSONAL')]) {
            def response = httpRequest customHeaders: [[name: 'Accept', value: 'application/vnd.github+json'],
                                                       [name: 'Authorization', value: 'Bearer ' + GITHUB_PERSONAL],
                                                       [name: 'X-GitHub-Api-Version', value: '2022-11-28']],
                    url: "https://api.github.com/repos/${data.gitMap.fullName}/hooks"
            echo "githubWebhookList: " + response.content
            jsonResponse = readJSON text: response.content
            echo "githubWebhookList(json): " + jsonResponse.toString()
        }
    }
    return jsonResponse
}
