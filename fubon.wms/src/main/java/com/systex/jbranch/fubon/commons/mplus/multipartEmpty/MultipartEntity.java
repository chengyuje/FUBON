package com.systex.jbranch.fubon.commons.mplus.multipartEmpty;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.systex.jbranch.fubon.commons.mplus.multipartEmpty.SecretKeyType;
import com.systex.jbranch.fubon.commons.mplus.multipartEmpty.annotation.MultipartAes;
import com.systex.jbranch.fubon.commons.mplus.multipartEmpty.annotation.MultipartAesUrlEncode;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MultipartEntity {
	SecretKeyType secretKeyType() default SecretKeyType.EMPTY;
	String secretKeyValue();
	
	@SuppressWarnings("rawtypes")
	Class voToMultipartEntity() default MultipartAes.class;
}
