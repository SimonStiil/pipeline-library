#!/usr/bin/env groovy
def <T> Map<String,List<T>> call(List<T> data, List<T> test) {
    ArrayList append = new ArrayList<T>()
    ArrayList delete = new ArrayList<T>()
    for (element in data) {
        def found = false
        for (testElement in test) {
            if (element == testElement) {
                found=true
                break
            }
        }
        if (!found){
            delete.add(element)
        }
    }
    for (testElement in test) {
        def found = false
        for (element in data) {
            if (element == testElement) {
                found=true
                break
            }
        }
        if (!found){
            append.add(testElement)
        }
    }
    HashMap results = new HashMap<String,ArrayList<T>>()
    results.put("append",append)
    results.put("delete",delete)
    return results
}
