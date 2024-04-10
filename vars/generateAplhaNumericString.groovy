#!/usr/bin/env groovy
def call(int pass_length) {
    Random rand = new Random(System.currentTimeMillis());
    def pool = ['a'..'z','A'..'Z','0'..'9'].flatten();
    char[] passChars = new char[pass_length]
    for (int i = 0 ; i < pass_length ; i++ ) {
        passChars[i] = pool[rand.nextInt(pool.size())]
    }
    return new String(passChars);
}
