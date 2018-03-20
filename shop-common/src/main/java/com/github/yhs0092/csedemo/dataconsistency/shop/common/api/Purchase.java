package com.github.yhs0092.csedemo.dataconsistency.shop.common.api;

public class Purchase {
  private Long id;

  private Long accountId;

  private Long goodsId;

  private Long totalPrice;

  private Integer quantity;

  /**
   * TCC/2PC
   */
  private String purchaseType;

  /**
   * CONFIRMED/CANCELLED/UNKNOWN
   */
  private String status = STATUS_UNKNOWN;

  public static final String PURCHASE_TYPE_TCC = "TCC";

  public static final String PURCHASE_TYPE_2PC = "2PC";

  public static final String STATUS_CONFIRMED = "confirmed";

  public static final String STATUS_CANCELLED = "cancelled";

  public static final String STATUS_UNKNOWN = "unknown";


  public Long getId() {
    return id;
  }

  public Purchase setId(Long id) {
    this.id = id;
    return this;
  }

  public Long getAccountId() {
    return accountId;
  }

  public Purchase setAccountId(Long accountId) {
    this.accountId = accountId;
    return this;
  }

  public Long getGoodsId() {
    return goodsId;
  }

  public Purchase setGoodsId(Long goodsId) {
    this.goodsId = goodsId;
    return this;
  }

  public Long getTotalPrice() {
    return totalPrice;
  }

  public Purchase setTotalPrice(Long totalPrice) {
    this.totalPrice = totalPrice;
    return this;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public Purchase setQuantity(Integer quantity) {
    this.quantity = quantity;
    return this;
  }

  public String getPurchaseType() {
    return purchaseType;
  }

  public Purchase setPurchaseType(String purchaseType) {
    this.purchaseType = purchaseType;
    return this;
  }

  public String getStatus() {
    return status;
  }

  public Purchase setStatus(String status) {
    this.status = status;
    return this;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Purchase{");
    sb.append("id=").append(id);
    sb.append(", accountId=").append(accountId);
    sb.append(", goodsId=").append(goodsId);
    sb.append(", totalPrice=").append(totalPrice);
    sb.append(", quantity=").append(quantity);
    sb.append(", purchaseType='").append(purchaseType).append('\'');
    sb.append(", status='").append(status).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
