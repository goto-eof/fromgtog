package com.andreidodu.fromgtog.gui.validator.composite.factory;

import com.andreidodu.fromgtog.gui.validator.composite.ComponentValidator;

import java.util.List;
import java.util.stream.Stream;

public class SetupFactory {

    public List<ComponentValidator> build() {
        return Stream.of(
                        new FromGitHubTabFilterTabFactory(),
                        new FromGitHubTabFileTabFactory(),
                        new FromGiteaTabFilterTabFactory(),
                        new FromGiteaTabFileTabFactory(),
                        new FromGitlabTabFilterTabFactory(),
                        new FromGitlabTabFileTabFactory(),
                        new FromLocalTabFactory(),
                        new ToGitHubTabFactory(),
                        new ToGiteaTabFactory(),
                        new ToGitlabTabFactory(),
                        new ToLocalTabFactory()
                )
                .map(ComponentValidatorFactory::createValidator)
                .toList();


    }

}
