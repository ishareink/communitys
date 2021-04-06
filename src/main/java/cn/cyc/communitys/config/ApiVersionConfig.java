package cn.cyc.communitys.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Aspect
@Component
public class ApiVersionConfig {
    private Logger log = LoggerFactory.getLogger(ApiVersionConfig.class);

    // 正则
    private final static Pattern VERSION_PREFIX_PATTERN = Pattern.compile("v(\\d+)/");

    /**
     * 自定义注解，实现接口版本管理
     */
    @Target({ElementType.METHOD}) //用于方法
    @Retention(RetentionPolicy.RUNTIME) //运行时有效
    @Documented //这个工具被java doc 收录
    public @interface ApiVersion {

        /**
         * 携带参数，默认为1
         * @return
         */
        int value() default 1;
    }
    /**
     * 定义切点
     */
    @Pointcut("@annotation(cn.cyc.communitys.config.ApiVersionConfig.ApiVersion)")
    public void resources(){ }

    @Around("resources()")
    public Object check(ProceedingJoinPoint pjp) throws Throwable {
        // 1、如果方法上标识了该注解
        // 拿到请求
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        // 获取请求的uri
        String requestURI = request.getRequestURI();
        log.info("请求的url：{}",request.getRequestURL());
        log.info("请求的uri：{}",requestURI);
        // 2、第二重校验，判断是否符合要求
        Matcher matcher = VERSION_PREFIX_PATTERN.matcher(requestURI);
        if(matcher.find()){
            // 3、符合正则表达式，则获取其中的值
            log.info("matcher-->{}",matcher);
            Integer version = Integer.valueOf(matcher.group(1));
            log.info("version--->{}",String.valueOf(version));
            // 获取注解中设定的值
            MethodSignature ms = (MethodSignature) pjp.getSignature();
            // 获取对应方法对象
            Method method = ms.getMethod();
            // 通过方法对象，根据反射获取方法上的注解
            ApiVersion annotation = method.getAnnotation(ApiVersion.class);
            Integer oldVersion = annotation.value();
            log.info("oldVersion-->{}",oldVersion);
            // 4、判断是否符合要求
            if((version - oldVersion) >= 0){
                // 继续执行
                return pjp.proceed();
            }
        }
        // 这里应该返回一个json，或者抛出一个自定义异常
        return "请求无效。。。";
    }
}


