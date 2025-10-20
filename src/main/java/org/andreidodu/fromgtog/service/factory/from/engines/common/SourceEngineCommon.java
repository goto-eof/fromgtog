package org.andreidodu.fromgtog.service.factory.from.engines.common;

import org.andreidodu.fromgtog.constants.ApplicationConstants;
import org.andreidodu.fromgtog.util.StringUtil;

import java.util.List;

public class SourceEngineCommon {

    public List<String> buildOrganizationBlacklist(String excludedOrganizations) {
        return StringUtil.stringsSeparatedByCommaToList(excludedOrganizations, ApplicationConstants.LIST_ITEM_SEPARATOR);
    }

}
