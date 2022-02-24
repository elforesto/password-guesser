# password-guesser
A Java-based password guesser/cracker.

Takes a CRC32 value and brute force guesses the original value. Uses Java's Executor class for multi-threading, and creates a number of threads equal to the number of detected CPUs.

This was based on sample code as part of a class on operating system design. Feel free to use this as a starting point to understand how to implement multi-threading.

Note: This is first attempt amateur hour code. Use at your own risk. It "works", but I can make no guarantees that it does so well or correctly.
