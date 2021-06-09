package study.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.controller.entity.Member;
import study.querydsl.controller.entity.QMember;
import study.querydsl.controller.entity.Team;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static study.querydsl.controller.entity.QMember.*;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);

        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4); 
    }
    
    @Test
    @DisplayName("JPQL 테스트")
    void startJPQL() throws Exception {
        // given
        // 멤버 1을 찾아라
        Member findByJPQL = em.createQuery("select m From Member m " +
                "where m.username = :username", Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        // when
        
        // then
        assertThat(findByJPQL.getUsername()).isEqualTo("member1");
        
    }

    @Test
    @DisplayName("QueryDSL 테스트")
    void startQuerydsl() throws Exception {
        // given
//        QMember m = new QMember("m"); // 별칭 직접 지정

        // when
        Member findByQuerydsl = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        // then
        assertThat(findByQuerydsl.getUsername()).isEqualTo("member1");

    }
    
    @Test
    @DisplayName("기본 검색 쿼리 테스트")
    void search() throws Exception {
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1")
                        .and(member.age.eq(10)))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
        assertThat(findMember.getAge()).isEqualTo(10);
    }

    @Test
    @DisplayName("검색 쿼리 - AND 조건 파라미터 처리 테스트")
    void searchAndParam () throws Exception {
        Member findMember = queryFactory
                .selectFrom(member)
                .where(
                        member.username.eq("member1"),
                        member.age.eq(10)
                )
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
        assertThat(findMember.getAge()).isEqualTo(10);
    }
    
    @Test
    @DisplayName("결과 조회 - 리스트 조회")
    void resultFetch() throws Exception {
        // 리스트
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .fetch();

        // 단 건
        Member fetchOne = queryFactory
                .selectFrom(member)
                .fetchOne();

        // 처음 한 건 조회
        Member fetchFirst = queryFactory
                .selectFrom(member)
                .fetchFirst();

        // 페이징에서 사용
        QueryResults<Member> results = queryFactory
                .selectFrom(member)
                .fetchResults();

        results.getTotal();
        List<Member> content = results.getResults();

        // count 쿼리로 변경
        long count = queryFactory.selectFrom(member)
                .fetchCount();

    }
}
