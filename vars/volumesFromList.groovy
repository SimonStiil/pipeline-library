#!/usr/bin/env groovy
boolean call(List value) {
  def volumeMounts = ""
  for (value in value){
      def workingValue = value
      if (workingValue.startsWith('.')){
          workingValue = workingValue.replaceFirst(".", env.WORKSPACE)
      }
      volumeMounts+= "-v " + workingValue + " "
  }
  return volumeMounts.trim()
}
