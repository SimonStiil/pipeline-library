#!/usr/bin/env groovy
boolean call(List listValue) {
  def volumeMounts = ""
  for (value in listValue){
      def workingValue = value
      if (workingValue.startsWith('.')){
          workingValue = workingValue.replaceFirst(".", env.WORKSPACE)
      }
      volumeMounts+= "-v " + workingValue + " "
  }
  return volumeMounts.trim()
}
