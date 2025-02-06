package no.gunbang.market.common;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.springframework.stereotype.Component;

import static org.hibernate.type.StandardBasicTypes.DOUBLE;

@Component
public class CustomFunctionContributor implements FunctionContributor {
    private static final String FUNCTION_NAME = "match_against";
    private static final String FUNCTION_TEMPLATE = "MATCH(?1) AGAINST(?2 IN BOOLEAN MODE)";

    @Override
    public void contributeFunctions(FunctionContributions functionContributions) {
        functionContributions.getFunctionRegistry().registerPattern(
                FUNCTION_NAME,
                FUNCTION_TEMPLATE,
                functionContributions.getTypeConfiguration().getBasicTypeRegistry().resolve(DOUBLE)
        );
    }
}