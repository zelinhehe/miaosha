package com.miaoshaproject.service.impl;

import com.miaoshaproject.dao.ItemDOMapper;
import com.miaoshaproject.dao.ItemStockDOMapper;
import com.miaoshaproject.dataobject.ItemDO;
import com.miaoshaproject.dataobject.ItemStockDO;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.service.ItemService;
import com.miaoshaproject.service.PromoService;
import com.miaoshaproject.service.model.ItemModel;
import com.miaoshaproject.service.model.PromoModel;
import com.miaoshaproject.validator.ValidationResult;
import com.miaoshaproject.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ValidatorImpl validator;

    @Autowired
    private ItemDOMapper itemDOMapper;

    @Autowired
    private ItemStockDOMapper itemStockDOMapper;

    @Autowired
    private PromoService promoService;

    private ItemDO convertItemDOFromItemModel(ItemModel itemModel) {
        if (itemModel == null) {
            return null;
        }
        ItemDO itemDO = new ItemDO();
        BeanUtils.copyProperties(itemModel, itemDO);
        return itemDO;
    }

    private ItemStockDO convertItemStockFromItemModel(ItemModel itemModel) {
        if (itemModel == null) {
            return null;
        }
        ItemStockDO itemStockDO = new ItemStockDO();
        itemStockDO.setItemId(itemModel.getId());
        itemStockDO.setStock(itemModel.getStock());
        return itemStockDO;
    }

    @Override
    @Transactional
    public ItemModel createItem(ItemModel itemModel) throws BusinessException {
        // 校验入参
        ValidationResult validationResult = validator.validate(itemModel);
        if (validationResult.isHasErrors()) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, validationResult.getErrMsg());
        }

        // 转化itemmodel -> dataobject
        ItemDO itemDO =convertItemDOFromItemModel(itemModel);

        // 写入数据库
        itemDOMapper.insertSelective(itemDO);
        itemModel.setId(itemDO.getId());

        ItemStockDO itemStockDO = convertItemStockFromItemModel(itemModel);
        itemStockDOMapper.insertSelective(itemStockDO);
        // 返回创建完成的对象


        return getItemById(itemModel.getId());
    }

    @Override
    public List<ItemModel> listItem() {
        List<ItemDO> itemDOList = itemDOMapper.listItem();
        List<ItemModel> itemModelList = itemDOList.stream().map(
                itemDO -> {
                    ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
                    ItemModel itemModel = convertItemModelFromDataObject(itemDO, itemStockDO);
                    return itemModel;
                }
        ).collect(Collectors.toList());
        return itemModelList;
    }

    @Override
    public ItemModel getItemById(Integer id) {
        ItemDO itemDO = itemDOMapper.selectByPrimaryKey(id);
        if (itemDO == null) {
            return null;
        }

        // 操作获取商品库存数量
        ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());

        // 将 dataobject -> model
        ItemModel itemModel = convertItemModelFromDataObject(itemDO, itemStockDO);

        // 获取商品活动信息
        PromoModel promoModel = promoService.getPromoByItemId(id);
        if (promoModel != null && promoModel.getStatus() != 3) {
            itemModel.setPromoModel(promoModel);
        }
        return itemModel;
    }

    @Override
    public boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException {
        int affectedRow = itemStockDOMapper.decreaseStock(itemId, amount);
        if (affectedRow > 0) {
            // 更新库存成功
            return true;
        } else {
            // 更新库存失败
            return false;
        }
    }

    @Override
    @Transactional
    public void increaseSales(Integer itemId, Integer amount) throws BusinessException {
        itemDOMapper.increaseSales(itemId, amount);
    }

    private ItemModel convertItemModelFromDataObject(ItemDO itemDO, ItemStockDO itemStockDO) {
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemDO, itemModel);
        itemModel.setStock(itemStockDO.getStock());
        return itemModel;
    }
}
