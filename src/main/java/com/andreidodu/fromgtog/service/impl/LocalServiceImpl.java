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
import org.eclipse.jgit.revwalk.RevWalk;
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

    @Override
    public boolean clone(String login, String token, String cloneUrl, String toPath) throws GitAPIException {
        try {
            FileUtils.deleteDirectory(new File(toPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (Git localGitRepository = Git.cloneRepository()
                .setURI(cloneUrl)
                .setCloneSubmodules(true)
                .setCloneAllBranches(true)
                .setDirectory(new File(toPath))
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(login, token))
                .call()) {
            String masterBranch = localGitRepository.getRepository().getBranch();
            log.info("master: {}", masterBranch);
            localGitRepository.branchList()
                    .setListMode(ListBranchCommand.ListMode.REMOTE)
                    .call()
                    .stream()
                    .filter(branch -> !branch.getName().endsWith("/" + masterBranch))
                    .forEach(branch -> {
                        try {
                            String[] fullName = branch.getName().split("/");
                            localGitRepository.branchCreate()
                                    .setName(fullName[fullName.length - 1])
                                    .setStartPoint(branch.getName())
                                    .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                                    .call();

                        } catch (GitAPIException e) {
                            throw new RuntimeException(e);
                        }
                    });

            localGitRepository.tagList().call().forEach(ref -> {
                try (RevWalk revWalk = new RevWalk(localGitRepository.getRepository())) {
//                    String tagName = ref.getName().replace("refs/tags/", "");
//                    RevObject revObject = revWalk.parseAny(ref.getObjectId());
//                    Ref tagCommand = localGitRepository.tag()
//                            .setName(tagName)
//                            .setObjectId(revObject)
//                            .setForceUpdate(true)
//                            .call();

                    localGitRepository.checkout()
                            .setName(ref.getName())
                            .call();

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });


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

    @Override
    public boolean pushOnRemote(String login, String token, String baseUrl, String repositoryName, String
            ownerLogin, File localDir, final boolean forceFlag) throws IOException, GitAPIException, URISyntaxException {
        String remoteUrl = baseUrl + "/" + ownerLogin + "/" + repositoryName + ".git";
        log.debug("pushOnRemote {}", remoteUrl);
        Git localGit = Git.open(localDir);


        final String GIT_REMOTE = "fromgtog";

        localGit.remoteAdd()
                .setName(GIT_REMOTE)
                .setUri(new URIish(remoteUrl))
                .call();

        Iterable<PushResult> pushResult = localGit.push()
                .setRemote(GIT_REMOTE)
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(login, token))
                .setPushAll()
                .setPushTags()
                .setRefSpecs(new RefSpec("refs/*:refs/*"))
                .setAtomic(true)
                .setForce(forceFlag)
                .call();

        localGit.remoteRemove().setRemoteName(GIT_REMOTE).call();

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
