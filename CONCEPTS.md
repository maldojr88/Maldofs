Filesystem
-----
A system to store how data is stored, organized and retrieved. Typcially, filesystems
are represented as a hierarchy of directories and files contained within them.

File
-----
A file is a collection of data stored in one unit.

I/O (Input/Output)
----
I/O is the communication between information processing systems. Communication (reading/writing)
is the mechanism by which information flows into and out of a file.



Channel 
----
A channel represents an open connection to an entity such as a hardware
device, a file, a network socket, or a program component that is capable of
performing one or more distinct I/O operations, for example reading or
writing.

OutputStream
-----
A stream is a sequence of data elements made available over time. In particular, An output stream 
accepts output bytes and sends them to some sink. A sink in computing is a class or function 
designed to receive incoming events from another object or function

Buffer
----
Temporary storage spot for a chunk of data that is being transferred from one place to another



FileSystemProvider
-----
The kernel of the FileSystem. This is the entry point of most of the functionality.

Path
-----
An object that may be used to locate a file in a file system. In effect, a path works conceptually
like an physical address.