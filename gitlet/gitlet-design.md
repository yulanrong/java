# Gitlet Design Document

**Name**: Yulan Rong

## Classes and Data Structures
### Commit
deal with staging and commit, save contents and take the current environment as a snap shot. 
### Gitlet
the core class of this project. It contains all the required functions and methods of gitlet. 
### main
read the commands and execute specific command.

## Algorithms
#### Commit Class
1. Commit(): The class constructor. It stores all the files and contents(include tracked and untracked) the user made. Each commit is a node of linkedlist. Commit has it's unique sha id, parent commit, author and committer(can be different people), created date and time, and commit message. The latest commit(where we are working off) is the head.
2. Log(): it prints out all info and metadata of parent commits of the current commit. 
3. getTime(): return the date and time that this commit created.
4. getID(): return the commit sha id of this commit
5. getMessage(): return the commit message of this commit.
#### Gitlet
1. init(): initialize gitlet directory if it doesn't exist. creates empty folders for tracked and untracked files and contents for staging. 

2. add(String fileName): add the untracked files into staging. create, modify, or delete files and contents are in untracked files until call add to stage it.

3. log(): prints all the commit message by time from the current commit until the initial commit.

4. globalLog(): prints information about all commits ever made. The order of the commits does not matter.
 
5. status(): prints out info in the format of following: Branches, Staged Files, Removed Files, Modifications Not Staged For Commit, Untracked Files

6. find(String commitMessage): Prints out the ids of all commits that have the given commit message, one per line. If there are multiple such commits, it prints the ids out on separate lines.

7. commit(String message): save the snapshot of the current commit

8. rm(String fileName): remove the file by its filename

9. checkout(String... args): it has three main methods: checkout(filename), checkout(commitID, filename), and checkout(branchname). 

10. branch(String branchName): create a new branch  with the given name, and points it at the current head node. A branch is nothing more than a name for a reference (a SHA-1 identifier) to a commit node. This command does NOT immediately switch to the newly created branch

11. rmBranch(String branchName): remove the branch by its name. This only means to delete the pointer associated with the branch; it does not mean to delete all commits that were created under the branch, or anything like that.

12. reset(String commitID): Checks out all the files tracked by the given commit. Removes tracked files that are not present in that commit. Also moves the current branch's head to that commit node.

13. merge(String branchName): merge branch to current branch
## Persistence
Runtime of each required method:
1. init: constant

2. add: worst case is linear relative to the size of the file being added.

3. commit: constant, no worse than linear with respect to the total size of files the commit is tracking. 

4. rm: constant

5. log: linear with respect to the number of nodes in head's history.

6. globalLog: Linear with respect to the number of commits ever made.

7. find:  linear relative to the number of commits.

8. status: depends only on the amount of data in the working directory plus the number of files staged to be added or deleted plus the number of branches.

9. checkout: linear relative to the size of the file being checked out.

10. branch: constant

11. rmBranch: constant

12. reset: linear with respect to the total size of files tracked by the given commit's snapshot.

13. merge: O(NlgN+D), where N is the total number of ancestor commits for the two branches and D is the total amount of data in all the files under these commits.