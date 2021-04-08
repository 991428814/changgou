package com.changgou.goods.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * @author chx
 * @version 1.0
 * @description: TODO
 * @date 2020/10/18 0018 15:58
 */
public class Goods implements Serializable {
    private Spu spu;
    private List<Sku>skuList;

    public Spu getSpu() {
        return spu;
    }

    public void setSpu(Spu spu) {
        this.spu = spu;
    }

    public List<Sku> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<Sku> skuList) {
        this.skuList = skuList;
    }
}