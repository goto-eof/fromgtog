package com.andreidodu.fromgtog.service.impl;

import com.andreidodu.fromgtog.dto.DeleteRepositoryRequestDTO;
import com.andreidodu.fromgtog.service.LocalService;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RemoteRefUpdate;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.andreidodu.fromgtog.util.ValidatorUtil.validateIsNotNull;

public class LocalServiceImpl implements LocalService {
    private static LocalService instance;
    Logger log = LoggerFactory.getLogger(LocalServiceImpl.class);

    public static LocalService getInstance() {
        if (instance == null) {
            instance = new LocalServiceImpl();
        }
        return instance;
    }

    private static File getFile(String pathString) {
        return new File(pathString);
    }

    @Override
    public List<String> listAllRepos(final String rootPath) {
        Objects.requireNonNull(rootPath);
        List<String> repos = new ArrayList<>();
        findGitRepositoriesExcursively(rootPath, repos);
        return repos;
    }

    private void findGitRepositoriesExcursively(String path, List<String> repositories) {
        File pathFile = getFile(path);
        log.debug("findGitRepositoriesExcursively {}", pathFile);
        List<String> foundRepositories = Arrays.stream(
                        Optional.ofNullable(pathFile.listFiles())
                                .orElseGet(() -> new File[0])
                )
                .peek(file -> log.debug(file.getAbsolutePath()))
                .filter(File::isDirectory)
                .map(File::getAbsolutePath)
                .toList();

        repositories.addAll(
                foundRepositories.stream()
                        .map(File::new)
                        .filter(this::isGitRepository)
                        .map(File::getAbsolutePath)
                        .toList()
        );

        foundRepositories
                .stream()
                .filter(repoPath -> !isGitRepository(new File(repoPath)))
                .forEach(repoPath -> findGitRepositoriesExcursively(repoPath, repositories));
    }

    private boolean isGitRepository(File path) {
        File gitDir = new File(path, ".git");
        return gitDir.exists() && gitDir.isDirectory();
    }

    @Override
    public boolean clone(String login, String token, String cloneUrl, String toPath) throws GitAPIException {

        Git git = Git.cloneRepository()
                .setURI(cloneUrl)
                .setDirectory(new File(toPath))
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(login, token))
                .call();

        return true;
    }

    @Override
    public boolean pushOnRemote(String login, String token, String baseUrl, String repositoryName, String ownerLogin, File localDir, final boolean forceFlag) throws IOException, GitAPIException, URISyntaxException {
        String remoteUrl = baseUrl + "/" + ownerLogin + "/" + repositoryName + ".git";
        log.debug("pushOnRemote {}", remoteUrl);
        Git git = Git.open(localDir);


        final String GIT_REMOTE = "fromgtog";

        git.remoteAdd()
                .setName(GIT_REMOTE)
                .setUri(new URIish(remoteUrl))
                .call();

        Iterable<PushResult> pushResult = git.push()
                .setRemote(GIT_REMOTE)
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(login, token))
                .setPushAll()
                .setForce(forceFlag)
                .call();

        git.remoteRemove().setRemoteName(GIT_REMOTE).call();

        return isPushOK(pushResult);
    }

    private boolean isPushOK(Iterable<PushResult> pushResults) {
        return StreamSupport.stream(pushResults.spliterator(), true)
                .allMatch(result ->
                        result.getRemoteUpdates()
                                .stream()
                                .allMatch(this::isRemoteRefStatusOK)
                );
    }

    private boolean isRemoteRefStatusOK(RemoteRefUpdate refUpdate) {
        RemoteRefUpdate.Status status = refUpdate.getStatus();
        return List.of(RemoteRefUpdate.Status.OK, RemoteRefUpdate.Status.UP_TO_DATE).contains(status);
    }

    @Override
    public boolean isRemoteRepositoryExists(String login, String token, String remoteUrl) {
        try {
            log.debug("checking if repository already exists...{}", remoteUrl);
            var result = Git.lsRemoteRepository()
                    .setRemote(remoteUrl)
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(login, token))
                    .call();
            log.debug("isRemoteRepositoryExists {}", result);
            return true;
        } catch (GitAPIException e) {
            log.error("Error checking remote repository: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteRepository(DeleteRepositoryRequestDTO deleteRepositoryRequestDTO) {
        validateIsNotNull(deleteRepositoryRequestDTO);
        File file = deleteRepositoryRequestDTO.localPath()
                .orElseThrow(() -> new IllegalArgumentException("localPath is null"));
        return deleteFilesRecursively(file);
    }

    private boolean deleteFilesRecursively(File file) {
        validateIsNotNull(file);

        log.debug("deleteFilesRecursively {}", file.getAbsoluteFile());
        try {

            Path path = file.toPath();

            if (!Files.exists(path)) {
                return false;
            }

            try (Stream<Path> walk = Files.walk(path)) {
                walk.sorted(Comparator.reverseOrder())
                        .forEach(deleteSingleFileOrDirectoryConsumer());
                log.info("Deleted directory: {}", path.toAbsolutePath());
            }

            log.info("Directory deleted successfully: {}", file.getAbsoluteFile());

            return true;

        } catch (IOException e) {
            log.error("error deleting files: {}", e.getMessage(), e);
            return false;
        }
    }

    private Consumer<Path> deleteSingleFileOrDirectoryConsumer() {
        return pathFile -> {
            try {
                Files.delete(pathFile);
            } catch (IOException e) {
                log.error("Failed to delete {}", pathFile, e);
            }
        };
    }

}
