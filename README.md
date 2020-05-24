# Module-Builder

## Scripting Language
The scripting language uses the following notation:
Terms in square brackets can occur one or more times, round brackets indicate that this term is optional, the logic operators are used as normal. 
Mandatory and fixed parts of the language are written in bold writing.

Names cannot be a keyword or built-in function or an already used name.
The constructs are effective in the way they are defined.
If for example variables are used in a submodule, they have to be defined before the submodules are defined, otherwise the verification will stop if one of these variables is used.

The language first executes the rule in the parent module, then by default calls the submodules. However, the programmer can influence this behaviour.


### Blocks
A block is always declared with **BEGIN** name **:** [(Expression)|(Statement)] **end**.
After a colon or an end-term a newline shall follow.
The keywords are described in this section.

#### Module

**BEGIN MODULE** moduleName **:** [Function|Variables|Rule|Interface|Submodules] **end**

One build.module file has to contain exactly one module!

#### Function

**BEGIN FUNCTION** functionName (**IS LOCAL**) **:** [Expression|Statement] **end**

Functions return nothing, but they can manipulate variables.
Functions can be inherited by submodules, if not declared as local.
A inherited function is declared as: **BEGIN FUNCTION** functionName **IS INHERITED end**
A function is called by name followed by open and close braket.
The identifiers are after the name to enhance extendability, this way the verifier can check which custom verifier to call without parsing the identifiers.

#### Variables

**BEGIN** Variables **:** [Expression] **end**
This block is used to declare and possibly define variables.
A module shall contain one or non of these blocks. 
The block has to be defined before variables in it are used!

#### Rule

**BEGIN RULE** RuleName **DEPENDENCY** [ruleName|Expression ([**,** ruleName|Expression])] **:** [Expression|Statement] **end**

If a rule of a module is called either by a command line call or by a higher level module also the rules with the same name of the submodules are called automatically. The rules of the submodules shall be executed after the rule of the parent module has been executed.
If another rule shall be executed in addition the programmer has to call this rule explicitly by stating moduleName.RuleName .

In the Dependency part it can be specified which rules or criteria has to be fulfilled before the rule is executed.
Rules stated here are only executed in this level. Rules with the same name located in submodules are not executed automatically.
A rule is called by name and open and closing brakets.

#### Interface

**BEGIN** Interface **:** [Interfacefilename**Linebreak**] **end**

Defines the interface of this module, only these files shall be used as includes in other modules.
A module can contain max. one of this blocks.

#### Submodules

**BEGIN** Submodules **:** [SubmoduleRelativePath**Linebreak**] **end**

The submodules are called in the order they are defined in this block. Each module can only contain one block.
The submodules have to be subdirectories of the module that includes them as submodules! This shall enforce a clean design. Basically, it shall be sufficient to give the folder name of the submodule or the relative path to this directory, if the modules themselves are grouped in larger module groups by the directory structure.

### Built-in Functions
The functions that are provided by the FunctionCore are the following:

* DIREXISTS(absolutePath): tests if a directory exists
* FILEEXISTS(absolutePath): tests if a file exists
* COPYFILE(DestPath,SrcPath): copy a file at a given path to a new location, including the file name.
* MOVEFILE(DestPath,SrcPath): move a file to a new location, including the file name.
* MODULENAME: returns the name of the current module
* MODULEINTERFACE(modulename): returns the interface string of a module.
* PATH: Absolute path to the directory of the current build.module file
* EXECUTE(command): Executes a command via shell.
* EXECUTE(program, arg): Execute a program with the given arguments, e.g. a compiler.
* PRINT(message): Prints to the std output.
* CREATEDIR(absolutePath): Create a directory.
* DELETEDIR(absolutePath): Delete a directory.
* CREATEFILE(absolutePath): Create a file.
* DELETEFILE(absolutePath): Delete a file.
* SIZE(variableName): Determine the size of a collection.
* TIMESTAMP(absolutePath): returns the timestamp of the time the file was modified the last time. Returns 0 if the file doesn't exist. Two timestamps can be compared by the known comparators: <, >, ==, <= and >=. 


Besides this the language provides a hugh variety of string manipulation functions, since the main purpose of this language is string manipulation. 

The language provides these string manipulation functions:
* CONCAT(string1,string2): concatenates two strings and returns the new string.
* SPLIT(string,delimiter): takes a string and a delimiter string and splits the string whenever the delimiter occurs. The delimiter is removed.
* REPLACE: replaces every occurrence of a string by another string.
* SUBSTRING(string,index1,index2): return the string between two indices: [start,end). If start or end are out of scope, an empty string is returned.
* INDEXOF(string,indexString): returns the first index of the occurrence of a given string in another string. If no index can be found, the functions returns 0.
If the given variable is a array, the operation is applied to all elements separately, except for INDEXOF and SPLIT. These operations only works with scalar types. 

### Data Types
The language only knows strings because any other data type is not required and the language doesn't support arithmetic operations anyhow. The define a string no quotation marks are required.
However, for compare operations the language implicitly contains bool values, however this data type is not exposed to the programmer.

#### Collections
Collections can be created like scalar variables. The separator between the elements are newlines. This will force the programmer to produce better readable code. Leading white spaces are ignored.
Hence, the elements of the collection can contain white spaces.

### Identifier
Can be used when declaring a block, e.g. variable or constant, as predescriptor.
* INPUT: Variable is set by command line parameters by NAME=VALUE. The assigned value in the code can be used as default if no corresponding command line input is provided.
* LOCAL: Block, e.g. Variable, function or rule, is not inherited, only local to this particular module.
* UPDATE: Pushs changes to this variable to the higher parent build.module hierarchy. Just a optional identifier. A block can also ignore this identifier.
* INHERITED: Inherited variable or function from parent module. Can be declared in the module that wants to inherit a block. Basically, it just indicates for the programmer that this block has been declared in a predecessor module. However, the implementation can ignore this.
* PUBLIC: Default identifier of variables and functions. This indicates that they can be inherited by childs and also updated by them.

Variables that are not marked with INHERITED hide variables with the same name from the parent module.
The command line parameters can be obtained from the core.

### Statements
This section shows the statements provided by this script language.

#### If

**if** expression **then** [statement|expression] (**else** [statement|expression]) **end**

##### For-Statement

**for var** iteratorName **in** CollectionName **:** [expression|statement] **end**


### Expressions
This language only supports variable assignments by **:=** and constant assignments by **=** and **+=** to add to a variable. If to an existing variable something is added a scalar extends to an array, in case of an array, another element is added.
Additionally, the language provides the common logic operators **==** and **!=** to compare two variables.
Like mentioned above, the language also supports the common operations to compare numbers like timestamps.
Expressions can also be constructed by strings, e.g. build commands, that will be executed by the shell implicitly. No extra call of EXECUTE is necessary. This will be handled by the interpreter module that implements the expressions functionality.
Expressions are terminated with a newline except they are located in a statement or dependency declaration.

An array can be accessed by name **[**index**]**.
Expressions are terminated by a semicolon (**;**).

If nothing is assigned to a variable by the operator **:=** the variable is cleared instead.