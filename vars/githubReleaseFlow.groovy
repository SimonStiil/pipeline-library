#!/usr/bin/env groovy
def call(Map data) {
/* Required:
 *   fileName
 *   repository
 *   commit
 *   release (tag name)
 * Optional : 
 *   filePath
 *   credentialId
 *   organization
 *   debug
 *   quiet
 
 */ 
  def response = githubGetRelease data
  if (response.status == 404){
    response = githubCreateRelease data
  }
  def jsonResponse = readJSON text: response.content
  data["url"] = jsonResponse["upload_url"]
  response = githubUploadAsset data
  
  jsonResponse = readJSON text: response.content
  return jsonResponse["browser_download_url"]
}
  