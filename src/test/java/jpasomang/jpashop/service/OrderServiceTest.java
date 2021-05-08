package jpasomang.jpashop.service;

import jpasomang.jpashop.domain.Address;
import jpasomang.jpashop.domain.Member;
import jpasomang.jpashop.domain.Order;
import jpasomang.jpashop.domain.OrderStatus;
import jpasomang.jpashop.domain.item.Item;
import jpasomang.jpashop.domain.item.Movie;
import jpasomang.jpashop.exception.NotEnoughStockException;
import jpasomang.jpashop.repository.OrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {


    @Autowired
    EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;


    @Test
    public void 상품주문() throws Exception {
        //given
        Member member = createMebmer("SOMANG");


        Item movie = createMovie("TENET", 10_000, 10);

        //when
        int orderCount = 2;
        Long order = orderService.order(member.getId(), movie.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(order);

        assertEquals("상품 주문시 상태는 ORDER", OrderStatus.ORDER, getOrder.getStatus());
        assertEquals("주문한 상품 종류 수가 정확해야 한다", 1, getOrder.getOrderItems().size());
        assertEquals("주문 가격은 가격 * 수량이다", movie.getPrice() * orderCount, getOrder.getTotalPrice());
        assertEquals("주문 수량만큼 재고가 줄어야 한다", 8, movie.getStockQuantity());
    }



    @Test(expected = NotEnoughStockException.class)
    public void 상품주문_재고수량초과() throws Exception {
        //given
        Member member = createMebmer("SOMANG");
        Item movie = createMovie("TENET", 10_000, 100_000);

        //when
        int orderCount = 100_001;
        orderService.order(member.getId(), movie.getId(), orderCount);

        //then
        fail("재고 수량 부족 예외가 발생해야 한다.");

    }

    @Test
    public void 주문취소() throws Exception {
        //given
        Member member = createMebmer("SOMANG");
        Item movie = createMovie("TENET", 12_000, 10);

        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), movie.getId(), orderCount);

        //when
        orderService.cancelOrder(orderId);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("주문을 취소하면 주문상태가 CANCEL 이 되야한다.", OrderStatus.CANCEL, getOrder.getStatus());
        assertEquals("주문을 취소하면 재고가 원래대로 돌아가야 한다", 10, movie.getStockQuantity());

    }



    private Item createMovie(String name, int price, int stockQuantity) {
        Item movie = new Movie();
        movie.setName(name);
        movie.setPrice(price);
        movie.setStockQuantity(stockQuantity);
        em.persist(movie);
        return movie;
    }

    private Member createMebmer(String name) {
        Member member = new Member();
        member.setName(name);
        member.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(member);
        return member;
    }
}
