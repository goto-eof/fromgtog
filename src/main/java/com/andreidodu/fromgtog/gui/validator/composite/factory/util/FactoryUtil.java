package com.andreidodu.fromgtog.gui.validator.composite.factory.util;

import com.andreidodu.fromgtog.gui.util.RegexUtil;
import com.andreidodu.fromgtog.gui.validator.composite.PrimaryComponentValidator;
import com.andreidodu.fromgtog.gui.validator.composite.SecondaryComponentValidator;
import com.andreidodu.fromgtog.type.EngineOptionsType;
import com.andreidodu.fromgtog.type.EngineType;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static com.andreidodu.fromgtog.gui.controller.constants.GuiKeys.*;
import static com.andreidodu.fromgtog.util.OsUtil.*;

public class FactoryUtil {

    public static void buildGitHubFilterTab(Predicate<JSONObject> mainIsApplicableCondition, PrimaryComponentValidator primaryTab) {
        PrimaryComponentValidator secondaryTab = new PrimaryComponentValidator(mainIsApplicableCondition);
        primaryTab.addComponentValidator(secondaryTab);
        secondaryTab.addComponentValidator(new SecondaryComponentValidator(mainIsApplicableCondition, isOrganizationValid(FROM_GITHUB_EXCLUDE_ORGANIZATIONS), "organization"));
        secondaryTab.addComponentValidator(new SecondaryComponentValidator(mainIsApplicableCondition, isRepoNameValid(FROM_GITHUB_EXCLUDE_REPO_NAME_LIST), "repo name"));
    }

    public static void buildGitHubFileTab(Predicate<JSONObject> mainIsApplicableCondition, PrimaryComponentValidator primaryTab) {
        PrimaryComponentValidator secondaryTab = new PrimaryComponentValidator(mainIsApplicableCondition);
        primaryTab.addComponentValidator(secondaryTab);
        secondaryTab.addComponentValidator(new SecondaryComponentValidator(mainIsApplicableCondition, isMandatoryFilePathValid(FROM_GITHUB_INCLUDE_REPO_NAMES_LIST_FILE), "filename"));
    }

    public static void buildGiteaFilterTab(Predicate<JSONObject> mainIsApplicableCondition, PrimaryComponentValidator primaryTab) {
        PrimaryComponentValidator secondaryTab = new PrimaryComponentValidator(mainIsApplicableCondition);
        primaryTab.addComponentValidator(secondaryTab);
        secondaryTab.addComponentValidator(new SecondaryComponentValidator(mainIsApplicableCondition, isOrganizationValid(FROM_GITEA_EXCLUDE_ORGANIZATIONS), "organization"));
        secondaryTab.addComponentValidator(new SecondaryComponentValidator(mainIsApplicableCondition, isRepoNameValid(FROM_GITEA_EXCLUDE_REPO_NAME_LIST), "repo name"));
    }

    public static void buildGiteaFileTab(Predicate<JSONObject> mainIsApplicableCondition, PrimaryComponentValidator primaryTab) {
        PrimaryComponentValidator secondaryTab = new PrimaryComponentValidator(mainIsApplicableCondition);
        primaryTab.addComponentValidator(secondaryTab);
        secondaryTab.addComponentValidator(new SecondaryComponentValidator(mainIsApplicableCondition, isMandatoryFilePathValid(FROM_GITEA_INCLUDE_REPO_NAMES_LIST_FILE), "filename"));
    }

    public static void buildGitlabFilterTab(Predicate<JSONObject> mainIsApplicableCondition, PrimaryComponentValidator primaryTab) {
        PrimaryComponentValidator secondaryTab = new PrimaryComponentValidator(mainIsApplicableCondition);
        primaryTab.addComponentValidator(secondaryTab);
        secondaryTab.addComponentValidator(new SecondaryComponentValidator(mainIsApplicableCondition, isOrganizationValid(FROM_GITLAB_EXCLUDE_ORGANIZATIONS), "organization"));
        secondaryTab.addComponentValidator(new SecondaryComponentValidator(mainIsApplicableCondition, isRepoNameValid(FROM_GITLAB_EXCLUDE_REPO_NAME_LIST), "repo name"));
    }

    public static void buildGitlabFileTab(Predicate<JSONObject> mainIsApplicableCondition, PrimaryComponentValidator primaryTab) {
        PrimaryComponentValidator secondaryTab = new PrimaryComponentValidator(mainIsApplicableCondition);
        primaryTab.addComponentValidator(secondaryTab);
        secondaryTab.addComponentValidator(new SecondaryComponentValidator(mainIsApplicableCondition, isMandatoryFilePathValid(FROM_GITLAB_INCLUDE_REPO_NAMES_LIST_FILE), "filename"));
    }

