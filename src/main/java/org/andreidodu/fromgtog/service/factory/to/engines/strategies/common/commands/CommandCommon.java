package org.andreidodu.fromgtog.service.factory.to.engines.strategies.common.commands;

import org.andreidodu.fromgtog.dto.CallbackContainer;
import org.andreidodu.fromgtog.service.factory.to.engines.strategies.common.records.RemoteExistsCheckCommandContext;
import org.andreidodu.fromgtog.service.factory.to.engines.strategies.common.records.StatusCommandContext;
import org.andreidodu.fromgtog.service.factory.to.engines.strategies.common.records.ThreadStopCommandContext;
import org.andreidodu.fromgtog.util.ApplicationUtil;

import java.io.File;

public class CommandCommon {
    final static String TEMP_DIRECTORY = ApplicationUtil.getTemporaryFolderName();

    public static String buildStagedPath(String repositoryName) {
        return TEMP_DIRECTORY + File.separator + repositoryName;
    }

    public static StatusCommandContext buildUpdateStatusContext(CallbackContainer callbackContainer, Integer maxProgressValue, Integer currentProgressValue, String statusMessage) {
        return StatusCommandContext.builder()
                .progressMaxValue(maxProgressValue)
                .progressCurrentValue(currentProgressValue)
                .messageStatus(statusMessage)
                .updateStatusMessageConsumer(callbackContainer.updateLogAndApplicationStatusMessage())
                .updateProgressCurrentConsumer(callbackContainer.updateApplicationProgressBarCurrent())
                .updateProgressMaxConsumer(callbackContainer.updateApplicationProgressBarMax())
                .build();
    }

    public static Boolean isShouldStopTheProcess(String repositoryName, CallbackContainer callbackContainer) {
        return new ThreadStopCheckCommand(
                ThreadStopCommandContext.builder()
                        .isShouldStopSupplier(callbackContainer.isShouldStop())
                        .repositoryName(repositoryName)
                        .updateApplicationStatusMessageConsumer(callbackContainer.updateLogAndApplicationStatusMessage())
                        .build()
        ).execute();
    }


    public static Boolean isRemoteDestinationRepositoryAlreadyExists(RemoteExistsCheckCommandContext remoteExistsCheckCommandContext) {
        return new RemoteExistsCheckCommand(remoteExistsCheckCommandContext).execute();
    }
}
