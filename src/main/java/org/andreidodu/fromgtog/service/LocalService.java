package org.andreidodu.fromgtog.service;

import org.andreidodu.fromgtog.dto.DeleteRepositoryRequestDTO;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface LocalService {

    List<String> listAllRepos(String rootPath);

    boolean clone(String login, String token, String cloneUrl, String toPath) throws GitAPIException;

    boolean pushOnRemote(String login, String token, String baseUrl, String repositoryName, String ownerLogin, File localDir, final boolean forceFlag, boolean deleteLocalDirectory) throws IOException, GitAPIException, URISyntaxException;

    boolean isRemoteRepositoryExists(String login, String token, String remoteUrl);

    boolean deleteRepository(DeleteRepositoryRequestDTO deleteRepositoryRequestDTO);

}
