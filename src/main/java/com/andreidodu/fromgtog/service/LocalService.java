package com.andreidodu.fromgtog.service;

import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface LocalService {

    List<String> listAllRepos(String rootPath);

    boolean clone(String login, String token, String cloneUrl, String toPath) throws GitAPIException;

    boolean pushOnRemote(String login, String token, String baseUrl, String repositoryName, String ownerLogin, File localDir) throws IOException, GitAPIException, URISyntaxException;

    boolean isRemoteRepositoryExists(String login, String token, String remoteUrl);
}
