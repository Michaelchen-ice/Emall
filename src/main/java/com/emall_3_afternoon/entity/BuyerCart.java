package com.emall_3_afternoon.entity;

import com.emall_3_afternoon.mapper.Goods_InfoMapper;
import com.emall_3_afternoon.mapper.Store_InfoMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

//购物车。把store_item和goods_item打包后。放入到Cart里面来。形成购物车。
//其实不用写这个购物车类，直接显示StoreList形式即可。
//但是老师诚恳的告诉你们，我不知道list形式如何序列化。单个对象我知道。
public class BuyerCart {

    private List<Store_Item> store_itemList;
//    private List<Integer> good_itemList;

    public BuyerCart() {
        this.store_itemList = new ArrayList<>();
//        this.good_itemList = new ArrayList<>();
    }

//    public List<Integer> getGood_itemList() {
//        return good_itemList;
//    }

//    public void setGood_itemList(List<Integer> good_itemList) {
//        this.good_itemList = good_itemList;
//    }

    public List<Store_Item> getStore_itemList() {
        return store_itemList;
    }

    public void setStore_itemList(List<Store_Item> store_itemList) {
        this.store_itemList = store_itemList;
    }

    public void addStore_Item(Store_Item store_item) {
        store_itemList.add(store_item);
    }

    public int findStore(int store_id) {
        int store_index = -1;
        int i = 0;
        if (store_itemList != null && !store_itemList.isEmpty()) {
            for (Store_Item item : this.store_itemList) {
                if (item.getStore_id() == store_id) {
                    store_index = i;
                    break;
                }
                i++;
            }
        } else {
            return -1;
        }
        return store_index;
    }

    public int findGood(int goods_id) {
        int good_index = -1;
        int i = 0;
        int flag = 0;
        if (store_itemList != null && !store_itemList.isEmpty()) {
            for (Store_Item item : this.store_itemList) {
                for (Goods_Item goods_item : item.getGoods_itemList()) {
                    if (goods_item.getGoods_info().getGoods_id() == goods_id) {
                        good_index = i;
                        flag = 1;
                        break;
                    }
                    i++;
                }
                if (flag == 1) {
                    break;
                }
                good_index = -1;
                i = 0;
            }
        } else {
            return -1;
        }
        return good_index;
    }

    public void addStore_Item(Goods_Info goods_info, Store_Info store_info, int goods_sum) {
        int goods_item_index = -1;
        int store_item_index = -1;

        //1:查找是否存在该店铺,如果店铺不存在，商品肯定不存在。
        store_item_index = this.findStore(goods_info.getStore_id());
        goods_item_index = this.findGood(goods_info.getGoods_id());
        if (store_item_index == -1) {//商品不存在，店铺也不存在，则添加店铺再添加商品
            //构造一个商品对象
            Goods_Item goods_item = new Goods_Item();
            goods_item.setGoods_info(goods_info); //设置商品
            goods_item.setAccount(goods_sum);
            goods_item.setGoods_money(goods_sum * goods_info.getGoods_actual_price());
            //构造一个店铺对象
            Store_Item store_item = new Store_Item();
            store_item.setStore_id(store_info.getStore_id());
            store_item.setStore_name(store_info.getStore_name());
            store_item.setLink_man_id(store_info.getLink_man_id());

            //把商品加入到店铺
            store_item.AddGoods_Item(goods_item);
            //把店铺加入到购物车。
            this.store_itemList.add(store_item);
        } else if (store_item_index >= 0) {
            if (goods_item_index == -1) {
                //说明店铺存在，但是商品不存在，则应该则该店铺下添加该商品
                Goods_Item goods_item = new Goods_Item();
                goods_item.setGoods_info(goods_info); //设置商品
                goods_item.setAccount(goods_sum);
                goods_item.setGoods_money(goods_sum * goods_info.getGoods_actual_price());
                //把商品加入到店铺，店铺本身就在购物车，所以不需要把店铺加入到购物车
                this.store_itemList.get(store_item_index).AddGoods_Item(goods_item);
            } else {//店铺存在，商品也存在
                //this.store_itemList.get(store_item_index).getGoods_itemList().get(goods_item_index)
                //找到店铺，找到商品
                this.store_itemList.get(store_item_index).getGoods_itemList().get(goods_item_index).
                        setAccount(goods_sum + this.store_itemList.get(store_item_index).
                                getGoods_itemList().
                                get(goods_item_index).getAccount()); //设置数量=原来的数量+新增加的数量
                this.store_itemList.get(store_item_index).getGoods_itemList().get(goods_item_index).
                        setGoods_money(goods_sum * goods_info.getGoods_actual_price() + this.store_itemList.get(store_item_index).
                                getGoods_itemList().
                                get(goods_item_index).getGoods_money()); //设置金额=原来的金额+新增加的金额
            }
        }
    }

    public void deleteStore_Item(int goods_id, int store_id) {
        int store_item_index = this.findStore(store_id);
        int goods_item_index = this.findGood(goods_id);
        //1:店铺下只有一种商品。则删除店铺即可
        if (this.store_itemList.get(store_item_index).getGoods_itemList().size() == 1) {
            this.store_itemList.remove(store_item_index);
        } else {
            //2:店铺下有多种商品，则需要删除商品，店铺不能删除
            this.store_itemList.get(store_item_index).getGoods_itemList().remove(goods_item_index);
        }
    }

    //修改肯定是走ajax，而且肯定存在。如果不存在，则没有修改的意义
    public void updateStore_Item(Goods_Info goods_info, Store_Info store_info, int goods_sum) {
        int store_item_index = this.findStore(goods_info.getStore_id());
        int goods_item_index = this.store_itemList.get(store_item_index).findGood_Item(goods_info.getGoods_id());
        this.store_itemList.get(store_item_index).getGoods_itemList().get(goods_item_index)
                .setAccount(goods_sum);
        this.store_itemList.get(store_item_index).getGoods_itemList().get(goods_item_index)
                .setGoods_money(goods_sum * goods_info.getGoods_actual_price());
    }


}
