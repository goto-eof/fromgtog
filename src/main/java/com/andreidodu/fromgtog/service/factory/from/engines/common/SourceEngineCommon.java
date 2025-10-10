package com.andreidodu.fromgtog.service.factory.from.engines.common;

import com.andreidodu.fromgtog.constants.ApplicationConstants;
import com.andreidodu.fromgtog.util.StringUtil;

import java.util.List;

public class SourceEngineCommon {

    public List<String> buildOrganizationBlacklist(String excludedOrganizations) {
        return StringUtil.stringsSeparatedByCommaToList(excludedOrganizations, ApplicationConstants.LIST_ITEM_SEPARATOR);
    }

}
