#!/usr/bin/env groovy
def call(Map data) {
  // https://docs.github.com/en/rest/releases/releases?apiVersion=2022-11-28#get-a-release-by-tag-name
  String credentialId = (data.credentialId) ? (data.credentialId) : "github-login-secret"
  String organization = (data.organization) ? (data.organization) : "SimonStiil"
  boolean debug = (data.debug) ? (data.debug) : false
  boolean quiet = (data.quiet) ? (data.quiet) : false
  def host = "https://api.github.com/repos/"+organization+"/"+data.repository+"/releases/tags/"+data.release
  def response = httpRequest acceptType: "APPLICATION_JSON",
      authentication: "github-login-secret",
      url: host,
	  customHeaders: [[name: 'X-GitHub-Api-Version', value: '2022-11-28']],
      consoleLogResponseBody: debug,
      quiet: quiet,
      validResponseCodes: "200,404,"
	println response.getClass().getName()
  return response;
}
