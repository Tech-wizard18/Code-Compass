package com.codecompass.backend.service;

import org.eclipse.jgit.api.Git;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class GitCloneService {

    public String cloneRepo(String repoUrl) throws Exception{

        Path tempDir = Files.createTempDirectory("codecompass-repo-");
        File targetDir = tempDir.toFile();

        Git.cloneRepository()
                .setURI(repoUrl)
                .setDirectory(targetDir)
                .call();

        return targetDir.getAbsolutePath();
    }
}
