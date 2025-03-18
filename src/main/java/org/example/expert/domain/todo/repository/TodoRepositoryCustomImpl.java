package org.example.expert.domain.todo.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.response.QTodoSearchResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TodoRepositoryCustomImpl implements TodoRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        QTodo todo = QTodo.todo;

        Todo result = queryFactory
                .selectFrom(todo)
                .leftJoin(todo.user).fetchJoin()
                .where(todo.id.eq(todoId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable, String weather, LocalDateTime startDate, LocalDateTime endDate) {
        QTodo todo = QTodo.todo;

        BooleanBuilder builder = new BooleanBuilder();

        if (weather != null) {
            builder.and(todo.weather.eq(weather));
        }
        if (startDate != null) {
            builder.and(todo.modifiedAt.goe(startDate));
        }
        if (endDate != null) {
            builder.and(todo.modifiedAt.loe(endDate));
        }

        List<Todo> result = queryFactory
                .selectFrom(todo)
                .leftJoin(todo.user).fetchJoin()
                .where(builder)
                .orderBy(todo.modifiedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(result, pageable, () ->
                Optional.ofNullable(queryFactory
                                .select(todo.id.count())
                                .from(todo)
                                .where(builder)
                                .fetchOne())
                        .orElse(0L)
        );
    }

    @Override
    public Page<TodoSearchResponse> searchTodos(Pageable pageable, String keyword, LocalDateTime startDate, LocalDateTime endDate, String nickname) {
        QTodo todo = QTodo.todo;
        QManager manager = QManager.manager;
        QComment comment = QComment.comment;

        BooleanBuilder builder = new BooleanBuilder();

        if (keyword != null && !keyword.isBlank()) {
            builder.and(todo.title.containsIgnoreCase(keyword));
        }
        if (nickname != null && !nickname.isBlank()) {
            builder.and(manager.user.nickname.containsIgnoreCase(nickname));
        }
        if (startDate != null) {
            builder.and(todo.createdAt.goe(startDate));
        }
        if (endDate != null) {
            builder.and(todo.createdAt.loe(endDate));
        }

        List<TodoSearchResponse> result = queryFactory
                .select(new QTodoSearchResponse(
                        todo.title,
                        manager.countDistinct(),
                        comment.countDistinct()
                ))
                .from(todo)
                .leftJoin(manager).on(manager.todo.eq(todo))
                .leftJoin(comment).on(comment.todo.eq(todo))
                .where(builder)
                .groupBy(todo.id)
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(result, pageable, () ->
                Optional.ofNullable(queryFactory
                                .select(todo.count())
                                .from(todo)
                                .leftJoin(manager).on(manager.todo.eq(todo))
                                .leftJoin(comment).on(comment.todo.eq(todo))
                                .where(builder)
                                .fetchOne())
                        .orElse(0L)
        );
    }
}
