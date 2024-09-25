package jye.budget.login.argumentresolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jye.budget.login.SessionConst;
import jye.budget.user.entity.User;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        log.info("supportsParameter 실행");

        boolean hasLoginAnnotation = methodParameter.hasParameterAnnotation(Login.class);
        boolean hasUserType = User.class.isAssignableFrom(methodParameter.getParameterType());

        return hasLoginAnnotation && hasUserType;
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) {
        log.info("resolveArgument 실행");

        HttpServletRequest request = (HttpServletRequest) nativeWebRequest.getNativeRequest();
        HttpSession session = request.getSession();
        if (session == null) {
            return null;
        }

        return session.getAttribute(SessionConst.LOGIN_USER);
    }
}
