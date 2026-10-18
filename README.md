# Zap

**A tiny command line tool that compiles and runs a Java project with one short command.**

No build files. No plugins. No ceremony. Put your code in a folder, run `zap init`, then `zap run`.

![Java](https://img.shields.io/badge/Java-21%2B-orange)
![Build](https://img.shields.io/badge/build-Maven-blue)
![Dependencies](https://img.shields.io/badge/runtime%20dependencies-none-brightgreen)
![Status](https://img.shields.io/badge/status-early%20development-yellow)

```
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

## Table of contents

- [Why I built Zap](#why-i-built-zap)
- [What Zap does](#what-zap-does)
- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Quick start](#quick-start)
- [Commands](#commands)
- [Configuration file](#configuration-file)
- [How `run` works](#how-run-works)
- [Providing input to your program](#providing-input-to-your-program)
- [Main class selection](#main-class-selection)
- [Error messages and exit codes](#error-messages-and-exit-codes)
- [Safety guarantees](#safety-guarantees)
- [Architecture](#architecture)
- [Design decisions](#design-decisions)
- [Known limitations](#known-limitations)
- [Roadmap](#roadmap)
- [Building from source](#building-from-source)

---

## Why I built Zap

Compiling Java files manually was a nightmare for me. It felt like I was assembling NASA's systems, armed with a bunch of weird letters that looked like they came straight out of Ancient Greece.

Make one tiny change, and suddenly you're rewriting compilation commands with every class and method listed all over again. One wrong character, and the whole thing falls apart.

But when we're working in the terminal, we're usually experimenting: solving an algorithm problem, testing an idea, working through an exercise, or building a small command-line program.

I wanted to focus on what I was actually trying to build, not worry about which files to compile, which classes to include, or how to write the same ridiculously long command for the hundredth time.

So I built Zap.

**I suffered so you don't have to.**

---

## What Zap does

Running a small Java project from the terminal usually means typing a `javac` command with the right output directory and the right list of files, then a `java` command with the right class path and the right main class. Build tools solve this, but they also bring configuration files, plugins, and a learning curve that is too heavy for demos, exercises, and small tools.

Zap does only this:

```
find sources  ->  compile  ->  check the main method  ->  run
```

Everything else is a saved setting you can change with one command.

*Life's too short to hand-write compilation commands, man. Just use Zap.*

---

## Features

- **Two commands to start**: `zap init`, then `zap run`.
- **Automatic main class**: finds `Main.java` for you, or you can choose any class.
- **Verified before running**: the main class is loaded and checked for a valid `public static void main(String[] args)` before the program starts.
- **Scripted input**: feed answers to `Scanner` / `System.in` from the command line, one-time or saved, so you can re-run interactive programs without typing.
- **Program arguments**: pass `String[] args`, one-time or saved.
- **Clean builds**: the output directory is emptied before every compile, so deleted classes never linger.
- **Strict, friendly validation**: every configuration problem is reported at once, with a clear message.
- **Atomic config writes**: the configuration file is never left half-written.
- **Windows-safe**: sources are passed to the compiler through an argument file, so large projects do not hit the command line length limit.
- **No runtime dependencies**: JDK only.

---

## Requirements

| Requirement | Version |
|---|---|
| JDK (not just a JRE) | **21 or newer** |
| `javac` and `java` on your `PATH` | required (or set full paths in the config) |
| Maven | only to build Zap itself |

Check your setup:

```
java -version
javac -version
```

---

## Installation

### 1. Build the jar

```
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

```
chmod +x zap
```

Without a script, you can always run the jar directly:

```
java -jar target/zap.jar run
```

---

## Quick start

Create a project with this layout:

```
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

```
zap init
zap run
```

Output:

```
Hello from Zap!
```

That's it. Zap created `.zap/config.properties`, compiled your code into `.zap/classes`, found `Main.java`, verified it, and ran it.

---

## Commands

```
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

```
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

```
zap run --args -v      # args = ["-v"]
```

Values with spaces need quotes, as in any shell:

```
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
| `zap config --main <package.Class>` | set the entry point (name and source file are checked) |
| `zap config --main --reset` | go back to `auto` |
| `zap config --show` | print all current settings |
| `zap config --reset-all` | restore every setting to its default |

Every change is validated **before** it is written. A rejected change leaves the file untouched.

**Priority**: values given to `run` replace the saved ones for that run.

```
zap config --input Sara 30
zap run                       # uses Sara, 30
zap run --input Rick 999       # uses Rick, 999 (saved values are not merged)
```

### `zap help` and `zap version`

```
zap help
zap version
```

> `zap help <command>` (detailed help for one command) is planned and not available yet.

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

Notes:

- Write paths with forward slashes (`src/main/java`), even on Windows.
- A key that is **missing** uses its default. A key that is **present but blank** is an error.
- Numbered keys must be continuous from 0. A gap (`input.0`, `input.2`) is reported as an error instead of silently losing data.
- You can edit the file by hand, but `zap config` rewrites the whole file in a fixed order, so hand-written comments and unknown keys are not preserved.

If your sources are not in `src/main/java`, change one line:

```properties
source.directory=src
```

---

## How `run` works

```
 locate project root      walk up from the current folder to find .zap/config.properties
          │
          ▼
 resolve configuration    defaults + saved values, validated (all problems reported together)
          │
          ▼
 build project layout     absolute paths, output directory safety check
          │
          ▼
 discover sources         every .java file under source.directory
          │
          ▼
 resolve main class       auto -> find Main.java      manual -> use the configured class
          │
          ▼
 clean output directory   remove stale classes
          │
          ▼
 compile                  javac -encoding UTF-8 -d <output> @<argument file>
          │
          ▼
 verify main method       load the class, check: public static void main(String[])
          │
          ▼
 run                      java -cp <output> <MainClass> <args>   (working directory = project root)
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

```
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

Other guarantees:

- `init` never overwrites an existing configuration.
- A rejected configuration change never modifies the file.
- The configuration is written to a temporary file first and then moved into place, so a crash cannot leave a half-written file.
- Compiled classes are checked in an isolated class loader, **without running** the program's static initializers.

---

## Architecture

### Package layout

```
com.zap
├── Zap.java                    entry point: wiring, one error handler, one exit
│
├── cli/                        text -> command objects, user-facing output
│   ├── Command                 sealed interface, one record per command
│   ├── CommandParser           parses arguments, never touches the disk
│   ├── InitCommandHandler      `zap init`   (prints)
│   └── ConfigCommandHandler    `zap config` (prints)
│
├── config/                     reading, checking and writing settings
│   ├── ConfigKeys              key names, one place
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

```
Zap
 ├── cli        ->  project, config, exception
 ├── execution  ->  project, config, exception
 ├── project    ->  config, exception
 ├── config     ->  exception
 └── exception  ->  (nothing)
```

### The configuration pipeline

Three classes, three jobs:

```
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
8. **Paths are resolved once.** `ProjectLayout` is the only place where configuration paths become absolute paths, and where output safety is checked.
9. **No checked exceptions leak out of the engine.**
10. **No hidden defaults for an uninitialized project.** A project must be initialized on purpose.

---

## Known limitations

- Single source tree: no multi-module projects.
- No external libraries (class path is only your compiled output). Dependency support is not planned in the current design.
- Resources (`.properties`, images, ...) are not copied to the output directory.
- The main class check assumes that the file name matches the public class name and that folders match packages.
- Only the classic `public static void main(String[] args)` form is accepted. Newer instance `main` forms are not.
- `zap config` rewrites the whole configuration file; hand-written comments and unknown keys are lost.
- Resetting a setting writes its default value explicitly instead of removing the key.
- `zap help <command>` is not implemented yet.
- Automated tests are not written yet.

---

## Roadmap

- [x] `init`, `run`, `config`, `version`
- [x] saved and one-time inputs and arguments
- [x] automatic and manual main class, with verification
- [x] atomic configuration writes, output directory safety
- [ ] `zap help <command>`, generated from the command list
- [ ] unit tests: parser, validator, manager, writer, finder, verifier
- [ ] `--verbose` mode (show what Zap is doing)
- [ ] extra class path entries (external jars)
- [ ] resource files support

---

## Building from source

```
git clone <your-repository-url>
cd zap
mvn clean package
```

Result:

```
target/zap.jar
```

Run the tests (once they exist):

```
mvn test
```
