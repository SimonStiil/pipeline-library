#!/usr/bin/env groovy
def call(Map data) {
/*
Required:
    gitMap: map from scmGetOrgRepo
Optional:
    credentialId
    webhookTokenId
 */
    if (data.gitMap.host == "github.com") {
        webhookList = githubWebhookList(data)

        echo "webhookList: " + webhookList.toString()
        if (webhookList == null) {
            echo "WebHook: Repository host ${data.gitMap.host} is not github.com"
            return null
        }
        if (!data.webhookTokenId){
            data.webhookTokenId = "generic-webhook-token"
        }
        withCredentials([string(credentialsId: data.webhookTokenId,
                variable: 'TOKEN')]) {
            data.url = env.JENKINS_URL + "generic-webhook-trigger/invoke?token=" + TOKEN
            data.events = ["delete", "push"]
            // https://docs.github.com/en/rest/repos/webhooks?apiVersion=2022-11-28
            def configFound = false
            for (webhook in webhookList) {
                if (webhook.config.url.contains(data.url)) {
                    configFound = true
                    results = compareLists(webhook.events, events)
                    def update = false
                    if (results.append.size() == 0) {
                        echo "WebHook: Events missing"
                        update = true
                    }
                    if (results.delete.size() == 0) {
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
                githubWebhookCreate(data)
                echo "WebHook: created"
            }
        }
    } else {
        echo "WebHook: Repository host ${data.gitMap.host} is not github.com"
    }
}