    public static Predicate<JSONObject> isMandatoryUrlValid(String url) {
        return jsonObject -> {
            String valueToValidate = jsonObject.getString(url);
            return !isEmpty(valueToValidate) && validate(valueToValidate, RegexUtil.REGEX_PATTERN_URL);
        };
    }

    public static Predicate<JSONObject> isRepoNameValid(String repoName) {
        return jsonObject -> {
            String valueToValidate = jsonObject.getString(repoName);
            return isEmpty(valueToValidate) || validate(valueToValidate, RegexUtil.REGEX_PATTERN_REPO_NAME);
        };
    }

    public static Predicate<JSONObject> isOrganizationValid(String excludeOrganizations) {
        return jsonObject -> {
            String valueToValidate = jsonObject.getString(excludeOrganizations);
            return isEmpty(valueToValidate) || Arrays.stream(valueToValidate.split(",")).allMatch(str -> validate(str, RegexUtil.REGEX_PATTERN_USERNAME));
        };
    }

    public static boolean isEmpty(String valueToValidate) {
        return valueToValidate == null || valueToValidate.trim().isEmpty();
    }

    public static boolean validate(String valueToValidate, Pattern regexPatternUsername) {
        return regexPatternUsername
                .matcher(valueToValidate)
                .matches();
    }

    public static Predicate<JSONObject> isSecondaryTabIndexEqualTo(EngineOptionsType engineOptionsType, String key) {
        return jsonObject -> engineOptionsType.equals(jsonObject.getEnum(EngineOptionsType.class, key));
    }

    public static Predicate<JSONObject> isMandatoryTokenValid(String tokenKey) {
        return jsonObject -> {
            String valueToValidate = jsonObject.getString(tokenKey);
            return !isEmpty(valueToValidate) && validate(valueToValidate, RegexUtil.REGEX_PATTERN_AT_LEAST_ONE_CHAR);
        };
    }

    public static Predicate<JSONObject> isMandatoryDirPathValid(String tokenKey) {
        return jsonObject -> {
            String valueToValidate = jsonObject.getString(tokenKey);
            return !isEmpty(valueToValidate) && isDirPathCrossPlatformValid(valueToValidate);
        };
    }

    private static boolean isDirPathCrossPlatformValid(String valueToValidate) {

        if (isWindows()) {
            return validate(valueToValidate, RegexUtil.REGEX_PATTERN_WINDOWS_DIR_PATH);
        }

        if (isLinux()) {
            return validate(valueToValidate, RegexUtil.REGEX_PATTERN_LINUX_DIR_PATH);
        }

        if (isMac()) {
            return validate(valueToValidate, RegexUtil.REGEX_PATTERN_MAC_DIR_PATH);
        }

        throw new IllegalArgumentException("Unsupported OS");
    }

    public static Predicate<JSONObject> isFromMainTabIndexEqualTo(EngineType engineType) {
        return (jsonObject) -> engineType.equals(EngineType.fromValue(jsonObject.getInt(FROM_TAB_INDEX)));
    }

    public static Predicate<JSONObject> isToMainTabIndexEqualTo(EngineType engineType) {
        return (jsonObject) -> engineType.equals(EngineType.fromValue(jsonObject.getInt(TO_TAB_INDEX)));
    }

    private static Predicate<JSONObject> isMandatoryFilePathValid(String key) {
        return jsonObject -> {
            String valueToValidate = jsonObject.getString(key);
            return !isEmpty(valueToValidate) && isFilenameCrossPlatformValid(valueToValidate);
        };
    }

    private static boolean isFilenameCrossPlatformValid(String valueToValidate) {

        if (isWindows()) {
            return validate(valueToValidate, RegexUtil.REGEX_PATTERN_WINDOWS_FILE_PATH);
        }

        if (isLinux()) {
            return validate(valueToValidate, RegexUtil.REGEX_PATTERN_LINUX_FILE_PATH);
        }

        if (isMac()) {
            return validate(valueToValidate, RegexUtil.REGEX_PATTERN_MAC_FILE_PATH);
        }

        throw new IllegalArgumentException("Unsupported OS");
    }

    public static Predicate<JSONObject> isMandatorySleepTimeValid(String appSleepTime) {
        return jsonObject -> {
            String valueToValidate = jsonObject.get(appSleepTime).toString();
            return !isEmpty(valueToValidate) && validate(valueToValidate, RegexUtil.REGEX_PATTERN_NUMBER_0_120);
        };
    }
}
