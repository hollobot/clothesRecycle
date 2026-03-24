package com.example.project.service;

import com.example.project.exception.BusinessException;
import com.example.project.mapper.ItemImageMapper;
import com.example.project.mapper.ItemMapper;
import com.example.project.mapper.OrderMapper;
import com.example.project.model.dto.item.PublishItemDto;
import com.example.project.model.po.Item;
import com.example.project.model.po.ItemImage;
import com.example.project.model.vo.item.ItemDetailVo;
import com.example.project.service.impl.ItemServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 物品发布与详情逻辑测试。
 */
@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private ItemImageMapper itemImageMapper;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private PointService pointService;

    @Mock
    private MessageService messageService;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void should_save_item_and_images_when_publish() {
        PublishItemDto dto = new PublishItemDto();
        dto.setTitle("测试卫衣");
        dto.setCategory("上衣");
        dto.setGenderType("通用");
        dto.setSizeType("L");
        dto.setConditionLevel("九成新");
        dto.setDescription("几乎未穿");
        dto.setAcquireType("POINT");
        dto.setCampusId(1L);
        dto.setPointPrice(50);
        dto.setImageUrls(List.of("http://img/1.jpg", "http://img/2.jpg"));

        // 模拟数据库自增主键回写，确保后续 item_image 可以使用 itemId。
        doAnswer(invocation -> {
            Item entity = invocation.getArgument(0);
            entity.setId(100L);
            return 1;
        }).when(itemMapper).insert(any(Item.class));

        Long itemId = itemService.publish(88L, dto);

        assertThat(itemId).isEqualTo(100L);

        ArgumentCaptor<Item> itemCaptor = ArgumentCaptor.forClass(Item.class);
        verify(itemMapper).insert(itemCaptor.capture());
        assertThat(itemCaptor.getValue().getCoverUrl()).isEqualTo("http://img/1.jpg");

        ArgumentCaptor<ItemImage> imageCaptor = ArgumentCaptor.forClass(ItemImage.class);
        verify(itemImageMapper, times(2)).insert(imageCaptor.capture());
        List<ItemImage> savedImages = imageCaptor.getAllValues();
        assertThat(savedImages.get(0).getSortNo()).isEqualTo(0);
        assertThat(savedImages.get(1).getSortNo()).isEqualTo(1);
    }

    @Test
    void should_throw_when_publish_without_images() {
        PublishItemDto dto = new PublishItemDto();
        dto.setImageUrls(List.of());

        assertThatThrownBy(() -> itemService.publish(88L, dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage("请至少上传一张物品图片");
    }

    @Test
    void should_return_image_urls_when_query_item_detail() {
        Item item = new Item();
        item.setId(10L);
        item.setTitle("测试外套");
        when(itemMapper.selectById(10L)).thenReturn(item);

        ItemImage image1 = new ItemImage();
        image1.setImageUrl("http://img/a.jpg");
        image1.setSortNo(0);
        ItemImage image2 = new ItemImage();
        image2.setImageUrl("http://img/b.jpg");
        image2.setSortNo(1);
        when(itemImageMapper.selectList(any())).thenReturn(List.of(image1, image2));

        ItemDetailVo detail = itemService.getDetail(10L);

        assertThat(detail.getId()).isEqualTo(10L);
        assertThat(detail.getImageUrls()).containsExactly("http://img/a.jpg", "http://img/b.jpg");
    }

    @Test
    void should_reject_audit_when_campus_admin_operates_other_campus_item() {
        Item item = new Item();
        item.setId(99L);
        item.setCampusId(2L);
        item.setStatus("PENDING_AUDIT");
        when(itemMapper.selectById(99L)).thenReturn(item);

        assertThatThrownBy(() -> itemService.audit(99L, true, "", 7L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("无权限操作其他校区物品");
    }
}
