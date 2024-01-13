# Problem Statement
Create an Android App that will help users organize their Files using Tags. 

Tag is an arbitrary value that can be associated with a file. For example - names of people,
places, events, etc.

Tags facilitate any logical and categorical grouping of files with a user-defined
name. A file can be associated with multiple tags, and a tag can be associated with multiple
files. 

### Objectives:
1. Users should be able to tag files in the Android system. They should also be
allowed to modify and delete the tags whenever necessary. Provide a simple and
intuitive experience to do the aforementioned.

2. Users should be able to locate tags and files under a tag easily.

3. Performance and efficiency - uninterrupted UI thread ensures smooth and quick
functioning on the front-end that the user interacts with; longer operations are
queued and performed on a separate thread in the background asynchronously.

4. Design choices - Although tags will be lost when files are shared outside the
application due to the restrictions on meta data access, users can move files freely
from within the application and the tags would remain the same. The same holds
true for renaming the files.

5. Robustness - All possible error generating code has been enclosed within try and
catch blocks and appropriate text views have been placed with descriptive
messages.


# Design choices

## 1. Approach History
We initially started with a very simple-to-implement primitive approach which
involved manipulation of the file names. We thought of a fixed length tag name (or
at an advanced stage, tag UID) concatenated with the file names which would help
retain the tags when the file is shared. But, evidently, we discarded the approach in
no time, without much thought, due to apparent problems like length of file names
with multiple tags, etc.


We then switched to manipulation of meta data along with a simple database. The
database would store information about the files for a tag, and the meta data could
store the tags for every that particular file. After some research, although we found
a few ways to add and view metadata for media files, we learnt about the
discontinuity of metadata access to developers in recent android versions and thus
had to stop proceeding with this approach as well.


Soon, we opted for a database-centric approach and when Smit asked us to
consider performing database operations using file operations, we looked into the
approach and estimated high implementation complexities, thus stuck with SQLite
database at the end. To ensure every action performed by the user was registered and executed, we
created an operations log that maintained a record of all actions and related
data for the long time consuming tasks so that the UI thread ran smoothly
without interruptions.

We chose to split the databases into a motley of in-memory and disk databases
after carefully evaluating the speed and memory trade-offs for both to exploit
the advantages respectively.

→ In-memory: TagUID to Tags; Files to TagUIDs

→ Disk: TagUIDs to files


## 2. Architecture (Design): 

### Saving Tags

  tag -> unique-id will be saved in one table.
    
  Primary key for tag -> unique-id table will be tag title.
  
  This unique id will be used instead of the tag name in the database.
  
  This data will be brought to memory as a HashMap during start up. 
  
**Advantages:**

  Searching will be performed in O(1) time
  
  Helps in reducing updates.

  Will take less space on the disk if the numbers are used to represent tags.


### Searching Files related to Tags

  Only one table is required for searching for the tags. 

  Primary key for Tag -> Files table will be UID (Unique-id) of the tags.
  
  Linked to foreign key in the above-mentioned table.
  
  **Case 1 : One Tag**

  Finding UID of tag from memory = 1

  Finding UID in table = log(n)

  Iterating over files to convert it into relevant object in memory = m

  **Case 2 : Multiple Tags**

  Finding UID of tag from memory = 1 Practically users won’t be searching for large
number of tags so we can consider the time as constant even if multiple tags are
there as n will be small.

  Finding UID in the table = log(n)

  Iterating over all the files tagged with these tags separately row after rows and
finding intersection or performing set operations = (m1 + m2 + m3 + m4 + …)


### Searching Tags related to Files

A table for maintaining file -> tags UID relation, where each record represents the
tags associated with a particular file in publicly accessible file system. 

This table will be primarily used to get the tags data for particular files that are
looked for or even during basic browsing through the file system. 

Primary key for File -> Tags table will be File's absolute path.

If a file is tagged with a single or even multiple tags, they will be saved in
the tags column as comma separated values.Ex. (“/dir1/dir2/file.ex”, “1,2,3”)

*absolute_path* will be used as primary key and thus will be indexed. SQLite
internally implements b-trees to maintain indices, similar to binary search, which
would give an average time complexity of O( logN )where N is the count of all of the
files tagged in the file system.


This table will need to have the index to be sorted which would pose a time
complexity of O(NlogN). But since the system would have heavy read operations and lighter write operations
so the latency between user interactions would be almost same. 


## Overview of Tables: 
<img width="300" alt="image" src="https://github.com/geee28/TagIt/assets/83246649/465bf979-8533-4c0f-a12b-4cd32599b978">

## Demo: 

*Landing page*

<img width = "300" alt="image" src="https://github.com/geee28/TagIt/assets/83246649/8a573662-319e-4ed3-a4d4-17ae8280bcb5">

*Navigating through the file tree under 'Files' and adding tags*

<img width = "300" alt="image" src="https://github.com/geee28/TagIt/assets/83246649/5c322268-c028-407b-b5da-dd219d32f399">

*Switching to the 'Tags' view*

<img width = "300" alt="image" src="https://github.com/geee28/TagIt/assets/83246649/fb80dd4d-12af-47ef-8731-f41d8bea6bee">

*Advanced filtering: Results of Homework5 **and** Question1*

<img width = "300" alt="image" src="https://github.com/geee28/TagIt/assets/83246649/c237fd91-b143-4f45-beec-ac92519d5d78">
<img width = "300" alt="image" src="https://github.com/geee28/TagIt/assets/83246649/9b4007b7-6957-44fd-8b7a-e2656cb503a2">

Contains each indicates the 'INTERSECT' operation; 

Contains indicates the 'UNION' operation; and 

Does not contains is for the 'NOT' operation. 

