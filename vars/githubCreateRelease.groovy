#!/usr/bin/env groovy
def call(Map data) {
  // https://docs.github.com/en/rest/releases/releases?apiVersion=2022-11-28#create-a-release
  String credentialId = (data.credentialId) ? (data.credentialId) : "github-login-secret"
  String organization = (data.organization) ? (data.organization) : "SimonStiil"
  boolean debug = (data.debug) ? (data.debug) : false
  boolean quiet = (data.quiet) ? (data.quiet) : false
  def host = "https://api.github.com/repos/"+organization+"/"+data.repository+"/releases"
  def requestBody = '''{"tag_name":"%s",
  "target_commitish":"%s",
  "name":"Jenkins %s",
  "body":"Auto Generated Release from Git Tag",
  "draft":false,
  "prerelease":false,
  "generate_release_notes":true
}'''
  requestBody = String.format(requestBody, data.release, data.commit, data.release) 
  
  def response = httpRequest acceptType: "APPLICATION_JSON",
      httpMode: "POST",
      contentType: "APPLICATION_JSON",
      authentication: credentialId,
      customHeaders: [[name: 'X-GitHub-Api-Version', value: '2022-11-28']],
      consoleLogResponseBody: debug,
      quiet: quiet,
      requestBody: requestBody,
      url: host,
      validResponseCodes: "201" //"201,404,422"
  return response;
}
