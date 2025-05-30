package com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.commands;

import com.andreidodu.fromgtog.service.LocalService;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.Command;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.records.RemoteExistsCheckCommandContext;
import com.andreidodu.fromgtog.service.impl.LocalServiceImpl;
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
                    .accept("Skipping repository because it already exists: " + remoteExistsCheckCommandContext.repositoryName());
            return true;
        }
        return false;
    }
}
