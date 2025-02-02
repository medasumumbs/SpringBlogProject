package ru.muravin.mvc_blog_application.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class PageableUtils {
    public static  <T> Page<T> listToPage(Pageable pageable, List<T> entities) {
        int lowerBound = pageable.getPageNumber() * pageable.getPageSize();
        int upperBound = Math.min(lowerBound + pageable.getPageSize(), entities.size());
        if (lowerBound == upperBound) upperBound = lowerBound + pageable.getPageSize();
        if (entities.isEmpty()) {
            return Page.empty(pageable);
        }
        List<T> subList = entities.subList(lowerBound, upperBound);

        return new PageImpl<T>(subList, pageable, entities.size());
    };
}
