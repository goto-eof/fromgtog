package org.andreidodu.fromgtog.service.factory.to.engines.strategies.common.commands;

import org.andreidodu.fromgtog.service.LocalService;
import org.andreidodu.fromgtog.service.factory.to.engines.strategies.common.Command;
import org.andreidodu.fromgtog.service.factory.to.engines.strategies.common.records.RemoteExistsCheckCommandContext;
import org.andreidodu.fromgtog.service.impl.LocalServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteExistsCheckCommand implements Command<Boolean> {
    final static Logger log = LoggerFactory.getLogger(RemoteExistsCheckCommand.class);

    private final RemoteExistsCheckCommandContext remoteExistsCheckCommandContext;

    public RemoteExistsCheckCommand(RemoteExistsCheckCommandContext remoteExistsCheckCommandContext) {
        this.remoteExistsCheckCommandContext = remoteExistsCheckCommandContext;
    }

    @Override
    public Boolean execute() {
        log.debug("checking if repositoy {} already exists", remoteExistsCheckCommandContext.repositoryName());
        remoteExistsCheckCommandContext.updateApplicationStatusMessagesConsumer().accept("checking if repository already exists: " + remoteExistsCheckCommandContext.repositoryName());

        LocalService localService = LocalServiceImpl.getInstance();

        String fullUrl = String.format("%s/%s/%s.git", remoteExistsCheckCommandContext.baseUrl(), remoteExistsCheckCommandContext.login(), remoteExistsCheckCommandContext.repositoryName());

        if (localService.isRemoteRepositoryExists(remoteExistsCheckCommandContext.login(), remoteExistsCheckCommandContext.token(), fullUrl)) {
            log.debug("skipping because {} already exists", remoteExistsCheckCommandContext.repositoryName());
            remoteExistsCheckCommandContext.updateApplicationStatusMessagesConsumer()
                    .accept("repository already exists: " + remoteExistsCheckCommandContext.repositoryName());
            return true;
        }
        return false;
    }
}
