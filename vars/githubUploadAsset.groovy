#!/usr/bin/env groovy
def call(Map data) {
  // https://docs.github.com/en/rest/releases/assets?apiVersion=2022-11-28#upload-a-release-asset
  String credentialId = (data.credentialId) ? (data.credentialId) : "github-login-secret"
  boolean debug = (data.debug) ? (data.debug) : false
  boolean quiet = (data.quiet) ? (data.quiet) : false
  String filePath = (data.filePath) ? (data.filePath) : "./"
  
  def response = httpRequest acceptType: "APPLICATION_JSON",
      httpMode: "POST",
	  contentType: "APPLICATION_OCTETSTREAM",
      authentication: credentialId,
	  customHeaders: [[name: 'X-GitHub-Api-Version', value: '2022-11-28']],
      consoleLogResponseBody: debug,
      quiet: quiet,
      url: data.url.replace("{?name,label}","?name="+ data.fileName),
      uploadFile: filePath+data.fileName,
      wrapAsMultipart: false,
      validResponseCodes: "201"
  return response;
}