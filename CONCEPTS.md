Basics
-----
#### Filesystem

A system to store how data is stored, organized and retrieved. Typically, filesystems
are represented as a hierarchy of directories and files contained within them.

#### File
A file is a collection of data stored in one unit.

#### I/O (Input/Output)
I/O is the communication between information processing systems. Communication (reading/writing)
is the mechanism by which information flows into and out of a file. I/O is said to be either block
oriented or stream oriented. File access is block oriented while reading from a keyboard or network
socket is stream oriented.

[Java I/O Tutorials](https://docs.oracle.com/javase/tutorial/essential/io/)

Java I/O Story
-----
#### Old I/O
##### OutputStream
A stream is a sequence of data elements made available over time. In particular, An output stream 
accepts output bytes and sends them to some sink. A sink in computing is a class or function 
designed to receive incoming events from another object or function.
##### RandomAccessFile 
Can be used to create a flat file database
##### FilterInputStream 
Streams for transforming streams (decrypt/encrypt, compress/decompress)
##### Reader/Writer
Manipulating character streams

#### New I/O
NIO package has several goals take advantage of modern OS I/O services to provide a more performant
and cleaner API. It mainly relies on channel based I/O as opposed to stream based I/O.

##### Buffer
Temporary storage spot for a chunk of data that is being transferred from one place to another. More
Java specific definition (from Javadocs):
"A container for data of a specific primitive type.
A buffer is a linear, finite sequence of elements of a specific primitive type. Aside from its 
content, the essential properties of a buffer are its capacity, limit, and position:
A buffer's capacity is the number of elements it contains. The capacity of a buffer is never 
negative and never changes.
A buffer's limit is the index of the first element that should not be read or written. A buffer's 
limit is never negative and is never greater than its capacity.
A buffer's position is the index of the next element to be read or written. A buffer's position 
is never negative and is never greater than its limit."

Flow: Application -> Buffer -> Channel -> OS

Typical Application flow:
1. buffer.put() // until you fill it up
2. buffer.flip()// prepare the buffer for draining
3. while(buffer.hasRemaining()){
    output.write(buffer.get())
}
4. buffer.clear()

Direct Buffer -> creates a buffer in the native heap (not in main JVM heap) to increase performance.

Disk (hardware) -> Disk Controller(DMA) -> OS Buffer -> Application (JVM) Buffer.

Because of the extra copy between OS Buffer -> JVM Buffer, newer I/O technique try to make the DMA
speak directly the Application buffer.

##### Channel 
- A channel represents an open connection to an entity such as a hardware device, a file, a network 
socket, or a program component that's capable of performing one or more distinct I/O operations, 
for example reading or writing.
- Typically, a channel has a 1-1 correspondence with a file descriptor
- Talk directly to the DMA to perform I/O more efficiently 
- FileChannel is an important class because it supports locking. (It is also used by DBMS's)

##### Selectors
- Enhance stream oriented processing by relying on the OS to help signal when I/O is ready 
- This allows for nonblocking I/O
- Modern OS's provide readiness selection through the "select()" POSIX call
- can examine one or more channels to determine which channels are ready for I/O

##### NIO-2
NIO provides *multiplexed* I/O (a combination of nonblocking I/O and readiness selection) to 
facilitate the creation of highly scalable servers.

NIO-2 provides *asynchronous* I/O which lets client code initiate an I/O operation and subsequently 
notifies the client when the operation is complete. Like multiplexed I/O, asynch I/O is also
commonly used to facilitate the creation of highly scalable servers. 

*NetworkChannel* is the main interface for Sockets


Java NIO - Main Classes/Interfaces
------
 
##### FileSystemProvider 
The kernel of the FileSystem. This is the entry point of most of the functionality.

##### Path
An object that may be used to locate a file in a file system. In effect, a path works conceptually
like an physical address.

##### Files

##### FileStore

##### 

##### FileAttributes

##### OpenOption

##### DirectoryStream

##### FileVisitor