#!/usr/bin/env groovy
// File kept as legacy. Unused after finding "scmData = checkout scm"

boolean call(String remoteRepoURL) {
    def gitMap = [:]
    for (buildData in currentBuild.getRawBuild().getActions(hudson.plugins.git.util.BuildData)){
        remoteUrl = buildData.getRemoteUrls()[0]
        build = buildData.lastBuild
        if (build != null){
            gitMap[remoteUrl] = build.getRevision().getSha1().name()
        }
    }
    return gitMap[remoteRepoURL]
}
/* references
 * https://javadoc.jenkins.io/plugin/git/hudson/plugins/git/util/BuildData.html
 * https://github.com/jenkinsci/git-plugin/blob/master/src/main/java/hudson/plugins/git/util/BuildData.java
 * https://javadoc.jenkins.io/plugin/git/hudson/plugins/git/util/Build.html
 */