package com.ski.tournament.config;

import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// aktualnie nieużywany, zastąpiony domyślną implementacją VaadinDefaultRequestCache
public class CustomRequestCache extends HttpSessionRequestCache {


    @Override
    public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
        if (!SecurityUtils.isFrameworkInternalRequest(request)) {
            super.saveRequest(request, response);
        }
    }
}
