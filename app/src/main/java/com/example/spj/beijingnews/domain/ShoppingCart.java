package com.example.spj.beijingnews.domain;

import java.io.Serializable;

/**
 *
 * 作用：购物车类继承Wares，记录某个商品在购物车中的状态，例如有多少个商品，是否选中
 */
public class ShoppingCart extends SmartServicePagerBean.Wares implements Serializable{

    /**
     * 购买的数量
     */
    private int count = 1;

    /**
     * 是否勾选
     */
    private boolean isCheck = true;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    @Override
    public String toString() {
        return "ShoppingCart{" +
                "count=" + count +
                ", isCheck=" + isCheck +
                '}';
    }
}
