package com.example.project.service;

import com.example.project.model.dto.gift.SaveGiftDto;
import com.example.project.model.po.Gift;
import com.example.project.model.po.GiftExchange;

import java.util.List;

/**
 * 礼品业务接口。
 */
public interface GiftService {

    List<Gift> listPublicGifts();

    List<Gift> listAdminGifts();

    Long createGift(SaveGiftDto dto);

    void updateGift(Long giftId, SaveGiftDto dto);

    void changeGiftStatus(Long giftId, boolean enabled);

    String exchangeGift(Long userId, Long giftId);

    List<GiftExchange> listMyExchanges(Long userId);

    List<GiftExchange> listAllExchanges();

    void verifyExchange(Long exchangeId);
}
