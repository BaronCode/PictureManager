package com.picman.picman.Endpoints;

import com.picman.picman.Exceptions.AccessDeniedException;
import com.picman.picman.Exceptions.InvalidEntryPointException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccessDeniedEndpoints {
    @GetMapping("/_401")
    public String _401Handler() {
        throw new InvalidEntryPointException("Must be authenticated to access this page!");
    }
    @GetMapping("/_403")
    public String _403Handler() {
        throw new AccessDeniedException("Not enough privileges to access this page!");
    }
}
