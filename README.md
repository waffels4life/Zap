<p align="center">
  <img src="assets/zap-logo.png" alt="Zap Logo" width="190" style="border-radius: 6px">
</p>

<h1 align="center">Zap</h1>

<p align="center">
A tiny command line tool that compiles and runs a Java project with one short command.
</p>

---

No build files. No plugins. No ceremony. Put your code in a folder, run `zap init`, then `zap run`.


![Java](https://img.shields.io/badge/Java-22%2B-orange)
![Build](https://img.shields.io/badge/build-Maven-blue)
![Dependencies](https://img.shields.io/badge/runtime%20dependencies-none-brightgreen)
![Status](https://img.shields.io/badge/status-early%20development-yellow)

```text
$ zap init
Zap initialized successfully.
Configuration: /home/me/demo/.zap/config.properties
Main class: auto
Run `zap help` to see available commands.

$ zap run --input Oreo 4
args: []
Name? Age? Hello Oreo, age 4
```

---

## The story: why I built this

Compiling Java by hand was a nightmare for me. It felt like I was hacking into NASA, surrounded by a wall of strange symbols that looked like they came straight out of ancient Greece. One tiny change, and I had to type every class out again, over and over. And one wrong letter was enough to ruin everything.

When we work in the terminal, we are either testing something, like coursework or an algorithm, or actually writing small terminal programs. And in those moments, I don't want to worry about *what* I need to compile, or *which* files to list. I just want to do my work in peace.

So I built Zap.

I suffered so you don't have to.

---

## Table of contents

- [The story: why I built this](#the-story-why-i-built-this)
- [Why Zap](#why-zap)
- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Quick start](#quick-start)
- [Commands](#commands)
- [Configuration file](#configuration-file)
- [How `run` works](#how-run-works)
- [Providing input to your program](#providing-input-to-your-program)
- [Main class selection](#main-class-selection)
- [Using external libraries](#using-external-libraries)
- [Error messages and exit codes](#error-messages-and-exit-codes)
- [Safety guarantees](#safety-guarantees)
- [Architecture](#architecture)
- [Design decisions](#design-decisions)
- [Known limitations](#known-limitations)
- [Roadmap](#roadmap)
- [Building from source](#building-from-source)

---

## Why Zap

Running a small Java project from the terminal usually means typing a `javac` command with the right output directory, the right list of source files, and the correct class path. Then you need another `java` command with the right class path and main class.

Build tools solve this, but they also bring configuration files, plugins, and a learning curve that is too heavy for demos, exercises, and small tools.

Zap does only this:

```text
find sources  ->  resolve dependencies  ->  compile
             ->  verify main class     ->  run
```

Everything else is a saved setting you can change with one command.

---

## Features

- **Two commands to start**: `zap init`, then `zap run`.
- **Automatic main class**: finds `Main.java` for you, or you can choose any class.
- **External libraries**: add JAR files, ZIP files, or directories to the class path.
- **Wildcard support**: use `lib/*` to include every JAR directly inside a directory.
- **Verified before running**: the main class is loaded and checked for a valid `public static void main(String[] args)` before the program starts.
- **Consistent class path**: external libraries are available during compilation, main class verification, and runtime execution.
- **Scripted input**: feed answers to `Scanner` / `System.in` from the command line, one-time or saved, so you can re-run interactive programs without typing.
- **Program arguments**: pass `String[] args`, one-time or saved.
- **Clean builds**: the output directory is emptied before every compile, so deleted classes never linger.
- **Built-in help**: `zap help` and `zap help <command>`, generated from one catalog, so the help never disagrees with itself.
- **Strict, friendly validation**: every configuration problem is reported at once, with a clear message.
- **Atomic config writes**: the configuration file is never left half-written.
- **Windows-safe compilation**: source files and class path entries are passed to the compiler through an argument file, avoiding command-line length issues during compilation.
- **No runtime dependencies**: Zap itself requires only a JDK. External libraries are optional and supplied by the user.

---

## Requirements

| Requirement | Version |
|---|---|
| JDK (not just a JRE) | **21 or newer** |
| `javac` and `java` on your `PATH` | required (or set full paths in the config) |
| Maven | only to build Zap itself |

Check your setup:

```text
java -version
javac -version
```

---

## Installation

### 1. Build the jar

```text
mvn clean package
```

This creates `target/zap.jar`.

### 2. Create a short command

**Windows** — create a file named `zap.bat` in a folder that is on your `PATH`:

```bat
@echo off
java -jar "C:\path\to\zap\target\zap.jar" %*
```

**Linux / macOS** — create an executable script named `zap` in a folder that is on your `PATH`:

```sh
#!/bin/sh
exec java -jar "/path/to/zap/target/zap.jar" "$@"
```

```text
chmod +x zap
```

Without a script, you can always run the jar directly:

```text
java -jar target/zap.jar run
```

---

## Quick start

Create a project with this layout:

```text
demo/
└── src/
    └── main/
        └── java/
            └── Main.java
```

```java
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello from Zap!");
    }
}
```

Then, inside `demo/`:

```text
zap init
zap run
```

Output:

```text
Hello from Zap!
```

That's it. Zap created `.zap/config.properties`, compiled your code into `.zap/classes`, found `Main.java`, verified it, and ran it.

---

## Commands

```text
zap init [auto | <package.Class>]
zap run [--input <v>...] [--args <a>...]
zap config <option>
zap help [<command>]
zap version
```

Short forms: `-r` (run), `-h` (help), `-v` (version).

### `zap init`

Initializes Zap in the **current directory**. Creates `.zap/config.properties`.

| Form | Meaning |
|---|---|
| `zap init` | defaults, main class = `auto` |
| `zap init auto` | same as above |
| `zap init com.app.Start` | use this class as the entry point |

When a class name is given, Zap checks that it is a valid fully qualified name **and** that its source file exists. If any check fails, nothing is written. Running `init` in an already initialized project is an error and never overwrites anything.

### `zap run` (`-r`)

Compiles and runs the project. Works from the project root or from any folder inside it.

```text
zap run
zap run --input 1 yes "hello world"
zap run --args --verbose file.txt
zap run --input Ali 20 --args --debug
```

| Option | Meaning |
|---|---|
| `--input <v>...` | lines fed to the program's standard input, for this run only |
| `--args <a>...` | program arguments (`String[] args`), for this run only |

Values after an option continue until the next known option. That means a value that looks like an option, such as `-v`, is kept as a value:

```text
zap run --args -v      # args = ["-v"]
```

Values with spaces need quotes, as in any shell:

```text
zap run --input "hello world"
```

The exit code of your program is returned as Zap's exit code.

### `zap config`

Changes the saved settings. Exactly one option per command.

| Command | Effect |
|---|---|
| `zap config --input <v>...` | save the inputs used on every run |
| `zap config --input --reset` | clear saved inputs |
| `zap config --args <a>...` | save the program arguments used on every run |
| `zap config --args --reset` | clear saved arguments |
| `zap config --classpath <path>...` | save external class path entries |
| `zap config --classpath --reset` | clear saved class path entries |
| `zap config --main <package.Class>` | set the entry point (name and source file are checked) |
| `zap config --main --reset` | go back to `auto` |
| `zap config --show` | print all current settings |
| `zap config --reset-all` | restore every setting to its default |

Every change is validated **before** it is written. A rejected change leaves the file untouched.

**Priority**: values given to `run` replace the saved ones for that run.

```text
zap config --input Arsam 22
zap run                       # uses Arsam, 22
zap run --input Oreo 4        # uses Oreo, 4 (saved values are not merged)
```

### `zap help` and `zap version`

```text
zap help          # overview of all commands
zap help run      # detailed help for one command
zap help config
zap version
```

The overview and the detailed pages are generated from one command catalog, so they always match each other. Aliases work too: `zap help -r` shows the page of `run`.

Example:

```text
$ zap help run
zap run - Compile and run the project

Usage:
  zap run [--input <value>...] [--args <value>...]
  zap -r

Description:
  Compiles every .java file in the source directory, verifies
  the main class, and runs it. Works from the project root or
  from any folder inside it.
  Values given here replace the saved ones for this run only.
  The exit code of your program becomes the exit code of Zap.

Options:
  --input <value>...   Lines sent to the program's standard input
  --args <value>...    Program arguments (the String[] args)

Examples:
  zap run
  zap run --input Ali 20
  zap run --args --verbose data.txt
  zap run --input "hello world" --args -v

Aliases: -r
```

An unknown topic is a usage error (exit code `2`):

```text
$ zap help foo
Error: Unknown command: foo. Run `zap help`.
```

---

## Configuration file

Location: `.zap/config.properties` in the project root.

```properties
# Zap configuration
source.directory=src/main/java
compiler.executable=javac
output.directory=.zap/classes
runtime.executable=java
main.class=auto
```

| Key | Default | Description |
|---|---|---|
| `source.directory` | `src/main/java` | where your `.java` files are, relative to the project root |
| `compiler.executable` | `javac` | compiler command or full path |
| `output.directory` | `.zap/classes` | where compiled classes go (emptied before every compile) |
| `runtime.executable` | `java` | runtime command or full path |
| `main.class` | `auto` | `auto` or a fully qualified class name |
| `input.0`, `input.1`, ... | none | saved inputs, one line each |
| `args.0`, `args.1`, ... | none | saved program arguments |
| `classpath.0`, `classpath.1`, ... | none | external JAR, ZIP, directory, or wildcard entries |

Example:

```properties
classpath.0=lib/gson.jar
classpath.1=build/classes
classpath.2=lib/*
```

Notes:

- Write paths with forward slashes (`src/main/java`), even on Windows.
- Class path entries are relative to the project root.
- A key that is **missing** uses its default. A key that is **present but blank** is an error.
- Numbered keys must be continuous from 0. A gap (`input.0`, `input.2`) is reported as an error instead of silently losing data.
- Class path entries are checked when saved and again on every run. A path that no longer exists causes a clear error.
- You can edit the file by hand, but `zap config` rewrites the whole file in a fixed order, so hand-written comments and unknown keys are not preserved.

If your sources are not in `src/main/java`, change one line:

```properties
source.directory=src
```

---

## How `run` works

```text
 locate project root      walk up from the current folder to find .zap/config.properties
          │
          ▼
 resolve configuration    defaults + saved values, validated (all problems reported together)
          │
          ▼
 build project layout     absolute paths, output directory safety check
          │
          ▼
   discover sources       every .java file under source.directory
          │
          ▼
 resolve main class       auto -> find Main.java      manual -> use the configured class
          │
          ▼
 resolve class path       convert entries into existing absolute paths
          │
          ▼
 check class path safety  reject entries inside the output directory
          │
          ▼
 clean output directory   remove stale classes
          │
          ▼
        compile           javac -encoding UTF-8 -d <output> @<argument file>
                          external libraries are supplied through -classpath
          │
          ▼
 verify main method       load the class with the output and external libraries
                          check: public static void main(String[])
          │
          ▼
         run              java -cp <output><separator><libraries> <MainClass> <args>
                          working directory = project root
```

The main class is resolved **before** compiling, so a missing or ambiguous entry point fails fast.

---

## Providing input to your program

Zap feeds the inputs to your program's standard input, one per line, in order, and then closes the input.

```java
Scanner scanner = new Scanner(System.in);
String name = scanner.nextLine();
int age = scanner.nextInt();
```

```text
zap run --input Ali 20
```

Your program reads `Ali`, then `20`, exactly as if you had typed them. There is no prompt detection: the values are simply the lines of standard input, in order.

- **No inputs given**: your terminal is connected directly to the program (normal interactive mode).
- **Fewer inputs than the program asks for**: the program reaches the end of input, and fails the way any Java program does (for example `NoSuchElementException` from `Scanner`).
- **More inputs than the program reads**: the extra lines are ignored.
- Inputs are sent as UTF-8.

---

## Main class selection

| `main.class` | Behavior |
|---|---|
| `auto` | Zap searches `source.directory` for files named `Main.java`. The class name comes from the folder path (`com/app/Main.java` → `com.app.Main`). |
| `com.app.Start` | Zap uses exactly this class. No search. |

In `auto` mode:

| Search result | Behavior |
|---|---|
| exactly one `Main.java` | used |
| none | error: `No Main.java found in ...` |
| more than one | error listing every candidate, asking you to choose with `zap config --main` |

Zap never guesses between several candidates.

Package names are taken from folder names, so folders must match package declarations (the standard Java convention).

---

## Using external libraries

Zap supports external libraries through the Java class path. You can add individual JAR files, ZIP files, directories containing compiled classes, or every JAR directly inside a directory.

No Maven or Gradle dependency resolution is performed. You provide the library files yourself.

### Supported entries

| Entry | Meaning |
|---|---|
| `lib/gson.jar` | use one JAR file |
| `lib/library.zip` | use one ZIP archive |
| `build/classes` | use a directory containing compiled classes |
| `lib/*` | use every `.jar` file directly inside `lib` |

Wildcard entries are not recursive. For example, `lib/*` does not include JARs inside `lib/plugins/`.

JAR and ZIP files are added as individual class path entries. Directories are added as class path roots, allowing Java to find compiled classes under their package paths.

### Adding libraries

A typical project layout:

```text
demo/
├── lib/
│   └── annotations.jar
└── src/
    └── main/
        └── java/
            └── Main.java
```

Add the library:

```text
zap config --classpath lib/annotations.jar
```

Or include every JAR directly inside a directory:

```text
zap config --classpath "lib/*"
```

You can also add multiple entries in one command:

```text
zap config --classpath lib/gson.jar lib/annotations.jar build/classes
```

Check the saved configuration:

```text
zap config --show
```

Example output:

```text
classpath.0=lib/*
```

Run the project normally:

```text
zap run
```

The same resolved class path is used for compilation, main class verification, and runtime execution.

### Resetting the class path

```text
zap config --classpath --reset
```

This removes all saved external class path entries.

### Important notes

- Class path entries are resolved relative to the project root, not the current working directory.
- Entries must exist when saved and must still exist when the project runs.
- A wildcard such as `lib/*` must point to an existing directory.
- Wildcards include JAR files only, directly inside the specified directory.
- Entries inside the output directory are rejected because Zap empties that directory before compilation.
- Dependencies of your libraries are not discovered automatically. If a library requires another library, add that dependency yourself.
- Libraries are supplied to both the compiler and runtime. Compile-only libraries, such as annotation libraries, can safely remain on the runtime class path.

---

## Error messages and exit codes

### Exit codes

| Code | Meaning |
|---|---|
| `0` | success |
| `1` | Zap error (not initialized, invalid config, compilation failed, ...) |
| `2` | wrong usage (unknown command or option, missing value, ...) |
| `130` | interrupted |
| other | `zap run` returns your program's own exit code |

### Common messages

| Message | What to do |
|---|---|
| `Project is not initialized: <path>. Run \`zap init\` first.` | run `zap init` in your project |
| `Project is already initialized: <path>` | nothing; the project is ready (use `zap config` to change settings) |
| `No Main.java found in <dir>. Use \`zap config --main <package.Class>\`.` | add a `Main.java` or set the class explicitly |
| `Multiple Main.java files found: a.Main, b.Main. Choose one with \`zap config --main\`.` | pick one |
| `Source file for <class> not found. Expected: <path>` | check the class name and package folders |
| `Cannot run <class>: no public static void main(String[] args) method` | add or fix the main method |
| `Classpath entry not found: <entry>` | check the path and make sure it exists relative to the project root |
| `Class path entry is not a jar or a folder: <entry>` | use a JAR, ZIP, or directory |
| `Class path entry is inside the output directory` | move the library outside the output directory |
| `class could not be loaded (a dependency may be missing from the class path)` | check that all required libraries are included |
| `Compilation failed (exit code 1).` | fix the compiler errors printed above it |
| `Cannot start 'javac'. Is it installed and on PATH?` | install a JDK, or set `compiler.executable` |
| `Invalid configuration:` followed by a list | every problem is listed; fix them all |
| `Unknown command: foo. Run \`zap help\`.` | check spelling |

---

## Safety guarantees

Zap deletes the contents of the output directory before every compile. To make sure that can never touch your own files, the output directory is rejected if it:

- is outside the project, or **is** the project root,
- overlaps the source directory (inside it, or containing it),
- contains the Zap configuration file.

External class path entries are also checked. An entry inside the output directory is rejected because its contents would be deleted before compilation.

Other guarantees:

- `init` never overwrites an existing configuration.
- A rejected configuration change never modifies the file.
- The configuration is written to a temporary file first and then moved into place, so a crash cannot leave a half-written file.
- Compiled classes are checked in an isolated class loader, **without running** the program's static initializers.
- Class path entries are resolved and checked again before every run.

---

## Architecture

### Package layout

```text
com.zap
├── Zap.java                    entry point: wiring, one error handler, one exit
│
├── cli/                        text -> command objects, user-facing output
│   ├── Command                 sealed interface, one record per command
│   ├── CommandParser           parses arguments, never touches the disk
│   ├── InitCommandHandler      `zap init`   (prints)
│   ├── ConfigCommandHandler    `zap config` (prints)
│   ├── HelpCommandHandler      `zap help`   (prints)
│   └── help/                   one source of truth for all help text
│       ├── HelpCatalog               the texts of every command
│       ├── CommandDoc                usage, description, options, examples
│       ├── OptionDoc                 one option and its description
│       └── HelpFormatter             turns docs into text (returns a String)
│
├── config/                     reading, checking and writing settings
│   ├── ConfigKeys              key names, one place
│   ├── ClasspathEntries        class path entry normalization and syntax
│   ├── ConfigPaths             where the config file lives
│   ├── ConfigLoader            reads the file, nothing else
│   ├── ConfigValidator         reports problems, decides nothing
│   ├── ConfigWriter            atomic writes
│   ├── ConfigManager           the decision maker
│   └── ZapConfig               immutable, typed settings
│
├── project/                    understanding the user's project
│   ├── ProjectLocator          finds the project root
│   ├── ProjectLayout           absolute paths + output safety check
│   ├── ProjectInitializer      first-time setup
│   ├── SourceDiscovery         finds .java files
│   ├── ClasspathResolver       resolves and validates external class path entries
│   └── mainclass/
│       ├── MainClassFinder           searches for Main.java
│       ├── MainClassResolver         auto or configured
│       ├── MainClassSourceLocator    class name -> source file
│       ├── MainClassVerifier         checks the compiled main method
│       ├── MainClassSearchResult     Found / NotFound / Ambiguous
│       └── MainClassStatus
│
├── execution/                  the engine
│   ├── ProjectExecutor         orchestrates the whole run
│   ├── CompilerRunner          javac
│   ├── ProgramRunner           java
│   ├── ProcessRunner           the only place that starts processes
│   ├── OutputCleaner           empties the output directory
│   └── RunOptions              one-time inputs and args
│
└── exception/                  typed errors, no logic
    ├── ZapException            base of every Zap error
    ├── config/  project/  execution/  cli/
```

### Dependency rule

Dependencies point one way. There are no cycles.

```text
Zap
 ├── cli        ->  project, config, exception
 ├── execution  ->  project, config, exception
 ├── project    ->  config, exception
 ├── config     ->  exception
 └── exception  ->  (nothing)
```

### The configuration pipeline

Three classes, three jobs:

```text
ConfigLoader      Is the file there? Can it be read?                 (I/O only)
ConfigValidator   Are the values valid?                              (reports a list, never decides)
ConfigManager     What should happen next?                           (the brain)
```

What the manager decides:

| Situation | Decision |
|---|---|
| config file missing | `ProjectNotInitializedException` |
| config file unreadable or malformed | error, no fallback |
| key missing in the file | use the default |
| key present but blank or invalid | `InvalidConfigurationException` with **all** problems |
| any change requested | apply, validate the **result**, then write atomically |

---

## Design decisions

1. **One job per class.** Loading, validating, deciding, running, and printing are separate classes.
2. **Lower layers throw, the top layer handles.** Nothing below `Zap.main` prints an error or exits. Every failure is a typed `ZapException`, caught in one place that chooses the exit code.
3. **Only handlers print.** The engine and the configuration code return results or throw. This keeps them testable.
4. **Validate the result, not the request.** Every configuration change is applied to an in-memory copy and checked as a whole before it is written.
5. **Report every problem at once.** The validator returns a list; the user fixes everything in one go.
6. **Immutable data.** `ZapConfig`, `RunOptions`, and the command objects are records with defensive copies.
7. **Sealed command model.** Adding a command without handling it is a compile error.
8. **Paths are resolved relative to the project root.** Project and class path entries are converted into normalized absolute paths before they are used.
9. **No checked exceptions leak out of the engine.**
10. **No hidden defaults for an uninitialized project.** A project must be initialized on purpose.
11. **One source of truth for help.** Help texts live in one catalog. The overview, every detail page and the aliases are built from it, and the formatter only returns strings, so it can be tested without printing.

---

## Known limitations

- Single source tree: no multi-module projects.
- No automatic dependency resolution. Zap does not download libraries or inspect their dependency metadata.
- Transitive dependencies must be added manually to the class path.
- Wildcard entries include only JAR files directly inside the specified directory; they are not recursive.
- Resource files (`.properties`, images, ...) are not copied to the output directory.
- The main class check assumes that the file name matches the public class name and that folders match packages.
- Only the classic `public static void main(String[] args)` form is accepted. Newer instance `main` forms are not.
- `zap config` rewrites the whole configuration file; hand-written comments and unknown keys are lost.
- Resetting a setting writes its default value explicitly instead of removing the key.
- The runtime class path is passed as a command-line argument. Extremely large class paths may eventually hit operating system command-line length limits, particularly on Windows.
- Automated tests are not written yet.

---

## Roadmap

- [x] `init`, `run`, `config`, `version`
- [x] saved and one-time inputs and arguments
- [x] automatic and manual main class, with verification
- [x] atomic configuration writes, output directory safety
- [x] `zap help <command>`, generated from one command catalog
- [x] external class path entries (JARs, ZIPs, directories, and wildcards)
- [ ] unit tests: parser, validator, manager, writer, finder, verifier, class path resolver
- [ ] `--verbose` mode (show what Zap is doing)
- [ ] resource files support
- [ ] improved runtime class path handling for very large dependency lists

---

## Building from source

```text
git clone https://github.com/waffels4life/Zap.git
cd Zap
mvn clean package
```

Result:

```text
target/zap.jar
```

Run the tests (once they exist):

```text
mvn test
```

---

_Life is too short to hand-write compilation commands. Just use Zap._

`-v 1.2.0 SNAPSHOT`
