#!/usr/bin/env groovy
// File kept as legacy. Unused after finding "scmData = checkout scm"

boolean call() {
    def gitMap = [:]
    for (config in scm.getUserRemoteConfigs()){
        return config.getUrl()
    }
    return null
}
/* references
 * https://javadoc.jenkins.io/plugin/git/hudson/plugins/git/GitSCM.html
 * https://github.com/jenkinsci/git-plugin/blob/master/src/main/java/hudson/plugins/git/GitSCM.java
 * https://javadoc.jenkins.io/plugin/git/hudson/plugins/git/UserRemoteConfig.html
 */