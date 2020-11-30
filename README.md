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
- Java Flight Recorder (when ready)
- Microbenchmarking (when ready)
- ArchUnit
- Dagger (NOT APPLICABLE)

Libraries to Learn
---------
- Guava
- ~~Truth~~


Requirements
------
- ~~Create FileSystem hierarchy with Paths~~
- ~~Create interactive REPL/shell for the FileSystem~~
- Load a file from the current FS into MaldoFS
- Create a file from a String or Binary in Java
- Save FS on Disk (Reload)

Demo
------
Run the __*MaldoInteractive.java*__ main class to execute the REPL. As the REPL is initializing
it creates basic Linux directories in root. Type "help" in the prompt to see the available commands.

```shell
[MaldoFS] $ ls
home       tmp        sbin       etc        var        
usr        opt        bin        dev        
[MaldoFS] $ cd home
[MaldoFS] $ ls
maljos     
[MaldoFS] $ pwd
/home/
[MaldoFS] $ cd ..
[MaldoFS] $ ls -l
drwxr-xr-x  root root 52 Nov 28 10:37 home
drwxr-xr-x  root root 52 Nov 28 10:37 tmp
drwxr-xr-x  root root 52 Nov 28 10:37 sbin
drwxr-xr-x  root root 52 Nov 28 10:37 etc
drwxr-xr-x  root root 52 Nov 28 10:37 var
drwxr-xr-x  root root 52 Nov 28 10:37 usr
drwxr-xr-x  root root 52 Nov 28 10:37 opt
drwxr-xr-x  root root 52 Nov 28 10:37 bin
drwxr-xr-x  root root 52 Nov 28 10:37 dev
[MaldoFS] $ 
``` 

Next Tasks
----
- Implement operations on regular files    
    - Append to file
    - Cat file contents
    - Remove File
    - Copy file
    - Move file
    - Load Binary file from current OS
    - Simple text editor to update file?
- Fix file permissions AND file size in "ls -l"
- Creating, deleting, moving and copying files and directories
- Clean up code
- Unit tests  

Future Enhancements
--------
- File Permissions
- Concurrency
- Load to Disk
- Recreate from Disk
