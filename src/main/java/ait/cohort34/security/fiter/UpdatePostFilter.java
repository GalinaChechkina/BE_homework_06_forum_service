package ait.cohort34.security.fiter;

import ait.cohort34.forum.dao.PostRepository;
import ait.cohort34.forum.model.Post;
import ait.cohort34.security.model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
@Order(60)
@RequiredArgsConstructor
public class UpdatePostFilter implements Filter {

    final PostRepository postRepository;

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request =(HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;

        if (checkEndpoint(request.getMethod(), request.getServletPath())) {

            User principal = (User) request.getUserPrincipal();

            String[] parts = request.getServletPath().split("/");
            String id = parts[parts.length-1];

            Post post = postRepository.findById(id).orElse(null);

            if(post==null){
                response.sendError(404, "Post not found");
                return;
            }
//изменить пост м. только сам автор
            String author = post.getAuthor();
            if(!principal.getName().equals(author)){
                response.sendError(403, "Not Authorized");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean checkEndpoint(String method, String servletPath) {
        return HttpMethod.PUT.matches(method) && servletPath.matches("/forum/post/\\w+");
    }
}
