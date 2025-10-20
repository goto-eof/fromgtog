package com.andreidodu.fromgtog.service.impl;

import com.andreidodu.fromgtog.dto.DeleteRepositoryRequestDTO;
import com.andreidodu.fromgtog.exception.CloningDestinationException;
import com.andreidodu.fromgtog.service.LocalService;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
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

    // TODO builder pattern + pass DTO and not a lot of parameters
    @Override
    public boolean clone(String login, String token, String cloneUrl, String localTemporaryRepoPath) throws GitAPIException {
        // delete local tmp data if necessary
        deleteDirectoryIfNecessary(localTemporaryRepoPath);
        return cloneRepositoryToLocal(login, token, cloneUrl, localTemporaryRepoPath);
    }

    private boolean cloneRepositoryToLocal(String login, String token, String cloneUrl, String localTemporaryRepoPath) throws GitAPIException {
        try (Git localGitRepository = Git.cloneRepository()
                .setURI(cloneUrl)
                .setCloneSubmodules(true)
                .setCloneAllBranches(true)
                .setDirectory(new File(localTemporaryRepoPath))
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(login, token))
                .call()) {
            Repository repository = localGitRepository.getRepository();

            log.info("HEAD 1: {}", repository.findRef("HEAD").getTarget().getName());
            log.info("HEAD ID 1: {}", repository.resolve("HEAD").getName());

            String mainBranchName = repository.getBranch();
            log.info("master 1: {}", mainBranchName);
            cloneAllRepositoryBranches(localGitRepository, mainBranchName);
            cloneAllRepositoryTags(localGitRepository);
            removeAllRemoteURL(localGitRepository);
            ensureMainBranchCheckedOut(localGitRepository, mainBranchName);

            log.info("master 2: {}", repository.getBranch());
            log.info("HEAD 2: {}", repository.findRef("HEAD").getTarget().getName());
            log.info("HEAD ID 2: {}", repository.resolve("HEAD").getName());

            return true;
        } catch (TransportException | RuntimeException e) {
            log.error("Unable to clone repository {}...", cloneUrl, e);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            throw e;
        } catch (IOException e) {
            log.error("Unable to clone repository {}...", cloneUrl, e);
            throw new RuntimeException(e);
        }
    }

    private static void setBranch(Git localGitRepository, String mainBranchName) throws GitAPIException {
        localGitRepository.checkout()
                .setName(mainBranchName)
                .call();
    }

    private static void ensureMainBranchCheckedOut(Git localGitRepository, String mainBranchName) throws GitAPIException {
        localGitRepository.checkout()
                .setName(mainBranchName)
                .setForced(true)
                .call();
    }

    private static void removeAllRemoteURL(Git localGitRepository) throws GitAPIException {
        localGitRepository.remoteList().call().forEach(remote ->
                {
                    try {
                        localGitRepository.remoteRemove().setRemoteName(remote.getName()).call();
                    } catch (GitAPIException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    private static void deleteDirectoryIfNecessary(String toPath) {
        if (!new File(toPath).exists()) {
            return;
        }
        try {
            FileUtils.deleteDirectory(new File(toPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void cloneAllRepositoryTags(Git localGitRepository) throws GitAPIException {
        for (Ref tag : localGitRepository.tagList().call()) {
            String tagName = tag.getName().replace("refs/tags/", "");
            setBranch(localGitRepository, tagName);
        }
    }

    private static void cloneAllRepositoryBranches(Git localGitRepository, String masterBranch) throws GitAPIException {
        localGitRepository.branchList()
                .setListMode(ListBranchCommand.ListMode.REMOTE)
                .call()
                .stream()
                .filter(branch -> !branch.getName().endsWith("/" + masterBranch))
                .forEach(branch -> {
                    try {
                        List<String> arr = Arrays.stream(branch.getName()
                                        .replace("refs/remotes/", "")
                                        .split("/"))
                                .skip(1)
                                .toList();
                        String branchName = String.join("/", arr);

                        localGitRepository.branchCreate()
                                .setName(branchName)
                                .setStartPoint(branch.getName())
                                .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                                .call();
                    } catch (GitAPIException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    // TODO builder pattern + pass DTO and not a lot of parameters
    @Override
    public boolean pushOnRemote(String login, String token, String baseUrl, String repositoryName, String
            ownerLogin, File localTemporaryRepoPath, final boolean forceFlag, boolean deleteLocalDirectory) throws IOException, GitAPIException, URISyntaxException {
        String remoteUrl = baseUrl + "/" + ownerLogin + "/" + repositoryName + ".git";
        log.debug("pushOnRemote {}", remoteUrl);
        Git localGit = Git.open(localTemporaryRepoPath);

        Repository repository = localGit.getRepository();
        log.info("master 3: {}", repository.getBranch());
        log.info("HEAD 3: {}", repository.findRef("HEAD").getTarget().getName());
        log.info("HEAD ID 3: {}", repository.resolve("HEAD").getName());

        final String GIT_REMOTE = "fromgtog";
        localGit.remoteAdd()
                .setName(GIT_REMOTE)
                .setUri(new URIish(remoteUrl))
                .call();

        String mainBranch = repository.getBranch();

        List<Iterable<PushResult>> result = new ArrayList<>();

        Iterable<PushResult> pushResult = pushMainBranch(login, token, forceFlag, localGit, GIT_REMOTE, mainBranch);
        result.add(pushResult);
        List<Iterable<PushResult>> pushAllOtherBranches = pushAllOtherBranches(login, token, forceFlag, localGit, GIT_REMOTE, mainBranch);
        result.addAll(pushAllOtherBranches);
        Iterable<PushResult> pushAllTags = pushAllTags(login, token, forceFlag, localGit, GIT_REMOTE, mainBranch);
        result.add(pushAllTags);

        localGit.remoteRemove().setRemoteName(GIT_REMOTE).call();
        // delete temporary directory
        if (deleteLocalDirectory) {
            deleteDirectoryIfNecessary(localTemporaryRepoPath.getAbsolutePath());
        }

        return isPushOK(result);
    }

    private static Iterable<PushResult> pushMainBranch(String login, String token, boolean forceFlag, Git localGit, String GIT_REMOTE, String mainBranch) throws GitAPIException {
        return localGit.push()
                .setRemote(GIT_REMOTE)
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(login, token))
                .setRefSpecs(new RefSpec("refs/heads/" + mainBranch + ":refs/heads/" + mainBranch))
                .setAtomic(true)
                .setForce(forceFlag)
                .call();
    }

    private Iterable<PushResult> pushAllTags(String login, String token, boolean forceFlag, Git localGit, String GIT_REMOTE, String mainBranch) throws GitAPIException {
        return localGit.push()
                .setRemote(GIT_REMOTE)
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(login, token))
                .setPushTags()
                .setAtomic(true)
                .call();
    }

    private List<Iterable<PushResult>> pushAllOtherBranches(String login, String token, boolean forceFlag, Git localGit, String GIT_REMOTE, String mainBranch) throws GitAPIException {
        return localGit.branchList().call().stream()
                .filter(b -> !b.getName().equals("refs/heads/" + mainBranch))
                .map(b -> {
                    try {
                        return localGit.push()
                                .setRemote(GIT_REMOTE)
                                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(login, token))
                                .setRefSpecs(new RefSpec(b.getName() + ":" + b.getName()))
                                .setAtomic(true)
                                .setForce(forceFlag)
                                .call();
                    } catch (GitAPIException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();
    }

    private boolean isPushOK(List<Iterable<PushResult>> result) {
        return result.stream()
                .allMatch(this::isPushOK);
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

            if (!file.exists()) {
                return false;
            }

            FileUtils.deleteDirectory(file);

            log.info("Directory deleted successfully: {}", file.getAbsoluteFile());

            return true;

        } catch (IOException e) {
            log.error("error deleting files: {}", e.getMessage(), e);
            throw new CloningDestinationException("unable to delete the file", e);
        }
    }

}
