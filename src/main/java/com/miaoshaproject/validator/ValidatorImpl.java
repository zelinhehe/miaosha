package com.miaoshaproject.validator;

import org.springframework.beans.factory.InitializingBean;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

public class ValidatorImpl implements InitializingBean {

    private Validator validator;

    public ValidationResult validate(Object bean) {
        ValidationResult result = new ValidationResult();
        Set<ConstraintViolation<Object>> constraintViolationSet = validator.validate(bean);
        if (constraintViolationSet.size() > 0) {
            // 有错误
            result.setHasErrors(true);
            constraintViolationSet.forEach(
                    objectConstraintViolation -> {
                        String errMsg = objectConstraintViolation.getMessage();
                        String propertyName = objectConstraintViolation.getPropertyPath().toString();
                        result.getErrorMsgMap().put(propertyName, errMsg);
                    }
            );
        }
        return result;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 将 hibernate validator通过工厂的初始化方式使其实例化
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }
}
