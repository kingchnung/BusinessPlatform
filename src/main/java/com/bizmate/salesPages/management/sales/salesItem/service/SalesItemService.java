package com.bizmate.salesPages.management.sales.salesItem.service;

import com.bizmate.salesPages.management.sales.salesItem.domain.SalesItem;

import java.util.List;

public interface SalesItemService {
    public List<SalesItem> salesItemList(SalesItem salesItem);
    public SalesItem salesItemInsert(SalesItem salesItem);
    public SalesItem salesItemUpdate(SalesItem salesItem);
    public void salesItemDelete(SalesItem salesItem);
}
