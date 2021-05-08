package jpasomang.jpashop.service;


import jpasomang.jpashop.domain.item.Item;
import jpasomang.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    /*
        아이템 저장 메소드
     */
    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    //모든 아이템 조회
    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    //아이템 하나 찾기
    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }
}
