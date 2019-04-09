package com.miaoshaproject.Validator;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

@Component
public class ValidatorImpl implements InitializingBean {

    private Validator validator;

    public ValidationResults validate(Object bean){
        final ValidationResults results = new ValidationResults();
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(bean);
        if (constraintViolations.size() > 0){
            results.setHasErrors(true);
            constraintViolations.forEach(constraintViolation->{
                String errMsg = constraintViolation.getMessage();
                String propertyName = constraintViolation.getPropertyPath().toString();
                results.getErrorMsgMap().put(propertyName,errMsg);
            });
        }
        return results;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }
}
