MaldoFS
=====

Overview
--------
Personal Java learning project inspired by [Jimfs](https://github.com/google/jimfs). Check out
the CONCEPTS.md file to understand high level components to understand the code. 

Tools to Learn
---------

- ~~GitHub~~
- Java 14
    - ~~Switch statement~~
- ~~Google Java Code Style~~
- ~~Error Prone~~ 
- ~~Java Flight Recorder (when ready)~~
- Microbenchmarking (when ready)
- ArchUnit

Libraries to Learn
---------
- Guava
- ~~Truth~~


Requirements
------
- ~~Create FileSystem hierarchy with Paths~~
- ~~Create interactive REPL/shell for the FileSystem~~
- ~~Create a file from a String or Binary in Java~~
- ~~Copy, Move, Remove files and directories~~
- ~~Text Editor support~~
- ~~Load a file from the current FS into MaldoFS~~
- Save FS on Disk (Reload)

Demo
------
Run the __*MaldoREPL.java*__ main class to execute the REPL. As the REPL is initializing
it creates basic Linux directories in root. Type "help" in the prompt to see the available commands.

```shell
[MaldoFS] $ ls
home       tmp        sbin       etc        var        
usr        opt        bin        dev        
[MaldoFS] $ cd home
[MaldoFS] $ pwd
/home/
[MaldoFS] $ ls
maljos     
[MaldoFS] $ cd maljos
[MaldoFS] $ touch myfile.txt
[MaldoFS] $ ls
myfile.txt      
[MaldoFS] $ echo 'hellooooWorld' >> myfile.txt
[MaldoFS] $ ls -l
drwxr-xr-x  root root 13 Dec 19 12:06 myfile.txt
[MaldoFS] $ mv myfile.txt otherfile
[MaldoFS] $ ls
otherfile       
[MaldoFS] $ cp otherfile myfile.txt
[MaldoFS] $ ls
otherfile       myfile.txt      
[MaldoFS] $ cat otherfile
hellooooWorld
[MaldoFS] $ cat myfile.txt
hellooooWorld
[MaldoFS] $ vim myfile.txt
[MaldoFS] $ import /etc/nfs.conf /etc/nfs.conf
[MaldoFS] $ ls /etc
nfs.conf
[MaldoFS] $ export /home/maljos/myfile.txt /tmp/myfile.txt
[MaldoFS] $
``` 

Incompleteness
----
As this is a learning project, certain components remain unimplemented and can be
scoped for future enhancements. This applies to implementation of several interfaces, 
abstract classes, and unit testing.
 
The REPL emulates the Bash shell, implementing only the most basic
features to interact reasonably with the FS. Some local directory "smartness" is implemented,
however, some commands will require absolute paths. 

Next Tasks
----
- I/O
    - Finish implementing interfaces    
- TODOs  
- Read Jimfs implementation in detail     
- Clean up code
- Unit tests  

Future Enhancements
--------
- File Permissions
- Concurrency
- Export FS to Disk
- Import FS from Disk
