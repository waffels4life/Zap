package com.zap.execution;

import com.zap.project.ProjectLocator;
import com.zap.project.SourceDiscovery;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public final class ProjectExecutor {

    private final ProjectLocator projectLocator;
    private final SourceDiscovery sourceDiscovery;
    private final CompilerRunner compilerRunner;
    private final ProgramRunner programRunner;

    public ProjectExecutor() {
        this.projectLocator = new ProjectLocator();
        this.sourceDiscovery = new SourceDiscovery();
        this.compilerRunner = new CompilerRunner();
        this.programRunner = new ProgramRunner();
        this.mainClassResolver = new MainClassResolver();
    }

    public int execute() throws IOException, InterruptedException {

        Path projectRoot = projectLocator.locate();
        Path sourceDirectory = projectRoot.resolve("src/main/java");
        Path outputDirectory = projectRoot.resolve(".zap/classes");

        List<Path> javaSources = sourceDiscovery.discover(sourceDirectory);

        if (javaSources.isEmpty()) {
            System.err.println("No Java source files found.");
            return 1;
        }

        System.out.println("Compiling " + javaSources.size()
                + " Java source files...");

        int compilationExitCode = compilerRunner.compile(
                javaSources,
                outputDirectory
        );

        if (compilationExitCode != 0) {
            System.err.println("Compilation failed.");
            return compilationExitCode;
        }

        String mainClass = mainClassResolver.resolve(javaSources);

        System.out.println("Running " + mainClass + "...");

        return programRunner.run(outputDirectory, mainClass);
    }
}
