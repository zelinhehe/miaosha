package com.miaoshaproject.service;

import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.service.model.OrderModel;

public interface OrderService {
    /**
     * 处理秒杀活动下单的两种方式：
     *  1.前端URL上传秒杀活动id，后端下单接口内校验id，判断是否属于对应商品且活动已开始。
     *  2.直接在下单接口内判断商品是否存在秒杀活动，若存在进行中的活动，则以秒杀价下单。
     *  选择第1种方式。why：
     *      业务逻辑上的模型可扩展性：一个商品可能在多个活动中，后端需要前端的URL来判断订单来自哪种终端。所以用第1种
     *      从接口性能讲：第2种中，所有商品（包括没有活动的商品）都需要查询活动信息，影响接口性能。
     */
    OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount) throws BusinessException;
}
