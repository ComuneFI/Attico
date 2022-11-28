package it.linksmt.assatti.service.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Utility class for handling pagination.
 *
 * <p>
 * Pagination uses the same principles as the <a href="https://developer.github.com/v3/#pagination">Github API</api>,
 * and follow <a href="http://tools.ietf.org/html/rfc5988">RFC 5988 (Link header)</a>.
 * </p>
 */
public class PaginationUtil {

    public static final int DEFAULT_OFFSET = 1;

    public static final int MIN_OFFSET = 1;

    public static final int DEFAULT_LIMIT = Integer.MAX_VALUE;

    public static final int MAX_LIMIT = Integer.MAX_VALUE;

    public static Pageable generatePageRequest(Integer offset, Integer limit) {
        if (offset == null || offset < MIN_OFFSET) {
            offset = DEFAULT_OFFSET;
        }
        if (limit == null || limit > MAX_LIMIT) {
            limit = DEFAULT_LIMIT;
        }
        return new PageRequest(offset - 1, limit);
    }
    //ivan ggiunto per gestire il sort all'interno si delle resource che dei service
    public static Pageable generatePageRequest(Integer offset, Integer limit, Sort sort) {
        if (offset == null || offset < MIN_OFFSET) {
            offset = DEFAULT_OFFSET;
        }
        if (limit == null || limit > MAX_LIMIT) {
            limit = DEFAULT_LIMIT;
        }
        return new PageRequest(offset - 1, limit, sort);
    }
    //ivan ggiunto per gestire il sort all'interno si delle resource che dei service
    public static Pageable generatePageRequest(Pageable pageable, Sort sort) {
    	Integer offset = pageable.getOffset();
    	Integer limit = pageable.getPageSize();
        return new PageRequest(offset , limit, sort);
    }

    public static HttpHeaders generatePaginationHttpHeaders(Page page, String baseUrl, Integer offset, Integer limit)
        throws URISyntaxException {

        if (offset == null || offset < MIN_OFFSET) {
            offset = DEFAULT_OFFSET;
        }
        if (limit == null || limit > MAX_LIMIT) {
            limit = DEFAULT_LIMIT;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + page.getTotalElements());
        String link = "";
        if (offset < page.getTotalPages()) {
            link = "<" + (new URI(baseUrl +"?page=" + (offset + 1) + "&per_page=" + limit)).toString()
                + ">; rel=\"next\",";
        }
        if (offset > 1) {
            link += "<" + (new URI(baseUrl +"?page=" + (offset - 1) + "&per_page=" + limit)).toString()
                + ">; rel=\"prev\",";
        }
        link += "<" + (new URI(baseUrl +"?page=" + page.getTotalPages() + "&per_page=" + limit)).toString()
            + ">; rel=\"last\"," +
            "<" + (new URI(baseUrl +"?page=" + 1 + "&per_page=" + limit)).toString()
            + ">; rel=\"first\"";
        headers.add(HttpHeaders.LINK, link);
        return headers;
    }
    
    public static HttpHeaders generateGroupPaginationHttpHeaders(int groupId, int totalPages, long totalElements, String baseUrl, Integer offset, Integer limit, HttpHeaders headers)
            throws URISyntaxException {

            if (offset == null || offset < MIN_OFFSET) {
                offset = DEFAULT_OFFSET;
            }
            if (limit == null || limit > MAX_LIMIT) {
                limit = DEFAULT_LIMIT;
            }
            if(headers==null) {
            	headers = new HttpHeaders();
            }
            String link = "";
            if (offset < totalPages) {
                link = "<" + (new URI(baseUrl +"?page=" + (offset + 1) + "&per_page=" + limit)).toString()
                    + ">; rel=\"next\",";
            }
            if (offset > 1) {
                link += "<" + (new URI(baseUrl +"?page=" + (offset - 1) + "&per_page=" + limit)).toString()
                    + ">; rel=\"prev\",";
            }
            link += "<" + (new URI(baseUrl +"?page=" + totalPages + "&per_page=" + limit)).toString()
                + ">; rel=\"last\"," +
                "<" + (new URI(baseUrl +"?page=" + 1 + "&per_page=" + limit)).toString()
                + ">; rel=\"first\"";
            headers.add(HttpHeaders.LINK + groupId, link);
            return headers;
        }
    
    public static HttpHeaders generatePaginationHttpHeaders(Long totalElements, String baseUrl, Integer offset, Integer limit)
            throws URISyntaxException {
    		Long totalPages = totalElements / limit + (totalElements % limit == 0 ? 0 : 1);
            if (offset == null || offset < MIN_OFFSET) {
                offset = DEFAULT_OFFSET;
            }
            if (limit == null || limit > MAX_LIMIT) {
                limit = DEFAULT_LIMIT;
            }
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Total-Count", "" + totalElements);
            String link = "";
            if (offset < totalPages) {
                link = "<" + (new URI(baseUrl +"?page=" + (offset + 1) + "&per_page=" + limit)).toString()
                    + ">; rel=\"next\",";
            }
            if (offset > 1) {
                link += "<" + (new URI(baseUrl +"?page=" + (offset - 1) + "&per_page=" + limit)).toString()
                    + ">; rel=\"prev\",";
            }
            link += "<" + (new URI(baseUrl +"?page=" + totalPages + "&per_page=" + limit)).toString()
                + ">; rel=\"last\"," +
                "<" + (new URI(baseUrl +"?page=" + 1 + "&per_page=" + limit)).toString()
                + ">; rel=\"first\"";
            headers.add(HttpHeaders.LINK, link);
            return headers;
        }
    
  
}
