#!/usr/bin/env groovy
def call(Map data) {
/*
Required:
    gitMap: map from scmGetOrgRepo
Optional:
    credentialId
    webhookTokenId
    debug: dafaults to false
    quiet: defaults to true
 */
    if (!data.debug){
        data.debug = false
    }
    if (!data.quiet){
        data.quiet = true
    }
    if (data.gitMap.host == "github.com") {
        webhookList = githubWebhookList(data)
        if (webhookList == null) {
            echo "WebHook: Repository host ${data.gitMap.host} is not github.com"
            return null
        }
        if (!data.webhookTokenId){
            data.webhookTokenId = "generic-webhook-token"
        }
        withCredentials([string(credentialsId: data.webhookTokenId,
                variable: 'TOKEN')]) {
            url = env.JENKINS_URL + "generic-webhook-trigger/invoke?token=" + TOKEN
            data.events = ["delete", "push", "pull_request"]
            // https://docs.github.com/en/rest/repos/webhooks?apiVersion=2022-11-28
            def configFound = false
            for (webhook in webhookList) {
                if (webhook.config.url.contains(url)) {
                    configFound = true
                    results = compareLists(webhook.events, data.events)
                    def update = false
                    if (results.append.size() != 0) {
                        echo "WebHook: Events missing"
                        update = true
                    }
                    if (results.delete.size() != 0) {
                        echo "WebHook: Events misconfigured"
                        update = true
                    }
                    if (update) {
                        data.hookId = webhook.id
                        githubWebhookUpdate(data)
                        echo "WebHook: Webhook events updated"
                    } else {
                        echo "WebHook: Already configured"
                    }
                }
            }
            if (!configFound) {
                echo "WebHook: No config found"
                data.url = url
                githubWebhookCreate(data)
                echo "WebHook: created"
            }
        }
    } else {
        echo "WebHook: Repository host ${data.gitMap.host} is not github.com"
    }
}