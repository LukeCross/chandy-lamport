# chandy-lamport
A didactic program demonstrating the Chandy Lamport algorithm.

An implementation of the Chandy Lamport Snapshot algorithm that uses a simple Bank Transfer System. Each process owns a Bank Account and on a random timer transfers amounts to each other process. Each process starts with a balance of 500. We simulate a channel and latency by introducing a wait of three seconds before flushing the channels in order to be able to demonstrate the Chandy Lamport Snapshot algorithm.

When the processes are running and transferring amounts in between themselves the user can press enter on a process and it will initiate the snapshot. This kicks off the snapshot algorithm and once completed the states of the processes and their channels is output to the user. To check whether this snapshot is correct the local states of all of the processes combined with what is in the channels must add to 500 * number of processes.

Written as part of a team of three with Dhruvil Tank (https://github.com/dhruviltank) and Barnaby Colby (https://github.com/barnabycolby).
