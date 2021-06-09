package study.querydsl;

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

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;

    @BeforeEach
    public void before() {
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
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QMember m = new QMember("m");

        // when
        Member findByQuerydsl = queryFactory
                .select(m)
                .from(m)
                .where(m.username.eq("member1"))
                .fetchOne();

        // then
        assertThat(findByQuerydsl.getUsername()).isEqualTo("member1");

    }
}
