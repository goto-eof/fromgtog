package com.andreidodu.fromgtog.service.factory.to.engines.strategies.generic;

import java.io.IOException;

public interface GenericDestinationEngineFromStrategyService {
    String getLogin(String token, String urlString);

    boolean createRepository(String url, String token, String repositoryName, String s, boolean equals) throws IOException, InterruptedException;

    boolean updateRepositoryPrivacy(String token, String login, String url, String repositoryName, boolean isArchived, boolean isPrivate) throws IOException, InterruptedException;
}
