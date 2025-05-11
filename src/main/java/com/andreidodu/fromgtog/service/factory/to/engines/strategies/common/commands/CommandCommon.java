package com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.commands;

import com.andreidodu.fromgtog.dto.CallbackContainer;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.records.StatusCommandContext;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.records.ThreadStopCommandContext;

import java.io.File;

public class CommandCommon {
    final static String TEMP_DIRECTORY = System.getProperty("java.io.tmpdir");

    public static String buildStagedPath(String repositoryName) {
        return TEMP_DIRECTORY + File.separator + repositoryName;
    }

    public static StatusCommandContext buildUpdateStatusContext(CallbackContainer callbackContainer, Integer maxProgressValue, Integer currentProgressValue, String statusMessage) {
        return StatusCommandContext.builder()
                .progressMaxValue(maxProgressValue)
                .progressCurrentValue(currentProgressValue)
                .messageStatus(statusMessage)
                .updateStatusMessageConsumer(callbackContainer.updateApplicationStatusMessage())
                .updateProgressCurrentConsumer(callbackContainer.updateApplicationProgressBarCurrent())
                .updateProgressMaxConsumer(callbackContainer.updateApplicationProgressBarMax())
                .build();
    }

    public static Boolean isShouldStopTheProcess(String repositoryName, CallbackContainer callbackContainer) {
        return new ThreadStopCheckCommand(
                ThreadStopCommandContext.builder()
                        .isShouldStopSupplier(callbackContainer.isShouldStop())
                        .repositoryName(repositoryName)
                        .updateApplicationStatusMessageConsumer(callbackContainer.updateApplicationStatusMessage())
                        .build()
        ).execute();
    }
}
